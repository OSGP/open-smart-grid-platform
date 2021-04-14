/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator;

import com.google.protobuf.GeneratedMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.codec.binary.Base64;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OslpTestHandler extends SimpleChannelInboundHandler<OslpEnvelope> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OslpTestHandler.class);

  private final Consumer<OslpEnvelope> oslpEnvelopeConsumer;

  public OslpTestHandler() {
    this(
        oslpEnvelope ->
            LOGGER.info(
                "Received OslpEnvelope for deviceUid {} containing a {}",
                Base64.encodeBase64String(oslpEnvelope.getDeviceId()),
                oslpEnvelope.getPayloadMessage().getAllFields().entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof GeneratedMessage)
                    .map(entry -> entry.getKey().getName())
                    .findFirst()
                    .orElse("?")));
  }

  public OslpTestHandler(final Consumer<OslpEnvelope> oslpEnvelopeConsumer) {
    this.oslpEnvelopeConsumer = Objects.requireNonNull(oslpEnvelopeConsumer);
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final OslpEnvelope msg)
      throws Exception {
    msg.getPayloadMessage().getAllFields().entrySet().stream()
        .filter(entry -> entry.getValue() instanceof GeneratedMessage)
        .map(entry -> entry.getKey().getName())
        .findFirst();
    this.oslpEnvelopeConsumer.accept(msg);
  }
}
