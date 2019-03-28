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

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.DeviceRegistrationService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.shared.exceptionhandling.NoDeviceResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OslpChannelHandlerClient extends OslpChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpChannelHandlerClient.class);

    @Autowired
    private ClientBootstrap bootstrap;

    @Autowired
    private DeviceRegistrationService deviceRegistrationService;

    public OslpChannelHandlerClient() {
        super(LOGGER);
    }

    public void setDeviceRegistrationService(final DeviceRegistrationService deviceRegistrationService) {
        this.deviceRegistrationService = deviceRegistrationService;
    }

    public ClientBootstrap getBootstrap() {
        return this.bootstrap;
    }

    public void setBootstrap(final ClientBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        final int channelId = e.getChannel().getId();
        if (this.callbackHandlers.containsKey(channelId)) {
            this.callbackHandlers.get(channelId).getDeviceResponseHandler()
                    .handleException(new NoDeviceResponseException());
            this.callbackHandlers.remove(channelId);
        }
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) {

        final OslpEnvelope message = (OslpEnvelope) e.getMessage();
        final Integer channelId = e.getChannel().getId();

        if (message.isValid()) {
            if (this.isOslpResponse(message)) {
                LOGGER.info("{} Received OSLP Response (before callback): {}", channelId, message.getPayloadMessage());

                // Check the sequence number
                final Integer sequenceNumber = SequenceNumberUtils
                        .convertByteArrayToInteger(message.getSequenceNumber());

                final OslpResponseHandler oslpResponseHandler = this.callbackHandlers.get(channelId)
                        .getDeviceResponseHandler();

                try {
                    this.deviceRegistrationService.checkSequenceNumber(message.getDeviceId(), sequenceNumber);
                    oslpResponseHandler.handleResponse(message);
                } catch (final ProtocolAdapterException exc) {
                    // Users should not be able to see errors about sequence
                    // numbers, replace the exception by a generic exception.
                    LOGGER.error("An error occurred while checking the sequence number", exc);
                    oslpResponseHandler.handleException(new NoDeviceResponseException());
                }

                this.callbackHandlers.remove(channelId);
                e.getChannel().close();

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

        this.callbackHandlers.put(channelFuture.getChannel().getId(), new OslpCallbackHandler(responseHandler));

        channelFuture.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(final ChannelFuture future) throws IOException {

                if (future.isSuccess()) {
                    OslpChannelHandlerClient.this.write(future, address, request);
                    // What is this call below good for?
                    future.getChannel().getId();
                } else {
                    LOGGER.info("The connection to the device {} is not successful", deviceIdentification);
                    throw new IOException("ChannelFuture - Unable to connect");
                }
            }
        });
    }

    private void write(final ChannelFuture channelFuture, final InetSocketAddress address, final OslpEnvelope request)
            throws IOException {
        final Channel channel = channelFuture.getChannel();

        if (channel != null && channel.isConnected()) {
            LOGGER.info("{} Connection established to: {}", channelFuture.getChannel().getId(), address);
        } else {
            LOGGER.info("The connection for device {} is not successful", request.getDeviceId());
            LOGGER.warn("{} Unable to connect to: {}", channelFuture.getChannel().getId(), address);
            throw new IOException("Channel - Unable to connect");
        }

        try {
            channel.write(request);

        } catch (final Exception e) {
            LOGGER.error("{} Exception while writing request: {}", channelFuture.getChannel().getId(), e.getCause(), e);
            this.callbackHandlers.remove(channel.getId());

            throw e;
        }
    }
}
