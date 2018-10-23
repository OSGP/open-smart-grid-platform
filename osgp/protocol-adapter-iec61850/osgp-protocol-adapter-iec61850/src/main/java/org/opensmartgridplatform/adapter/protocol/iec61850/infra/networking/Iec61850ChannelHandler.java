/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.Iec61850LogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.Iec61850LogItemRequestMessageSender;
import org.opensmartgridplatform.iec61850.RegisterDeviceRequest;

public abstract class Iec61850ChannelHandler extends SimpleChannelHandler {

    private final Logger logger;

    @Autowired
    private Iec61850LogItemRequestMessageSender iec61850LogItemRequestMessageSender;

    protected Iec61850ChannelHandler(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.logger.info("{} Channel opened", e.getChannel().getId());
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.logger.info("{} Channel disconnected", e.getChannel().getId());
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.logger.info("{} Channel closed", e.getChannel().getId());
        super.channelClosed(ctx, e);
    }

    @Override
    public void channelUnbound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.logger.info("{} Channel unbound", e.getChannel().getId());
        super.channelUnbound(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        final int channelId = e.getChannel().getId();
        this.logger.warn("{} Unexpected exception from downstream. {}", channelId, e.getCause());
        e.getChannel().close();
    }

    protected void logMessage(final RegisterDeviceRequest message) {

        final Iec61850LogItemRequestMessage iec61850LogItemRequestMessage = new Iec61850LogItemRequestMessage(
                message.getDeviceIdentification(), true, message.isValid(), message, message.getSize());

        this.iec61850LogItemRequestMessageSender.send(iec61850LogItemRequestMessage);
    }
}
