/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class DlmsChannelHandler extends SimpleChannelInboundHandler<DlmsPushNotification> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsChannelHandler.class);

    @Autowired
    private DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender;

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        final String channelId = ctx.channel().id().asLongText();
        LOGGER.info("{} Channel active.", channelId);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        final String channelId = ctx.channel().id().asLongText();
        LOGGER.info("{} Channel inactive.", channelId);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        final String channelId = ctx.channel().id().asLongText();
        LOGGER.warn("{} Unexpected exception from downstream. {}", channelId, cause);
        ctx.channel().close();
    }

    protected void logMessage(final DlmsPushNotification message) {

        final DlmsLogItemRequestMessage dlmsLogItemRequestMessage = new DlmsLogItemRequestMessage(
                message.getEquipmentIdentifier(), true, message.isValid(), message, message.getSize());

        this.dlmsLogItemRequestMessageSender.send(dlmsLogItemRequestMessage);
    }
}
