// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DlmsPushNotificationReplayingDecoder
    extends ReplayingDecoder<DlmsPushNotificationReplayingDecoder.DecodingState> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DlmsPushNotificationReplayingDecoder.class);

  private final DlmsPushNotificationDecoder decoder = new DlmsPushNotificationDecoder();

  public enum DecodingState {
    EQUIPMENT_IDENTIFIER
  }

  public DlmsPushNotificationReplayingDecoder() {
    LOGGER.debug("Created new DLMS Push Notification replaying decoder");
  }

  /**
   * Decoded the alarm bytes in the buffer. Could be either a MX382, DSMR4 or SMR5 alarm. If there
   * are not enough bytes while decoding, the ReplayingDecoder rewinds and tries the decoding again
   * when there are more bytes received.
   *
   * @param ctx the context from the ReplayingDecoder. Not used in decoding the alarm.
   * @param byteBuf the bytes of the alarm.
   * @param out decoded list of objects
   * @throws UnrecognizedMessageDataException
   */
  @Override
  protected void decode(
      final ChannelHandlerContext ctx, final ByteBuf byteBuf, final List<Object> out)
      throws UnrecognizedMessageDataException {
    final byte[] byteArray = new byte[byteBuf.readableBytes()];
    byteBuf.readBytes(byteArray);

    final DlmsPushNotification dlmsPushNotification = this.decoder.decode(byteArray);

    LOGGER.info("Decoded push notification: {}", dlmsPushNotification);
    out.add(dlmsPushNotification);
  }
}
