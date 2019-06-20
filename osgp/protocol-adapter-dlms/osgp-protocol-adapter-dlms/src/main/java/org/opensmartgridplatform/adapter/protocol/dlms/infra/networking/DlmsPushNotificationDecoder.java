/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.AlarmHelperService;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DlmsPushNotificationDecoder extends ReplayingDecoder<DlmsPushNotificationDecoder.DecodingState> {

    private static final int EQUIPMENT_IDENTIFIER_LENGTH = 17;
    private static final int NUMBER_OF_BYTES_FOR_ALARM = 4;
    private static final int NUMBER_OF_BYTES_FOR_LOGICAL_NAME = 6;

    private static final int SMR5_NUMBER_OF_BYTES_FOR_ADDRESSING = 8;
    private static final int SMR5_NUMBER_OF_BYTES_FOR_INVOKE_ID = 4;

    private static final byte[] SMS_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x03, 0x00, (byte) 0xFF };
    private static final byte[] CSD_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x02, 0x00, (byte) 0xFF };
    private static final byte[] SCHEDULER_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x0F, 0x00, 0x04, (byte) 0xFF };

    private static final String PUSH_SCHEDULER_TRIGGER = "Push scheduler";
    private static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";
    private static final String PUSH_CSD_TRIGGER = "Push csd wakeup";
    private static final String PUSH_SMS_TRIGGER = "Push sms wakeup";

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsPushNotificationDecoder.class);

    /**
     * The elements inside the DSMR4 DLMS Push notification (Alarm or Wakeup SMS are
     * expressed in bytes separated by a comma (byte 0x2C).
     */
    private static final byte COMMA = 0x2C;

    // Dlms data-types.
    private static final byte STRUCTURE = 0x02;
    private static final byte OCTET_STRING = 0x09;
    private static final byte DOUBLE_LONG_UNSIGNED = 0x06;

    public enum DecodingState {
        EQUIPMENT_IDENTIFIER, DATA_OBJECT
    }

    private DlmsPushNotification.Builder builder;

    public DlmsPushNotificationDecoder() {
        LOGGER.debug("Created new DLMS Push Notification decoder");
        this.reset();
    }

    private void reset() {
        this.checkpoint(DecodingState.EQUIPMENT_IDENTIFIER);
        this.builder = new DlmsPushNotification.Builder();
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer,
            final DecodingState state) throws UnknownDecodingStateException, UnrecognizedMessageDataException {
        /**
         *  DSMR4 alarm examples (in HEX bytes):
         *
         *  45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32       // Equipment id EXXXX123456789012
         *  2C                                                       // Comma
         *  00 00 0F 00 04 FF                                        // Logical name 0.0.15.0.4.255
         *
         *  45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32       // Equipment id EXXXX123456789012
         *  2C                                                       // Comma
         *  00 00 00 02                                              // Alarm register, with Replace battery set
         *
         *  SMR5 alarm examples (in HEX bytes):
         *
         *  0F                                                       // Data-notification
         *  00 00 00 01                                              // Long-invoke-id-and-priority (can be ignored)
         *  00                                                       // Date-time (empty)
         *  02 02                                                    // Data-value: structure with 2 elements
         *  09 11 45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32 // Equipment id EXXXX123456789012
         *  09 06 00 00 19 09 00 FF                                  // Logical name: Push setup schedule
         *
         *  0F                                                       // Data-notification
         *  00 00 00 01                                              // Long-invoke-id-and-priority (can be ignored)
         *  00                                                       // Date-time (empty)
         *  02 03                                                    // Data-value: structure with 3 elements
         *  09 11 45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32 // Equipment id EXXXX123456789012
         *  09 06 00 01 19 09 00 FF                                  // Logical name: Push setup alarms
         *  06 00 00 00 02                                           // Alarm register, with Replace battery set
         *
         *  Notes:
         *  - For SMR5 alarms, we get 8 additional addressing bytes in front of the alarm. These bytes can be
         *  ignored here.
         *  - To check if the alarm is in DSMR4 or SMR5 format, check the 9th byte (at index 8). If it is 0F, then it
         *  is SMR5, otherwise it is DSMR4, because the 9th byte in DSMR4 is in the identifier and this should be a
         *  number or a character, so it can't be ASCII code 0F.
         * */

        boolean smr5alarm = buffer.getByte(8) == 0x0F;

        LOGGER.info("Decoding state: {}, SMR5 alarm: {}", state, smr5alarm);

        if (smr5alarm) {
            return decodeSmr5alarm(buffer);
        } else {
            switch (state) {
            case EQUIPMENT_IDENTIFIER:
                this.decodeEquipmentIdentifier(buffer);
                return this.setCheckpointAndContinueDecode(ctx, channel, buffer, DecodingState.DATA_OBJECT);
            case DATA_OBJECT:
                this.decodeReceivedData(buffer);
                return this.buildPushNotification();
            default:
                throw new UnknownDecodingStateException(String.valueOf(state));
            }
        }
    }

    private Object decodeSmr5alarm(final ChannelBuffer buffer) throws UnrecognizedMessageDataException {

        // Skip addressing, the 0x0F byte and the invoke-id-and-priority bytes
        buffer.skipBytes(SMR5_NUMBER_OF_BYTES_FOR_ADDRESSING + 1 + SMR5_NUMBER_OF_BYTES_FOR_INVOKE_ID);

        // Datetime is not used, so can be skipped as well
        skipDateTime(buffer);

        // Next byte should indicate a Structure
        byte dataTypeByte = buffer.readByte();
        if (dataTypeByte != STRUCTURE) {
            throw new UnrecognizedMessageDataException("Expected a structure (0x02), but encountered: " + dataTypeByte);
        }
        this.builder.appendByte(dataTypeByte);

        // Next byte should indicate the amount of elements in the structure: 2 or 3
        byte dataLength = buffer.readByte();
        if (dataLength != 2 && dataLength != 3) {
            throw new UnrecognizedMessageDataException("Expected a structure with 2 or 3 elements, but amount is " + dataLength);
        }
        this.builder.appendByte(dataLength);

        decodeEquipmentIdentifierSmr5(buffer);
        decodeLogicalNameSmr5(buffer);

        if (dataLength == 3) {
            decodeAlarmsSmr5(buffer);
        }

        return this.buildPushNotification();
    }

    private void skipDateTime(final ChannelBuffer buffer) {

        // The first byte of the datetime is the length byte
        byte dateTimeLength = buffer.readByte();

        // Skip the rest of the datetime bytes
        buffer.skipBytes(dateTimeLength);
    }

    private void decodeEquipmentIdentifierSmr5(final ChannelBuffer buffer) throws UnrecognizedMessageDataException {
        // First byte should indicate octet-string
        byte dataTypeByte = buffer.readByte();
        if (dataTypeByte != OCTET_STRING) {
            throw new UnrecognizedMessageDataException("Expected an octet-string (0x09), but encountered: " + dataTypeByte);
        }
        this.builder.appendByte(dataTypeByte);

        // Next byte should be the length of the octet-string
        byte dataLength = buffer.readByte();
        if (dataLength != EQUIPMENT_IDENTIFIER_LENGTH) {
            throw new UnrecognizedMessageDataException("Expected an identifier with length " + EQUIPMENT_IDENTIFIER_LENGTH +
                    ", but specified length is: " + dataLength);
        }
        this.builder.appendByte(dataLength);

        final byte[] equipmentIdentifierBytes = new byte[dataLength];
        buffer.readBytes(equipmentIdentifierBytes, 0, equipmentIdentifierBytes.length);

        this.builder.withEquipmentIdentifier(new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII));
        this.builder.appendBytes(equipmentIdentifierBytes);
    }

    private void decodeLogicalNameSmr5(final ChannelBuffer buffer) throws UnrecognizedMessageDataException {
        // First byte should indicate octet-string
        byte dataTypeByte = buffer.readByte();
        if (dataTypeByte != OCTET_STRING) {
            throw new UnrecognizedMessageDataException("Expected an octet-string (0x09), but encountered: " + dataTypeByte);
        }
        this.builder.appendByte(dataTypeByte);

        // Next byte should be the length of the octet-string
        byte dataLength = buffer.readByte();
        if (dataLength != NUMBER_OF_BYTES_FOR_LOGICAL_NAME) {
            throw new UnrecognizedMessageDataException("Expected a logical name with length " + NUMBER_OF_BYTES_FOR_LOGICAL_NAME +
                    ", but specified length is: " + dataLength);
        }
        this.builder.appendByte(dataLength);

        // Next bytes are logical name
        decodeObisCodeData(buffer);
    }

    private void decodeAlarmsSmr5(final ChannelBuffer buffer) throws UnrecognizedMessageDataException {
        // First byte should indicate double-long-unsigned
        byte dataTypeByte = buffer.readByte();
        if (dataTypeByte != DOUBLE_LONG_UNSIGNED) {
            throw new UnrecognizedMessageDataException("Expected a double-long-unsigned (0x06), but encountered: " + dataTypeByte);
        }
        this.builder.appendByte(dataTypeByte);

        // Next bytes are the alarm
        decodeAlarmRegisterData(buffer);
    }

    private void decodeEquipmentIdentifier(final ChannelBuffer buffer) throws UnrecognizedMessageDataException {
        final byte[] equipmentIdentifierPlusSeparatorBytes = new byte[EQUIPMENT_IDENTIFIER_LENGTH + 1];
        buffer.readBytes(equipmentIdentifierPlusSeparatorBytes, 0, equipmentIdentifierPlusSeparatorBytes.length);

        if (equipmentIdentifierPlusSeparatorBytes[EQUIPMENT_IDENTIFIER_LENGTH] != COMMA) {
            throw new UnrecognizedMessageDataException("message must start with " + EQUIPMENT_IDENTIFIER_LENGTH
                    + " bytes for the equipment identifier, followed by byte 0x2C (a comma).");
        }

        final byte[] equipmentIdentifierBytes = Arrays
                .copyOfRange(equipmentIdentifierPlusSeparatorBytes, 0, EQUIPMENT_IDENTIFIER_LENGTH);
        final String equipmentIdentifier = new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII);
        this.builder.withEquipmentIdentifier(equipmentIdentifier);
        this.builder.appendBytes(equipmentIdentifierPlusSeparatorBytes);
    }

    private void decodeReceivedData(final ChannelBuffer buffer) throws UnrecognizedMessageDataException {
        // SLIM-1711 Is a very weird bug, where readableBytes turns out to be almost MAXINT
        // Seems like BigEndianHeapChannelBuffer has some kind of overflow/underflow.
        final int readableBytes = buffer.writerIndex() - buffer.readerIndex();
        if (readableBytes > Math.max(NUMBER_OF_BYTES_FOR_ALARM, NUMBER_OF_BYTES_FOR_LOGICAL_NAME)) {
            throw new UnrecognizedMessageDataException(
                    "length of data bytes is not " + NUMBER_OF_BYTES_FOR_ALARM + " (alarm) or "
                            + NUMBER_OF_BYTES_FOR_LOGICAL_NAME + " (obiscode)");
        }

        if (readableBytes == NUMBER_OF_BYTES_FOR_ALARM) {
            this.decodeAlarmRegisterData(buffer);
        } else if (readableBytes == NUMBER_OF_BYTES_FOR_LOGICAL_NAME) {
            this.decodeObisCodeData(buffer);
        } else {
            this.waitForMoreBytes(buffer);
        }
    }

    private Object setCheckpointAndContinueDecode(final ChannelHandlerContext ctx, final Channel channel,
            final ChannelBuffer buffer, final DecodingState nextState)
            throws UnknownDecodingStateException, UnrecognizedMessageDataException {
        this.checkpoint(nextState);
        return this.decode(ctx, channel, buffer, nextState);
    }

    private DlmsPushNotification buildPushNotification() {
        try {
            return this.builder.build();
        } finally {
            this.reset();
        }
    }

    private void decodeObisCodeData(final ChannelBuffer buffer) {

        final byte[] logicalNameBytes = new byte[NUMBER_OF_BYTES_FOR_LOGICAL_NAME];
        buffer.readBytes(logicalNameBytes, 0, NUMBER_OF_BYTES_FOR_LOGICAL_NAME);

        if (Arrays.equals(SMS_OBISCODE_BYTES, logicalNameBytes)) {
            this.builder.withTriggerType(PUSH_SMS_TRIGGER);
        } else if (Arrays.equals(CSD_OBISCODE_BYTES, logicalNameBytes)) {
            LOGGER.warn("CSD Push notification not supported");
            this.builder.withTriggerType(PUSH_CSD_TRIGGER);
        } else if (Arrays.equals(SCHEDULER_OBISCODE_BYTES, logicalNameBytes)) {
            LOGGER.warn("Scheduler Push notification not supported");
            this.builder.withTriggerType(PUSH_SCHEDULER_TRIGGER);
        } else {
            LOGGER.warn("Unknown Push notification not supported. Unable to decode");
            this.builder.withTriggerType("");
        }

        this.builder.withAlarms(null);
        this.builder.appendBytes(logicalNameBytes);
    }

    private void decodeAlarmRegisterData(final ChannelBuffer buffer) {

        final byte[] alarmBytes = new byte[NUMBER_OF_BYTES_FOR_ALARM];
        buffer.readBytes(alarmBytes, 0, NUMBER_OF_BYTES_FOR_ALARM);

        final Set<AlarmTypeDto> alarms = new AlarmHelperService().toAlarmTypes(ByteBuffer.wrap(alarmBytes).getInt());

        this.builder.withTriggerType(PUSH_ALARM_TRIGGER);
        this.builder.withAlarms(alarms);
        this.builder.appendBytes(alarmBytes);
    }

    private void waitForMoreBytes(final ChannelBuffer buffer) {
        /*
         * Trigger the REPLAY error from the ReplayingDecoderBuffer which is
         * used as ChannelBuffer implementation with the ReplayingDecoder.
         *
         * This should make sure decode gets reinvoked properly until enough
         * bytes are available.
         */
        buffer.skipBytes(Integer.MAX_VALUE);
    }

}
