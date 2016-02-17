/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.networking;

import java.io.ByteArrayOutputStream;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dlms.DlmsPushNotification;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;

public class DlmsPushNotificationDecoder extends ReplayingDecoder<DlmsPushNotificationDecoder.DecodingState> {

    private static final byte[] SMS_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x00, 0x02, 0x03, (byte) 0xFF };
    private static final byte[] CSD_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x02, 0x02, 0x00, (byte) 0xFF };
    private static final byte[] SCHEDULER_OBISCODE_BYTES = new byte[] { 0x00, 0x00, 0x0F, 0x00, 0x04, (byte) 0xFF };

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsPushNotificationDecoder.class);

    /**
     * The elements inside the DLMS Push notification (Alarm or Wakeup SMS are
     * expressed in bytes separated by a comma (byte 0x2C).
     */
    private static final byte COMMA = 0x2C;

    public static enum DecodingState {
        EQUIPMENT_IDENTIFIER,
        RECEIVED_DATA;
    }

    @Autowired
    private AlarmHelperService alarmHelperService;

    private DlmsPushNotification.Builder builder;

    public DlmsPushNotificationDecoder() {
        LOGGER.debug("Created new DLMS Push Notification Alarm decoder");
        this.reset();
    }

    private void reset() {
        this.checkpoint(DecodingState.EQUIPMENT_IDENTIFIER);
        this.builder = new DlmsPushNotification.Builder();
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer,
            final DecodingState state) throws UnknownDecodingStateException {
        LOGGER.debug("Decoding state: {}", state.toString());

        switch (state) {
        case EQUIPMENT_IDENTIFIER:
            this.decodeEquipmentIdentifier(buffer);
            return this.setCheckpointAndContinueDecode(ctx, channel, buffer, DecodingState.RECEIVED_DATA);
        case RECEIVED_DATA:
            this.decodeReceivedData(buffer);
            return this.buildPushNotification();
        default:
            throw new UnknownDecodingStateException(state.name());
        }
    }

    private Object setCheckpointAndContinueDecode(final ChannelHandlerContext ctx, final Channel channel,
            final ChannelBuffer buffer, final DecodingState nextState) throws UnknownDecodingStateException {
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

    private void decodeEquipmentIdentifier(final ChannelBuffer buffer) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte nextByte;
        while ((nextByte = buffer.readByte()) != COMMA) {
            baos.write(nextByte);
        }
        final byte[] equipmentIdentifierBytes = baos.toByteArray();
        final String equipmentIdentifier = new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII);
        this.builder.withEquipmentIdentifier(equipmentIdentifier);
        this.builder.appendBytes(equipmentIdentifierBytes).appendByte(COMMA);
    }

    private void decodeReceivedData(final ChannelBuffer buffer) {

        final int dataBufferLength = buffer.readableBytes();
        if (dataBufferLength <= 4) {
            this.readAlarmRegisterData(buffer);
        } else {
            this.readObiscodeData(buffer);
        }
    }

    private void readObiscodeData(final ChannelBuffer buffer) {
        final byte[] dataBytes = buffer.readBytes(6).array();

        if (Arrays.equals(SMS_OBISCODE_BYTES, dataBytes)) {
            final StringBuilder obiscode = new StringBuilder();
            for (final byte b : dataBytes) {
                if (obiscode.length() != 0) {
                    obiscode.append(".");
                }
                obiscode.append(b & 0xFF);
            }
            this.builder.withObiscode(obiscode.toString());
        } else if (Arrays.equals(CSD_OBISCODE_BYTES, dataBytes)) {
            LOGGER.warn("CSD Push notification not supported");
            this.builder.withObiscode("");
        } else if (Arrays.equals(SCHEDULER_OBISCODE_BYTES, dataBytes)) {
            LOGGER.warn("Scheduler Push notification not supported");
            this.builder.withObiscode("");
        } else {
            LOGGER.warn("Unknown Push notification not supported. Unable to decode");
        }

        this.builder.withAlarms(null);
    }

    private void readAlarmRegisterData(final ChannelBuffer buffer) {
        final byte[] dataBytes = buffer.readBytes(4).array();
        long registerValue = 0;
        for (final byte b : dataBytes) {
            registerValue = registerValue << 8;
            final int unsignedValue = b & 0xFF;
            registerValue = registerValue + unsignedValue;
        }
        this.builder.withObiscode("");
        final Set<AlarmType> alarms = this.alarmHelperService.toAlarmTypes(registerValue);
        this.builder.withAlarms(alarms);
        this.builder.appendBytes(dataBytes);
    }

}
