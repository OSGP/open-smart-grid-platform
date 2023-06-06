// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.oslp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * SimpleChannelInboundHandler changing the direction of the OslpEncoder (which is a
 * ChannelOutboundHandlerAdapter), so it can be used as an in-bound handler.
 *
 * <p>This enables setting up an EmbeddedChannel for testing that the OslpDecoder decodes messages
 * encoded by the OslpEncoder, without changing fields that are serialized/deserialized in the
 * communication.
 */
public class InboundOslpEncoder extends SimpleChannelInboundHandler<OslpEnvelope> {

  private final OslpEncoder oslpEncoder = new OslpEncoder();

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final OslpEnvelope msg)
      throws Exception {
    final List<Object> out = new ArrayList<>();
    this.oslpEncoder.encode(ctx, msg, out);
    for (final Object object : out) {
      ctx.fireChannelRead(object);
    }
  }
}
