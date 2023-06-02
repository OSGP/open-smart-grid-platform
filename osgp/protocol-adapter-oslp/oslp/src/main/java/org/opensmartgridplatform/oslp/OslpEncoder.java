//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.oslp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  protected void encode(
      final ChannelHandlerContext ctx, final OslpEnvelope msg, final List<Object> out)
      throws Exception {
    final String channelId = ctx.channel().id().asLongText();
    LOGGER.debug("Encoding message for channel {}.", channelId);

    out.add(encodeMessage(msg));
  }
}
