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

import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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
            LOGGER.debug("Decoding security key.");
            this.decodeSecurityKey(in);
            this.checkpoint(DecodingState.SEQUENCE_NUMBER);
        }

        if (this.state().compareTo(DecodingState.SEQUENCE_NUMBER) <= 0) {
            LOGGER.debug("Decoding sequence number.");
            this.decodeSequenceNumber(in);
            this.checkpoint(DecodingState.DEVICE_ID);
        }

        if (this.state().compareTo(DecodingState.DEVICE_ID) <= 0) {
            LOGGER.debug("Decoding device id.");
            this.decodeDeviceId(in);
            this.checkpoint(DecodingState.LENGTH_INDICATOR);
        }

        if (this.state().compareTo(DecodingState.LENGTH_INDICATOR) <= 0) {
            LOGGER.debug("Decoding length indicator.");
            this.decodeLengthIndicator(in);
            this.checkpoint(DecodingState.PAYLOAD_MESSAGE);
        }

        if (this.state().compareTo(DecodingState.PAYLOAD_MESSAGE) <= 0) {
            LOGGER.debug("Decoding payload.");
            this.decodePayload(in);
            this.checkpoint(DecodingState.SECURITY_KEY);
            try {
                final OslpEnvelope msg = this.builder.withSignature(this.signature).withProvider(this.provider).build();
                out.add(msg);
            } finally {
                this.reset();
            }
        } else {
            // Should not get here
            throw new UnknownOslpDecodingStateException(this.state().name());
        }
    }

    private void decodeSecurityKey(final ByteBuf buffer) {
        final byte[] bytes = ByteBufUtil.getBytes(buffer, buffer.readerIndex(), OslpEnvelope.SECURITY_KEY_LENGTH);
        this.builder.withSecurityKey(bytes);
        buffer.readerIndex(buffer.readerIndex() + OslpEnvelope.SECURITY_KEY_LENGTH);
    }

    private void decodeSequenceNumber(final ByteBuf buffer) {
        final byte[] bytes = ByteBufUtil.getBytes(buffer, buffer.readerIndex(), OslpEnvelope.SEQUENCE_NUMBER_LENGTH);
        this.builder.withSequenceNumber(bytes);
        buffer.readerIndex(buffer.readerIndex() + OslpEnvelope.SEQUENCE_NUMBER_LENGTH);
    }

    private void decodeDeviceId(final ByteBuf buffer) {
        final int length = OslpEnvelope.DEVICE_ID_LENGTH + OslpEnvelope.MANUFACTURER_ID_LENGTH;
        final byte[] bytes = ByteBufUtil.getBytes(buffer, buffer.readerIndex(), length);
        this.builder.withDeviceId(bytes);
        buffer.readerIndex(buffer.readerIndex() + length);
    }

    private void decodeLengthIndicator(final ByteBuf buffer) {
        this.length = buffer.getUnsignedShort(buffer.readerIndex());
        buffer.readerIndex(buffer.readerIndex() + OslpEnvelope.LENGTH_INDICATOR_LENGTH);
    }

    private void decodePayload(final ByteBuf buffer) throws InvalidProtocolBufferException {
        if (this.payloadComplete(buffer)) {
            final byte[] bytes = ByteBufUtil.getBytes(buffer, buffer.readerIndex(), this.length);
            this.builder.withPayloadMessage(Oslp.Message.parseFrom(bytes));
        } else {
            LOGGER.debug("Payload has not yet been fully received.");
        }
    }

    private boolean payloadComplete(final ByteBuf buffer) {
        return buffer.capacity() >= buffer.readerIndex() + this.length;
    }

    private void reset() {
        this.checkpoint(DecodingState.SECURITY_KEY);
        this.builder = new OslpEnvelope.Builder();
        this.length = 0;
    }

}
