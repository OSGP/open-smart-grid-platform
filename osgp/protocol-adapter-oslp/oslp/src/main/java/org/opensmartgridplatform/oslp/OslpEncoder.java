/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.oslp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class OslpEncoder extends MessageToMessageEncoder<OslpEnvelope> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpEncoder.class);

    private static ByteBuf encodeMessage(final OslpEnvelope envelope) {
        final int size = envelope.getSize();

        final ByteBuf buffer = Unpooled.buffer(size);

        buffer.writeBytes(envelope.getSecurityKey());
        buffer.writeBytes(envelope.getSequenceNumber());
        buffer.writeBytes(envelope.getDeviceId());
        buffer.writeBytes(envelope.getLengthIndicator());
        buffer.writeBytes(envelope.getPayloadMessage().toByteArray());

        return buffer;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final OslpEnvelope msg, final List<Object> out)
            throws Exception {
        final String channelId = ctx.channel().id().asLongText();
        LOGGER.debug("Encoding message for channel {}.", channelId);

        out.add(encodeMessage(msg));
    }
}
