/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.oslp;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;

public class OslpDecoder extends ReplayingDecoder<OslpDecoder.DecodingState> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OslpDecoder.class);

    private final String signature;
    private final String provider;

    public static enum DecodingState {
        SECURITY_KEY,
        SEQUENCE_NUMBER,
        DEVICE_ID,
        LENGTH_INDICATOR,
        PAYLOAD_MESSAGE;
    }

    private OslpEnvelope.Builder builder;

    private int length;

    public OslpDecoder(final String signature, final String provider) {
        LOGGER.debug("Created new decoder");
        this.signature = signature;
        this.provider = provider;

        this.reset();
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final org.jboss.netty.channel.Channel channel,
            final ChannelBuffer buffer, final DecodingState state) throws UnknownOslpDecodingStateException,
            InvalidProtocolBufferException {
        LOGGER.debug("Decoding state: {}", state.toString());

        if (state.compareTo(DecodingState.SECURITY_KEY) == 0) {
            this.decodeSecurityKey(buffer);
            this.checkpoint(DecodingState.SEQUENCE_NUMBER);
        }

        if (state.compareTo(DecodingState.SEQUENCE_NUMBER) <= 0) {
            this.decodeSequenceNumber(buffer);
            this.checkpoint(DecodingState.DEVICE_ID);
        }

        if (state.compareTo(DecodingState.DEVICE_ID) <= 0) {
            this.decodeDeviceId(buffer);
            this.checkpoint(DecodingState.LENGTH_INDICATOR);
        }

        if (state.compareTo(DecodingState.LENGTH_INDICATOR) <= 0) {
            this.decodeLengthIndicator(buffer);
            this.checkpoint(DecodingState.PAYLOAD_MESSAGE);
        }

        if (state.compareTo(DecodingState.PAYLOAD_MESSAGE) <= 0) {
            this.builder.withPayloadMessage(Oslp.Message.parseFrom(buffer.readBytes(this.length).array()));
            this.checkpoint(DecodingState.SECURITY_KEY);
            try {
                return this.builder.withSignature(this.signature).withProvider(this.provider).build();
            } finally {
                this.reset();
            }
        } else {
            // Should not get here
            throw new UnknownOslpDecodingStateException(state.name());
        }
    }

    private void decodeSecurityKey(final ChannelBuffer buffer) {
        this.builder.withSecurityKey(buffer.readBytes(OslpEnvelope.SECURITY_KEY_LENGTH).toByteBuffer().array());
    }

    private void decodeSequenceNumber(final ChannelBuffer buffer) {
        this.builder.withSequenceNumber(buffer.readBytes(OslpEnvelope.SEQUENCE_NUMBER_LENGTH).toByteBuffer().array());

    }

    private void decodeDeviceId(final ChannelBuffer buffer) {
        this.builder.withDeviceId(buffer.readBytes(OslpEnvelope.DEVICE_ID_LENGTH + OslpEnvelope.MANUFACTURER_ID_LENGTH)
                .toByteBuffer().array());
    }

    private void decodeLengthIndicator(final ChannelBuffer buffer) {
        this.length = buffer.getUnsignedShort(buffer.readerIndex());
        // Unlike the read* methods, the get* methods do not increase the reader index. 
        // If we wouldn't update the reader index the bytes read while trying to read the payload
        // would also contain the length indicator (which would cause the payload parsing to fail).
        buffer.readerIndex(buffer.readerIndex() + OslpEnvelope.LENGTH_INDICATOR_LENGTH);
    }

    private void reset() {
        this.checkpoint(DecodingState.SECURITY_KEY);
        this.builder = new OslpEnvelope.Builder();
        this.length = 0;
    }
}
