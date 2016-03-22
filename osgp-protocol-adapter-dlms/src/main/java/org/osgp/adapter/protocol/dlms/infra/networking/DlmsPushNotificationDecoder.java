/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.networking;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.osgp.adapter.protocol.dlms.domain.commands.AlarmHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsPushNotification;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmTypeDto;

public class DlmsPushNotificationDecoder extends ReplayingDecoder<DlmsPushNotificationDecoder.DecodingState> {

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

    public static enum DecodingState {
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

        LOGGER.info("Decoding state: {}", state.toString());

        if (this.getSizeBytePattern(buffer) <= 0) {
            // appears that an new state is passed even if all data in the
            // channel buffer is processed
            // return an empty object, which is discarded in de
            // DlmsChannelHandlerServer
            this.builder = new DlmsPushNotification.Builder();
            this.builder.withEquipmentIdentifier("");
            this.builder.appendBytes(new byte[] { COMMA });
            this.builder.withTriggerType("");
            return this.buildPushNotification();
        }

        switch (state) {
        case EQUIPMENT_IDENTIFIER:
            this.decodeEquipmentIdentifier(buffer);
            return this.setCheckpointAndContinueDecode(ctx, channel, buffer, DecodingState.DATA_OBJECT);
        case DATA_OBJECT:
            this.decodeReceivedData(buffer);
            return this.buildPushNotification();
        default:
            throw new UnknownDecodingStateException(state.name());
        }
    }

    private void decodeEquipmentIdentifier(final ChannelBuffer buffer) {
        final byte[] pattern = this.getBytePattern(buffer);
        final int separator = this.getFirstIndexOf(pattern, COMMA);

        final byte[] equipmentIdentifierBytes = Arrays.copyOfRange(pattern, 0, separator);
        final String equipmentIdentifier = new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII);
        this.builder.withEquipmentIdentifier(equipmentIdentifier);
        this.builder.appendBytes(equipmentIdentifierBytes).appendByte(COMMA);

        // extract equipment identifier from buffer (the decoder must consume
        // bytes!)
        buffer.readBytes(equipmentIdentifierBytes.length);
    }

    private void decodeReceivedData(final ChannelBuffer buffer) throws UnrecognizedMessageDataException {
        final byte[] pattern = this.getBytePattern(buffer);
        final int separator = this.getFirstIndexOf(pattern, COMMA);

        final byte[] dataBytes = Arrays.copyOfRange(pattern, separator + 1, pattern.length);
        if (dataBytes.length == 4) {
            this.decodeAlarmRegisterData(dataBytes);
        } else if (dataBytes.length == 6) {
            this.decodeObiscodeData(dataBytes);
        } else {
            LOGGER.info("Unrecognized length of data bytes in message. length is {}", dataBytes.length);
            throw new UnrecognizedMessageDataException("length of data bytes is not 4 (alarm) or 6 (obiscode)");
        }
        this.builder.appendBytes(dataBytes);

        // extract data bytes and separator from buffer (the decoder must
        // consume bytes!)
        buffer.readBytes(dataBytes.length + 1);
    }

    private byte[] getBytePattern(final ChannelBuffer buffer) {
        final int startIndex = buffer.readerIndex();
        final int endIndex = buffer.writerIndex();
        final byte[] pattern = new byte[endIndex - startIndex];
        buffer.getBytes(buffer.readerIndex(), pattern);
        return pattern;
    }

    private int getFirstIndexOf(final byte[] dataBytes, final byte searchByte) {
        int i = -1;
        for (int index = 0; index < dataBytes.length; index++) {
            if (dataBytes[index] == searchByte) {
                i = index;
                break;
            }
        }
        return i;
    }

    private int getSizeBytePattern(final ChannelBuffer buffer) {
        final int startIndex = buffer.readerIndex();
        final int endIndex = buffer.writerIndex();
        return endIndex - startIndex;
    }

    private Object setCheckpointAndContinueDecode(final ChannelHandlerContext ctx, final Channel channel,
            final ChannelBuffer buffer, final DecodingState nextState) throws UnknownDecodingStateException,
            UnrecognizedMessageDataException {
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

    private void decodeObiscodeData(final byte[] dataBytes) {

        if (Arrays.equals(SMS_OBISCODE_BYTES, dataBytes)) {

            this.builder.withTriggerType(PUSH_SMS_TRIGGER);
        } else if (Arrays.equals(CSD_OBISCODE_BYTES, dataBytes)) {
            LOGGER.warn("CSD Push notification not supported");
            this.builder.withTriggerType(PUSH_CDS_TRIGGER);
        } else if (Arrays.equals(SCHEDULER_OBISCODE_BYTES, dataBytes)) {
            LOGGER.warn("Scheduler Push notification not supported");
            this.builder.withTriggerType(PUSH_SCHEDULER_TRIGGER);
        } else {
            LOGGER.warn("Unknown Push notification not supported. Unable to decode");
            this.builder.withTriggerType("");
        }

        this.builder.withAlarms(null);
    }

    private void decodeAlarmRegisterData(final byte[] dataBytes) {
        long registerValue = 0;
        for (final byte b : dataBytes) {
            registerValue = registerValue << 8;
            final int unsignedValue = b & 0xFF;
            registerValue = registerValue + unsignedValue;
        }
        this.builder.withTriggerType(PUSH_ALARM_TRIGGER);
        final AlarmHelperService alarmHelperService = new AlarmHelperService();
        final Set<AlarmTypeDto> alarms = alarmHelperService.toAlarmTypes(registerValue);
        this.builder.withAlarms(alarms);
    }

}
