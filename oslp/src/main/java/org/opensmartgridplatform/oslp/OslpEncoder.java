/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.oslp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class OslpEncoder extends OneToOneEncoder {
    private static ChannelBuffer encodeMessage(OslpEnvelope envelope) {
        int size = envelope.getSize();

        ChannelBuffer buffer = ChannelBuffers.buffer(size);

        buffer.writeBytes(envelope.getSecurityKey());
        buffer.writeBytes(envelope.getSequenceNumber());
        buffer.writeBytes(envelope.getDeviceId());
        buffer.writeBytes(envelope.getLengthIndicator());
        buffer.writeBytes(envelope.getPayloadMessage().toByteArray());

        return buffer;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) {
        if (msg instanceof OslpEnvelope) {
            return encodeMessage((OslpEnvelope) msg);
        } else {
            return msg;
        }
    }
}
