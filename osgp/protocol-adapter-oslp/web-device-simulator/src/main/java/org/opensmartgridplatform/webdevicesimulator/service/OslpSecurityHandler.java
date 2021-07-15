/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.service;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.security.PublicKey;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.springframework.beans.factory.annotation.Autowired;

@Sharable
public class OslpSecurityHandler extends SimpleChannelInboundHandler<OslpEnvelope> {

  @Autowired private PublicKey publicKey;

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, final OslpEnvelope message)
      throws Exception {
    message.validate(this.publicKey);

    ctx.fireChannelRead(message);
  }
}
