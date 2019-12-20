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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

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
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        LOGGER.debug("Decoding state: {}", this.state().toString());

        if (this.state().compareTo(DecodingState.SECURITY_KEY) == 0) {
            this.decodeSecurityKey(in);
            this.checkpoint(DecodingState.SEQUENCE_NUMBER);
        }

        if (this.state().compareTo(DecodingState.SEQUENCE_NUMBER) <= 0) {
            this.decodeSequenceNumber(in);
            this.checkpoint(DecodingState.DEVICE_ID);
        }

        if (this.state().compareTo(DecodingState.DEVICE_ID) <= 0) {
            this.decodeDeviceId(in);
            this.checkpoint(DecodingState.LENGTH_INDICATOR);
        }

        if (this.state().compareTo(DecodingState.LENGTH_INDICATOR) <= 0) {
            this.decodeLengthIndicator(in);
            this.checkpoint(DecodingState.PAYLOAD_MESSAGE);
        }

        if (this.state().compareTo(DecodingState.PAYLOAD_MESSAGE) <= 0) {
            this.builder.withPayloadMessage(Oslp.Message.parseFrom(in.readBytes(this.length).array()));
            this.checkpoint(DecodingState.SECURITY_KEY);
            try {
                out.add(this.builder.withSignature(this.signature).withProvider(this.provider).build());
            } finally {
                this.reset();
            }
        } else {
            // Should not get here
            throw new UnknownOslpDecodingStateException(this.state().name());
        }
    }

    private void decodeSecurityKey(final ByteBuf buffer) {
        this.builder.withSecurityKey(buffer.readBytes(OslpEnvelope.SECURITY_KEY_LENGTH).array());
    }

    private void decodeSequenceNumber(final ByteBuf buffer) {
        this.builder.withSequenceNumber(buffer.readBytes(OslpEnvelope.SEQUENCE_NUMBER_LENGTH).array());

    }

    private void decodeDeviceId(final ByteBuf buffer) {
        this.builder.withDeviceId(
                buffer.readBytes(OslpEnvelope.DEVICE_ID_LENGTH + OslpEnvelope.MANUFACTURER_ID_LENGTH).array());
    }

    private void decodeLengthIndicator(final ByteBuf buffer) {
        this.length = buffer.getUnsignedShort(buffer.readerIndex());
        // Unlike the read* methods, the get* methods do not increase the reader
        // index.
        // If we wouldn't update the reader index the bytes read while trying to
        // read the payload
        // would also contain the length indicator (which would cause the
        // payload parsing to fail).
        buffer.readerIndex(buffer.readerIndex() + OslpEnvelope.LENGTH_INDICATOR_LENGTH);
    }

    private void reset() {
        this.checkpoint(DecodingState.SECURITY_KEY);
        this.builder = new OslpEnvelope.Builder();
        this.length = 0;
    }

}
