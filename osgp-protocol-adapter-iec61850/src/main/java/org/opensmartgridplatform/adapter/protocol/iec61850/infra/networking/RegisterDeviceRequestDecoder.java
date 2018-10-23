/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opensmartgridplatform.iec61850.RegisterDeviceRequest;

public class RegisterDeviceRequestDecoder extends FrameDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterDeviceRequestDecoder.class);

    public RegisterDeviceRequestDecoder() {
        LOGGER.debug("Created new IEC61850 Register Device Request decoder");
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) {
        /*
         * Do not return anything. Wait for decodeLast to handle all bytes
         * received when the channel is disconnected.
         */
        return null;
    }

    @Override
    protected Object decodeLast(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) {

        LOGGER.info("Decoding bytes received at channel disconnect.");

        if (!buffer.readable()) {
            LOGGER.warn("Channel disconnect with no readable bytes.");
            return null;
        }

        final byte[] availableBytes = new byte[buffer.readableBytes()];
        buffer.readBytes(availableBytes);

        try {
            return new RegisterDeviceRequest(availableBytes);
        } catch (final Exception e) {
            LOGGER.error("Unable to construct a {} based on the bytes received: {}",
                    RegisterDeviceRequest.class.getSimpleName(), Arrays.toString(availableBytes), e);
            return null;
        }
    }
}
