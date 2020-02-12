/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.slf4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class OslpChannelHandler extends SimpleChannelInboundHandler<OslpEnvelope> {

    private static Semaphore availableConnections;
    private static boolean limitConnections;

    private final Logger logger;

    @Resource
    protected String oslpSignatureProvider;

    @Resource
    protected String oslpSignature;

    @Resource
    protected int connectionTimeout;

    protected final ConcurrentMap<String, OslpCallbackHandler> callbackHandlers = new ConcurrentHashMap<>();

    protected OslpChannelHandler(final Logger logger) {
        this.logger = logger;
        OslpChannelHandler.limitConnections = false;
    }

    protected OslpChannelHandler(final Logger logger, final int maxConcurrentIncomingMessages) {
        OslpChannelHandler.availableConnections = new Semaphore(maxConcurrentIncomingMessages);
        OslpChannelHandler.limitConnections = true;
        this.logger = logger;
    }

    public void setProvider(final String provider) {
        this.oslpSignatureProvider = provider;
    }

    public void setSignature(final String signature) {
        this.oslpSignature = signature;
    }

    private void acquireConnection() throws InterruptedException {
        if (OslpChannelHandler.limitConnections) {
            final Instant acquireStart = Instant.now();
            this.logger.info("Connection requested. Available connections: " + availableConnections.availablePermits());

            availableConnections.acquire();

            final Duration acquireDuration = Duration.between(acquireStart, Instant.now());
            this.logger.info("Connection granted. Available connections: {}, acquireDuration (ms) {}",
                    availableConnections.availablePermits(), acquireDuration.toMillis());
        }
    }

    private void releaseConnection() {
        if (OslpChannelHandler.limitConnections) {
            this.logger.info("Connection released. Available connections: " + availableConnections.availablePermits());
            availableConnections.release();
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        this.acquireConnection();
        this.logger.info("Channel [{}] active.", ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        this.logger.info("Channel [{}] inactive.", ctx.channel().id());
        this.releaseConnection();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        final String channelId = ctx.channel().id().asLongText();

        if (this.isConnectionReset(cause)) {
            this.logger.info("Channel [{}] - Connection was (as expected) reset by the device.", channelId);
        } else {
            this.logger.warn("Channel [{}] - Unexpected exception from downstream. {}", channelId, cause);
            this.callbackHandlers.get(channelId).getDeviceResponseHandler().handleException(cause);
            this.callbackHandlers.remove(channelId);
        }
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }

    private boolean isConnectionReset(final Throwable e) {
        return e != null && e instanceof IOException && e.getMessage() != null
                && e.getMessage().contains("Connection reset by peer");
    }
}
