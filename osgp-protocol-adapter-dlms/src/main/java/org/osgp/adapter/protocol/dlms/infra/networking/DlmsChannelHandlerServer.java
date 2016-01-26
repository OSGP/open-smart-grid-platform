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
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.osgp.adapter.protocol.dlms.domain.commands.AlarmHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;

public class DlmsChannelHandlerServer extends DlmsChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsChannelHandlerServer.class);

    @Autowired
    private AlarmHelperService alarmHelperService;

    public DlmsChannelHandlerServer() {
        super(LOGGER);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {

        final Object message = e.getMessage();
        if (message instanceof ChannelBuffer) {

            final ChannelBuffer cb = (ChannelBuffer) message;
            final byte[] bytes = cb.array();

            final byte comma = 0x2C;
            int commaPos = -1;
            for (int i = 0; i < bytes.length; i++) {
                if (comma == bytes[i]) {
                    commaPos = i;
                }
            }
            if (commaPos == -1) {
                LOGGER.error("MessageReceived does not contain a comma: " + Arrays.toString(bytes));
            } else {
                final String deviceIdentification = new String(Arrays.copyOfRange(bytes, 0, commaPos),
                        StandardCharsets.US_ASCII);
                LOGGER.info("MessageReceived for deviceIdentification: " + deviceIdentification);
                if (commaPos == bytes.length - 1) {
                    LOGGER.error("MessageReceived has no alarm register bytes following the comma.");
                } else {
                    final byte[] remaining = Arrays.copyOfRange(bytes, commaPos + 1, bytes.length);
                    LOGGER.info("MessageReceived for alarm register bytes: " + Arrays.toString(remaining));
                    if (remaining.length != 4) {
                        LOGGER.warn("MessageReceived does not have 4 bytes for the alarm register: " + remaining.length);
                    }
                    long registerValue = 0;
                    final int lengthUpToMax4 = Math.min(4, remaining.length);
                    for (int i = 0; i < lengthUpToMax4; i++) {
                        final byte b = remaining[i];
                        registerValue = registerValue << 8;
                        registerValue = registerValue + b;
                    }
                    LOGGER.info("MessageReceived for alarm register value: " + registerValue);
                    final Set<AlarmType> alarmTypes = this.alarmHelperService.toAlarmTypes(registerValue);
                    LOGGER.info("MessageReceived for alarm types: " + alarmTypes);

                    this.logMessage(deviceIdentification, alarmTypes);
                }
            }

        } else {

            LOGGER.warn("MessageReceived of type " + (message == null ? "null" : message.getClass().getName()) + ": "
                    + message);
        }
    }

    public void setAlarmHelperService(final AlarmHelperService alarmHelperService) {
        this.alarmHelperService = alarmHelperService;
    }
}
