// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.springframework.beans.factory.annotation.Autowired;

@Sharable
@Slf4j
public class DlmsChannelHandlerServer extends DlmsChannelHandler {

  @Autowired private PushedMessageProcessor pushedMessageProcessor;

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, final DlmsPushNotification message) {
    final String correlationId = UUID.randomUUID().toString().replace("-", "");
    final String deviceIdentification = message.getEquipmentIdentifier();
    final String ipAddress = this.retrieveIpAddress(ctx, deviceIdentification);

    this.pushedMessageProcessor.process(message, correlationId, deviceIdentification, ipAddress);
  }

  private String retrieveIpAddress(
      final ChannelHandlerContext ctx, final String deviceIdentification) {
    String ipAddress = null;
    try {
      ipAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
      log.info(
          "Push notification for device {} received from IP address {}",
          deviceIdentification,
          ipAddress);
    } catch (final Exception ex) {
      log.info("Unable to determine IP address of the meter sending a push notification: ", ex);
    }
    return ipAddress;
  }
}
