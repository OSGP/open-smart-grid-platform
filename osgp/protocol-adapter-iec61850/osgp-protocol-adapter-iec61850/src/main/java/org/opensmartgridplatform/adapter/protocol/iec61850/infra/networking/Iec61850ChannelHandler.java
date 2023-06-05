// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.Iec61850LogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.Iec61850LogItemRequestMessageSender;
import org.opensmartgridplatform.iec61850.RegisterDeviceRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Iec61850ChannelHandler
    extends SimpleChannelInboundHandler<RegisterDeviceRequest> {

  private final Logger logger;

  @Autowired private Iec61850LogItemRequestMessageSender iec61850LogItemRequestMessageSender;

  protected Iec61850ChannelHandler(final Logger logger) {
    this.logger = logger;
  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) throws Exception {
    final String channelId = ctx.channel().id().asLongText();
    this.logger.info("{} Channel active.", channelId);
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
    final String channelId = ctx.channel().id().asLongText();
    this.logger.info("{} Channel inactive.", channelId);
    super.channelInactive(ctx);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
    final String channelId = ctx.channel().id().asLongText();
    this.logger.warn("{} Unexpected exception from downstream.", channelId, cause);
    ctx.channel().close();
  }

  protected void logMessage(final RegisterDeviceRequest message) {

    final Iec61850LogItemRequestMessage iec61850LogItemRequestMessage =
        new Iec61850LogItemRequestMessage(
            message.getDeviceIdentification(), true, message.isValid(), message, message.getSize());

    this.iec61850LogItemRequestMessageSender.send(iec61850LogItemRequestMessage);
  }
}
