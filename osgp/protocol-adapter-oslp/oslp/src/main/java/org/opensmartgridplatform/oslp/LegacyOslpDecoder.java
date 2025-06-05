// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.oslp;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;

import org.opensmartgridplatform.oslp.Envelope.OslpEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyOslpDecoder extends ReplayingDecoder<LegacyOslpDecoder.DecodingState> {
  private static final Logger LOGGER = LoggerFactory.getLogger(LegacyOslpDecoder.class);

  private final String signature;
  private final String provider;

  public enum DecodingState {
    SECURITY_KEY,
    SEQUENCE_NUMBER,
    DEVICE_ID,
    LENGTH_INDICATOR,
    PAYLOAD_MESSAGE;
  }

  private LegacyOslpEnvelope.Builder builder = new LegacyOslpEnvelope.Builder();
  private int length;

  public LegacyOslpDecoder(final String signature, final String provider) {
    super(DecodingState.SECURITY_KEY);
    LOGGER.debug("Created new decoder");
    this.signature = signature;
    this.provider = provider;
  }

  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out)
      throws Exception {
    final DecodingState decodingState = this.state();
    LOGGER.debug("Decoding state: {}", decodingState);

    switch (decodingState) {
      case SECURITY_KEY:
        LOGGER.debug("Decoding security key.");
        this.decodeSecurityKey(in);
        this.checkpoint(DecodingState.SEQUENCE_NUMBER);
        // fall-through
      case SEQUENCE_NUMBER:
        LOGGER.debug("Decoding sequence number.");
        this.decodeSequenceNumber(in);
        this.checkpoint(DecodingState.DEVICE_ID);
        // fall-through
      case DEVICE_ID:
        LOGGER.debug("Decoding device id.");
        this.decodeDeviceId(in);
        this.checkpoint(DecodingState.LENGTH_INDICATOR);
        // fall-through
      case LENGTH_INDICATOR:
        LOGGER.debug("Decoding length indicator.");
        this.decodeLengthIndicator(in);
        this.checkpoint(DecodingState.PAYLOAD_MESSAGE);
        // fall-through
      case PAYLOAD_MESSAGE:
        LOGGER.debug("Decoding payload.");
        this.decodePayload(in);
        this.outputOslpEnvelopeAndResetDecoder(out);
        break;
      default:
        throw new UnknownOslpDecodingStateException(decodingState.name());
    }
  }

  private void outputOslpEnvelopeAndResetDecoder(final List<Object> out) {
    final LegacyOslpEnvelope msg =
        this.builder.withSignature(this.signature).withProvider(this.provider).build();
    this.reset();
    out.add(msg);
  }

  private void reset() {
    this.checkpoint(DecodingState.SECURITY_KEY);
    this.builder = new LegacyOslpEnvelope.Builder();
    this.length = 0;
  }

  private void decodeSecurityKey(final ByteBuf buffer) {
    final byte[] bytes = this.readBytes(buffer, LegacyOslpEnvelope.SECURITY_KEY_LENGTH);
    LOGGER.debug("Decoded security key: {}", bytes);
    this.builder.withSecurityKey(bytes);
  }

  private void decodeSequenceNumber(final ByteBuf buffer) {
    final byte[] bytes = this.readBytes(buffer, LegacyOslpEnvelope.SEQUENCE_NUMBER_LENGTH);
    LOGGER.debug("Decoded sequence number: {}", bytes);
    this.builder.withSequenceNumber(bytes);
  }

  private void decodeDeviceId(final ByteBuf buffer) {
    final int deviceAndManufacturerLength =
        LegacyOslpEnvelope.DEVICE_ID_LENGTH + LegacyOslpEnvelope.MANUFACTURER_ID_LENGTH;
    final byte[] bytes = this.readBytes(buffer, deviceAndManufacturerLength);
    LOGGER.debug("Decoded device id: {}", bytes);
    this.builder.withDeviceId(bytes);
  }

  private void decodeLengthIndicator(final ByteBuf buffer) {
    this.length = this.readLengthIndicator(buffer, LegacyOslpEnvelope.LENGTH_INDICATOR_LENGTH);
    LOGGER.debug("Decoded length: {}", this.length);
  }

  private int readLengthIndicator(final ByteBuf buffer, final int lengthIndicatorLength) {
    if (lengthIndicatorLength == 2) {
      return buffer.readUnsignedShort();
    }
    /*
     * Throw an exception if the length indicator is something other than 2,
     * which is the value of OslpEnvelope.LENGTH_INDICATOR_LENGTH at the
     * time this method was added.
     *
     * This method should only ever be called with
     * OslpEnvelope.LENGTH_INDICATOR_LENGTH. As soon as that value would
     * ever be changed to something else, reading the length value from the
     * buffer needs to be changed with it.
     */
    throw new AssertionError("Length indicator is not 2: " + lengthIndicatorLength);
  }

  private void decodePayload(final ByteBuf buffer) throws InvalidProtocolBufferException {
    final byte[] bytes = this.readBytes(buffer, this.length);

    final OslpEnvelope envelope = OslpEnvelope.parseFrom(bytes);

    this.builder
            .withDeviceId(envelope.getDeviceId().toByteArray())
            .withSecurityKey(envelope.getSecurityKey().toByteArray())
            .withSequenceNumber(envelope.getSequenceNumber().toByteArray())
            .withPayloadMessage(envelope.getPayload());
  }

  private byte[] readBytes(final ByteBuf buffer, final int length) {
    final ByteBuf temp = buffer.readBytes(length);
    try {
      return ByteBufUtil.getBytes(temp);
    } finally {
      temp.release();
    }
  }
}
