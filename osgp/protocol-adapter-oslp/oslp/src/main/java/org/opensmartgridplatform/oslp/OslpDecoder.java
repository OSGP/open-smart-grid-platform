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

    public enum DecodingState {
        SECURITY_KEY,
        SEQUENCE_NUMBER,
        DEVICE_ID,
        LENGTH_INDICATOR,
        PAYLOAD_MESSAGE,
        DONE;
    }

    private final OslpEnvelope.Builder builder = new OslpEnvelope.Builder();
    private int length;

    public OslpDecoder(final String signature, final String provider) {
        super(DecodingState.SECURITY_KEY);
        LOGGER.debug("Created new decoder");
        this.signature = signature;
        this.provider = provider;
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        LOGGER.debug("Decoding state: {}", this.state());

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
            this.checkpoint(DecodingState.DONE);
        }

        if (this.state().compareTo(DecodingState.DONE) <= 0) {
            LOGGER.debug("Done decoding.");
            final OslpEnvelope msg = this.builder.withSignature(this.signature).withProvider(this.provider).build();
            out.add(msg);
        } else {
            // Should not get here
            throw new UnknownOslpDecodingStateException(this.state().name());
        }
    }

    private void decodeSecurityKey(final ByteBuf buffer) {
        final byte[] bytes = ByteBufUtil.getBytes(buffer, buffer.readerIndex(), OslpEnvelope.SECURITY_KEY_LENGTH);
        LOGGER.debug("Decoded security key: {}", bytes);
        this.builder.withSecurityKey(bytes);
        buffer.readerIndex(buffer.readerIndex() + OslpEnvelope.SECURITY_KEY_LENGTH);
    }

    private void decodeSequenceNumber(final ByteBuf buffer) {
        final byte[] bytes = ByteBufUtil.getBytes(buffer, buffer.readerIndex(), OslpEnvelope.SEQUENCE_NUMBER_LENGTH);
        LOGGER.debug("Decoded sequence number: {}", bytes);
        this.builder.withSequenceNumber(bytes);
        buffer.readerIndex(buffer.readerIndex() + OslpEnvelope.SEQUENCE_NUMBER_LENGTH);
    }

    private void decodeDeviceId(final ByteBuf buffer) {
        final int deviceAndManufacturerLength = OslpEnvelope.DEVICE_ID_LENGTH + OslpEnvelope.MANUFACTURER_ID_LENGTH;
        final byte[] bytes = ByteBufUtil.getBytes(buffer, buffer.readerIndex(), deviceAndManufacturerLength);
        LOGGER.debug("Decoded device id: {}", bytes);
        this.builder.withDeviceId(bytes);
        buffer.readerIndex(buffer.readerIndex() + deviceAndManufacturerLength);
    }

    private void decodeLengthIndicator(final ByteBuf buffer) {
        this.length = buffer.getUnsignedShort(buffer.readerIndex());
        LOGGER.debug("Decoded length: {}", this.length);
        buffer.readerIndex(buffer.readerIndex() + OslpEnvelope.LENGTH_INDICATOR_LENGTH);
    }

    private void decodePayload(final ByteBuf buffer) throws InvalidProtocolBufferException {
        final byte[] bytes = ByteBufUtil.getBytes(buffer, buffer.readerIndex(), this.length);
        LOGGER.debug("Decoded payload: {}", bytes);
        this.builder.withPayloadMessage(Oslp.Message.parseFrom(bytes));
        buffer.readerIndex(buffer.readerIndex() + this.length);
    }
}
