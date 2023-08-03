// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.dlms.DlmsPushNotification;

@ExtendWith(MockitoExtension.class)
class DlmsChannelHandlerServerTest {

  @Mock private PushedMessageProcessor pushedMessageProcessor;

  @InjectMocks private DlmsChannelHandlerServer dlmsChannelHandlerServer;

  @Test
  void testChannelRead0() {
    final String equipmentIdentifier = "123";
    final String ipAddress = "127.0.0.1";
    final ChannelHandlerContext ctx = this.newChannelHandlerContextMock(ipAddress);
    final DlmsPushNotification message =
        new DlmsPushNotification("bytes".getBytes(), equipmentIdentifier, "", new HashSet<>());

    this.dlmsChannelHandlerServer.channelRead0(ctx, message);

    verify(this.pushedMessageProcessor)
        .process(eq(message), any(String.class), eq(equipmentIdentifier), eq(ipAddress));
  }

  @Test
  void testChannelRead0EmptyEqId() {
    final String equipmentIdentifier = "";
    final String ipAddress = "127.0.0.1";
    final ChannelHandlerContext ctx = this.newChannelHandlerContextMock(ipAddress);
    final DlmsPushNotification message =
        new DlmsPushNotification("bytes".getBytes(), equipmentIdentifier, "", new HashSet<>());

    this.dlmsChannelHandlerServer.channelRead0(ctx, message);

    verifyNoInteractions(this.pushedMessageProcessor);
  }

  @Test
  void testChannelRead0EqIdNull() {
    final String equipmentIdentifier = null;
    final String ipAddress = "127.0.0.1";
    final ChannelHandlerContext ctx = this.newChannelHandlerContextMock(ipAddress);
    final DlmsPushNotification message =
        new DlmsPushNotification("bytes".getBytes(), equipmentIdentifier, "", new HashSet<>());

    this.dlmsChannelHandlerServer.channelRead0(ctx, message);

    verifyNoInteractions(this.pushedMessageProcessor);
  }

  @Test
  void testChannelRead0ExceptionIpAddress() {
    final String equipmentIdentifier = "123";
    final String ipAddress = null;

    final ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
    when(ctx.channel()).thenThrow(new RuntimeException());

    final DlmsPushNotification message =
        new DlmsPushNotification("bytes".getBytes(), equipmentIdentifier, "", new HashSet<>());

    this.dlmsChannelHandlerServer.channelRead0(ctx, message);

    verify(this.pushedMessageProcessor)
        .process(eq(message), any(String.class), eq(equipmentIdentifier), eq(ipAddress));
  }

  private ChannelHandlerContext newChannelHandlerContextMock(final String ipAddress) {
    final ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
    final Channel channel = mock(Channel.class);
    final InetSocketAddress socketAddress = mock(InetSocketAddress.class);
    when(ctx.channel()).thenReturn(channel);
    when(channel.remoteAddress()).thenReturn(socketAddress);
    when(socketAddress.getHostString()).thenReturn(ipAddress);
    return ctx;
  }
}
