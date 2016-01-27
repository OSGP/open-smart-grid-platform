/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.networking;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.osgp.adapter.protocol.dlms.domain.commands.AlarmHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dlms.DlmsPushNotificationAlarm;

public class DlmsChannelHandlerServer extends DlmsChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsChannelHandlerServer.class);

    @Autowired
    private AlarmHelperService alarmHelperService;

    public DlmsChannelHandlerServer() {
        super(LOGGER);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final DlmsPushNotificationAlarm message = (DlmsPushNotificationAlarm) e.getMessage();
        this.logMessage(message);
    }

    public void setAlarmHelperService(final AlarmHelperService alarmHelperService) {
        this.alarmHelperService = alarmHelperService;
    }
}
