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
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.osgp.adapter.protocol.dlms.domain.commands.AlarmHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dlms.DlmsPushNotificationAlarm;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;

public class DlmsPushNotificationAlarmDecoder extends ReplayingDecoder<DlmsPushNotificationAlarmDecoder.DecodingState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsPushNotificationAlarmDecoder.class);

    /**
     * The elements inside the DLMS Push notification are expressed in bytes
     * separated by a comma (byte 0x2C).
     */
    private static final byte COMMA = 0x2C;

    public static enum DecodingState {
        EQUIPMENT_IDENTIFIER,
        ALARM_OBJECT;
    }

    @Autowired
    private AlarmHelperService alarmHelperService;

    private DlmsPushNotificationAlarm.Builder builder;

    public DlmsPushNotificationAlarmDecoder() {
        LOGGER.debug("Created new DLMS Push Notification Alarm decoder");
        this.reset();
    }

    private void reset() {
        this.checkpoint(DecodingState.EQUIPMENT_IDENTIFIER);
        this.builder = new DlmsPushNotificationAlarm.Builder();
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer,
            final DecodingState state) throws UnknownAlarmDecodingStateException {
        LOGGER.debug("Decoding state: {}", state.toString());

        switch (state) {
        case EQUIPMENT_IDENTIFIER:
            this.decodeEquipmentIdentifier(buffer);
            return this.setCheckpointAndContinueDecode(ctx, channel, buffer, DecodingState.ALARM_OBJECT);
        case ALARM_OBJECT:
            this.decodeAlarmObject(buffer);
            return this.buildPushNotificationAlarm();
        default:
            throw new UnknownAlarmDecodingStateException(state.name());
        }
    }

    private Object setCheckpointAndContinueDecode(final ChannelHandlerContext ctx, final Channel channel,
            final ChannelBuffer buffer, final DecodingState nextState) throws UnknownAlarmDecodingStateException {
        this.checkpoint(nextState);
        return this.decode(ctx, channel, buffer, nextState);
    }

    private DlmsPushNotificationAlarm buildPushNotificationAlarm() {
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

    private void decodeAlarmObject(final ChannelBuffer buffer) {
        final byte[] alarmObject = buffer.readBytes(4).array();
        long registerValue = 0;
        for (final byte b : alarmObject) {
            registerValue = registerValue << 8;
            final int unsignedValue = b & 0xFF;
            registerValue = registerValue + unsignedValue;
        }
        final Set<AlarmType> alarms = this.alarmHelperService.toAlarmTypes(registerValue);
        this.builder.withAlarms(alarms);
        this.builder.appendBytes(alarmObject);
        int remaining = 0;
        while ((remaining = buffer.readableBytes()) > 0) {
            final byte[] bytes = new byte[remaining];
            buffer.readBytes(bytes);
            this.builder.appendBytes(bytes);
        }
    }
}
