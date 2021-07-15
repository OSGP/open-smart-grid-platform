/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.DeviceRegistrationService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.OslpUtils;
import org.opensmartgridplatform.shared.exceptionhandling.NoDeviceResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.micrometer.core.instrument.Metrics;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class OslpChannelHandlerClient extends OslpChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpChannelHandlerClient.class);

    @Autowired
    private Bootstrap bootstrap;

    @Autowired
    private DeviceRegistrationService deviceRegistrationService;

    @Autowired
    private String successfulMessagesMetric;

    public OslpChannelHandlerClient() {
        super(LOGGER);
    }

    public void setDeviceRegistrationService(final DeviceRegistrationService deviceRegistrationService) {
        this.deviceRegistrationService = deviceRegistrationService;
    }

    public Bootstrap getBootstrap() {
        return this.bootstrap;
    }

    public void setBootstrap(final Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        final String channelId = ctx.channel().id().asLongText();
        if (this.callbackHandlers.containsKey(channelId)) {
            this.callbackHandlers.get(channelId)
                    .getDeviceResponseHandler()
                    .handleException(new NoDeviceResponseException());
            this.callbackHandlers.remove(channelId);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final OslpEnvelope message) {
        final String channelId = ctx.channel().id().asLongText();

        LOGGER.info("channelRead0 called for channel {}.", channelId);

        if (message.isValid()) {
            if (OslpUtils.isOslpResponse(message)) {
                LOGGER.info("{} Received OSLP Response (before callback): {}", channelId, message.getPayloadMessage());

                // Check the sequence number
                final Integer sequenceNumber = SequenceNumberUtils
                        .convertByteArrayToInteger(message.getSequenceNumber());

                final OslpResponseHandler oslpResponseHandler = this.callbackHandlers.get(channelId)
                        .getDeviceResponseHandler();

                try {
                    this.deviceRegistrationService.checkSequenceNumber(message.getDeviceId(), sequenceNumber);
                    oslpResponseHandler.handleResponse(message);
                    Metrics.counter(this.successfulMessagesMetric).increment();
                } catch (final ProtocolAdapterException exc) {
                    // Users should not be able to see errors about sequence
                    // numbers, replace the exception by a generic exception.
                    LOGGER.error("An error occurred while checking the sequence number", exc);
                    oslpResponseHandler.handleException(new NoDeviceResponseException());
                }

                this.callbackHandlers.remove(channelId);
                ctx.channel().close();

            } else {
                LOGGER.warn("{} Received OSLP Request, which is not expected: {}", channelId,
                        message.getPayloadMessage());
            }
        } else {
            LOGGER.warn("{} Received message wasn't properly secured.", channelId);
        }
    }

    public void send(final InetSocketAddress address, final OslpEnvelope request,
            final OslpResponseHandler responseHandler, final String deviceIdentification) {
        LOGGER.info("Sending OSLP request: {}", request.getPayloadMessage());

        // Open connection and send message.
        final ChannelFuture channelFuture = this.bootstrap.connect(address);

        this.callbackHandlers.put(channelFuture.channel().id().asLongText(), new OslpCallbackHandler(responseHandler));

        channelFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(final ChannelFuture future) throws IOException {

                if (future.isSuccess()) {
                    try {
                        OslpChannelHandlerClient.this.write(future, address, request);
                    } catch (final IOException e) {
                        responseHandler.handleException(e);
                    }
                } else {
                    LOGGER.info("The connection to the device {} is not successful", deviceIdentification);
                    responseHandler.handleException(new IOException("ChannelFuture - Unable to connect"));
                }
            }
        });
    }

    private void write(final ChannelFuture channelFuture, final InetSocketAddress address, final OslpEnvelope request)
            throws IOException {
        final Channel channel = channelFuture.channel();

        if (channel != null && channel.isActive()) {
            LOGGER.info("Channel [{}] - Connection established to: {}", channelFuture.channel().id(), address);
        } else {
            LOGGER.info("The connection for device {} is not successful", request.getDeviceId());
            LOGGER.warn("Channel [{}] - Unable to connect to: {}", channelFuture.channel().id(), address);
            throw new IOException("Channel - Unable to connect");
        }

        try {
            channel.writeAndFlush(request);
        } catch (final Exception e) {
            LOGGER.error("{} Exception while writing request: {}", channelFuture.channel().id(), e.getCause(), e);
            this.callbackHandlers.remove(channel.id().asLongText());

            throw e;
        }
    }
}
