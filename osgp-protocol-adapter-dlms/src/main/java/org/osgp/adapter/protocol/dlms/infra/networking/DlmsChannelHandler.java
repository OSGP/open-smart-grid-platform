/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.networking;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessage;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.dlms.DlmsPushNotification;

public abstract class DlmsChannelHandler extends SimpleChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsChannelHandler.class);

    @Autowired
    private DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender;

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Channel opened", e.getChannel().getId());
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Channel disconnected", e.getChannel().getId());
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Channel closed", e.getChannel().getId());
        super.channelClosed(ctx, e);
    }

    @Override
    public void channelUnbound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Channel unbound", e.getChannel().getId());
        super.channelUnbound(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        final int channelId = e.getChannel().getId();
        LOGGER.warn("{} Unexpected exception from downstream. {}", channelId, e.getCause());
        e.getChannel().close();
    }

    protected void logMessage(final DlmsPushNotification message) {

        final DlmsLogItemRequestMessage dlmsLogItemRequestMessage = new DlmsLogItemRequestMessage(
                message.getEquipmentIdentifier(), true, message.isValid(), message, message.getSize());

        this.dlmsLogItemRequestMessageSender.send(dlmsLogItemRequestMessage);
    }
}
