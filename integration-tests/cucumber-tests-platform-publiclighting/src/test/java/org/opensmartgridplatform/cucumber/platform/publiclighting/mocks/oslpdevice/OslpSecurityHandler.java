// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.security.PublicKey;
import org.opensmartgridplatform.oslp.LegacyOslpEnvelope;

@Sharable
public class OslpSecurityHandler extends SimpleChannelInboundHandler<LegacyOslpEnvelope> {

  private PublicKey publicKey;

  public OslpSecurityHandler(final PublicKey publicKey) {
    this.publicKey = publicKey;
  }

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, final LegacyOslpEnvelope message)
      throws Exception {
    message.validate(this.publicKey);

    ctx.fireChannelRead(message);
  }
}
