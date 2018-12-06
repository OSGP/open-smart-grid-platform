/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AlarmHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

public class DlmsPushNotificationDecoder extends ReplayingDecoder<DlmsPushNotificationDecoder.DecodingState> {

    private static final int EQUIPMENT_IDENTIFIER_LENGTH = 17;
    private static final int NUMBER_OF_BYTES_FOR_ALARM = 4;
    private static final int NUMBER_OF_BYTES_FOR_LOGICAL_NAME = 6;

    private static final byte[] SMS_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x03, 0x00, (byte) 0xFF };
    private static final byte[] CSD_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x02, 0x00, (byte) 0xFF };
    private static final byte[] SCHEDULER_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x0F, 0x00, 0x04, (byte) 0xFF };

    private static final String PUSH_SCHEDULER_TRIGGER = "Push scheduler";
    private static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";
    private static final String PUSH_CDS_TRIGGER = "Push cds wakeup";
    private static final String PUSH_SMS_TRIGGER = "Push sms wakeup";

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsPushNotificationDecoder.class);

    /**
     * The elements inside the DLMS Push notification (Alarm or Wakeup SMS are
     * expressed in bytes separated by a comma (byte 0x2C).
     */
    private static final byte COMMA = 0x2C;

    public enum DecodingState {
        EQUIPMENT_IDENTIFIER,
        DATA_OBJECT;
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

        LOGGER.info("Decoding state: {}", state);

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

    private void decodeEquipmentIdentifier(final ChannelBuffer buffer) throws UnrecognizedMessageDataException {
        final byte[] equipmentIdentifierPlusSeparatorBytes = new byte[EQUIPMENT_IDENTIFIER_LENGTH + 1];
        buffer.readBytes(equipmentIdentifierPlusSeparatorBytes, 0, equipmentIdentifierPlusSeparatorBytes.length);

        if (equipmentIdentifierPlusSeparatorBytes[EQUIPMENT_IDENTIFIER_LENGTH] != COMMA) {
            throw new UnrecognizedMessageDataException("message must start with " + EQUIPMENT_IDENTIFIER_LENGTH
                    + " bytes for the equipment identifier, followed by byte 0x2C (a comma).");
        }

        final byte[] equipmentIdentifierBytes = Arrays.copyOfRange(equipmentIdentifierPlusSeparatorBytes, 0,
                EQUIPMENT_IDENTIFIER_LENGTH);
        final String equipmentIdentifier = new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII);
        this.builder.withEquipmentIdentifier(equipmentIdentifier);
        this.builder.appendBytes(equipmentIdentifierPlusSeparatorBytes);
    }

    private void decodeReceivedData(final ChannelBuffer buffer) throws UnrecognizedMessageDataException {
        // SLIM-1711 Is a very weird bug, where readableBytes turns out to be almost MAXINT
        // Seems like BigEndianHeapChannelBuffer has some kind of overflow/underflow.
        final int readableBytes = buffer.writerIndex() - buffer.readerIndex();
        if (readableBytes > Math.max(NUMBER_OF_BYTES_FOR_ALARM, NUMBER_OF_BYTES_FOR_LOGICAL_NAME)) {
            throw new UnrecognizedMessageDataException("length of data bytes is not " + NUMBER_OF_BYTES_FOR_ALARM
                    + " (alarm) or " + NUMBER_OF_BYTES_FOR_LOGICAL_NAME + " (obiscode)");
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
            this.builder.withTriggerType(PUSH_CDS_TRIGGER);
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
