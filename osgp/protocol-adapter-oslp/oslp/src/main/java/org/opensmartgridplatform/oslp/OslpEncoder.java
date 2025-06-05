// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.oslp;

import com.google.protobuf.ByteString;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.opensmartgridplatform.oslp.Envelope.OslpEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OslpEncoder extends MessageToMessageEncoder<LegacyOslpEnvelope> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OslpEncoder.class);

  private static ByteBuf encodeMessage(final LegacyOslpEnvelope envelope) {
    final OslpEnvelope oslpEnvelope = OslpEnvelope.newBuilder()
            .setSecurityKey(ByteString.copyFrom(envelope.getSecurityKey()))
            .setSequenceNumber(ByteString.copyFrom(envelope.getSequenceNumber()))
            .setDeviceId(ByteString.copyFrom(envelope.getDeviceId()))
            .setLengthIndicator(ByteString.copyFrom(envelope.getLengthIndicator()))
            .setPayload(envelope.getPayloadMessage())
            .build();

    final int size = oslpEnvelope.getSerializedSize();

    final ByteBuf buffer = Unpooled.buffer(size);

    buffer.writeBytes(oslpEnvelope.toByteArray());

    return buffer;
  }

  @Override
  protected void encode(final ChannelHandlerContext ctx, final LegacyOslpEnvelope msg, final List<Object> out) {
    if (LOGGER.isDebugEnabled()) {
      final String channelId = ctx.channel().id().asLongText();
      LOGGER.debug("Encoding message for channel {}.", channelId);
    }

    out.add(encodeMessage(msg));
  }
}
