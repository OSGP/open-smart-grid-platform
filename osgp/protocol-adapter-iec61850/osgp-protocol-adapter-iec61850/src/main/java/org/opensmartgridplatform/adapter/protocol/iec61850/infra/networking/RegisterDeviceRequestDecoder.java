//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.Arrays;
import java.util.List;
import org.opensmartgridplatform.iec61850.RegisterDeviceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterDeviceRequestDecoder extends ByteToMessageDecoder {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegisterDeviceRequestDecoder.class);

  public RegisterDeviceRequestDecoder() {
    LOGGER.debug("Created new IEC61850 Register Device Request decoder");
  }

  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
    /*
     * Do nothing. Wait for decodeLast to handle all bytes received when the channel is disconnected.
     */
  }

  @Override
  protected void decodeLast(
      final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {

    LOGGER.info("Decoding bytes received at channel disconnect.");

    if (!in.isReadable()) {
      LOGGER.warn("Channel disconnect with no readable bytes.");
      return;
    }

    final byte[] availableBytes = new byte[in.readableBytes()];
    in.readBytes(availableBytes);

    try {
      out.add(new RegisterDeviceRequest(availableBytes));
    } catch (final Exception e) {
      LOGGER.error(
          "Unable to construct a {} based on the bytes received: {}",
          RegisterDeviceRequest.class.getSimpleName(),
          Arrays.toString(availableBytes),
          e);
    }
  }
}
