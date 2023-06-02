//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DlmsChannelHandler extends SimpleChannelInboundHandler<DlmsPushNotification> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DlmsChannelHandler.class);

  @Autowired private DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender;

  @Override
  public void channelActive(final ChannelHandlerContext ctx) throws Exception {
    final String channelId = ctx.channel().id().asLongText();
    LOGGER.info("{} Channel active.", channelId);
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
    final String channelId = ctx.channel().id().asLongText();
    LOGGER.info("{} Channel inactive.", channelId);
    super.channelInactive(ctx);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
    final String channelId = ctx.channel().id().asLongText();
    LOGGER.warn("{} Unexpected exception from downstream.", channelId, cause);
    ctx.channel().close();
  }

  protected void logMessage(final DlmsPushNotification message) {

    final DlmsLogItemRequestMessage dlmsLogItemRequestMessage =
        new DlmsLogItemRequestMessage(
            message.getEquipmentIdentifier(), true, message.isValid(), message, message.getSize());

    this.dlmsLogItemRequestMessageSender.send(dlmsLogItemRequestMessage);
  }
}
