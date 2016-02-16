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
        final String equipmentIdentifier = new String(baos.toByteArray(), StandardCharsets.US_ASCII);
        this.builder.withEquipmentIdentifier(equipmentIdentifier);
    }

    private void decodeReceivedData(final ChannelBuffer buffer) {

        final byte[] dataBytes = buffer.readBytes(6).array();
        final byte[] smsObiscodeBytes = new byte[] { 0x00, 0x00, 0x00, 0x02, 0x03, (byte) 0xFF };

        if (Arrays.equals(smsObiscodeBytes, dataBytes)) {
            final StringBuilder obiscode = new StringBuilder();
            for (final byte b : dataBytes) {
                if (obiscode.length() != 0) {
                    obiscode.append(".");
                }
                obiscode.append(b & 0xFF);
            }
            this.builder.withObiscode(obiscode.toString());
            this.builder.withAlarms(null);
        } else {

            final byte[] alarmObject = Arrays.copyOfRange(dataBytes, 0, 4);
            long registerValue = 0;
            for (final byte b : alarmObject) {
                registerValue = registerValue << 8;
                registerValue = registerValue + b;
            }
            this.builder.withObiscode("");
            final Set<AlarmType> alarms = this.alarmHelperService.toAlarmTypes(registerValue);
            this.builder.withAlarms(alarms);
        }

    }
}
