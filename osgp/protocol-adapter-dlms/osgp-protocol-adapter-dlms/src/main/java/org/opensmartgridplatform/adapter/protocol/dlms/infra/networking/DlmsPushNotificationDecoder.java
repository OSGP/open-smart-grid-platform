/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import java.util.List;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DlmsPushNotificationDecoder
    extends ReplayingDecoder<DlmsPushNotificationDecoder.DecodingState> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DlmsPushNotificationDecoder.class);

  public enum DecodingState {
    EQUIPMENT_IDENTIFIER
  }

  public DlmsPushNotificationDecoder() {
    LOGGER.debug("Created new DLMS Push Notification decoder");
  }

  /**
   * Decoded the alarm bytes in the buffer. Could be either a DSMR4 or SMR5 alarm. If there are not
   * enough bytes while decoding, the ReplayingDecoder rewinds and tries the decoding again when
   * there are more bytes received.
   *
   * @param ctx the context from the ReplayingDecoder. Not used in decoding the alarm.
   * @param in the bytes of the alarm.
   * @param out decoded list of objects
   * @throws UnrecognizedMessageDataException
   */
  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out)
      throws UnrecognizedMessageDataException {
    /**
     * DSMR4 alarm examples (in HEX bytes):
     *
     * <p>45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32 // Equipment id EXXXX123456789012 2C //
     * Comma 00 00 0F 00 04 FF // Logical name 0.0.15.0.4.255
     *
     * <p>45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32 // Equipment id EXXXX123456789012 2C //
     * Comma 00 00 00 02 // Alarm register, with Replace battery set
     *
     * <p>SMR5 alarm examples (in HEX bytes):
     *
     * <p>0F // Data-notification 00 00 00 01 // Long-invoke-id-and-priority (can be ignored) 00 //
     * Date-time (empty) 02 02 // Structure with 2 elements 09 11 45 58 58 58 58 31 32 33 34 35 36
     * 37 38 39 30 31 32 // Equipment id EXXXX123456789012 09 06 00 00 19 09 00 FF // Logical name:
     * Push setup schedule
     *
     * <p>0F // Data-notification 00 00 00 01 // Long-invoke-id-and-priority (can be ignored) 00 //
     * Date-time (empty) 02 03 // Structure with 3 elements 09 11 45 58 58 58 58 31 32 33 34 35 36
     * 37 38 39 30 31 32 // Equipment id EXXXX123456789012 09 06 00 01 19 09 00 FF // Logical name:
     * Push setup alarms 06 00 00 00 02 // Alarm register, with Replace battery set
     *
     * <p>Notes: - For SMR5 alarms, we get 8 additional addressing bytes in front of the alarm.
     * These bytes can be ignored here. - To check if the alarm is in DSMR4 or SMR5 format, check
     * the 9th byte (at index 8). If it is 0F, then it is SMR5, otherwise it is DSMR4, because the
     * 9th byte in DSMR4 is in the identifier and this should be a number or a character, so it
     * can't be ASCII code 0F.
     */
    final DlmsPushNotification pushNotification;

    // Determine whether the alarm is in DSMR4 or SMR5 format.
    final boolean smr5alarm = in.getByte(8) == 0x0F;

    LOGGER.info("Decoding state: {}, SMR5 alarm: {}", this.state(), smr5alarm);

    if (smr5alarm) {
      final Smr5AlarmDecoder alarmDecoder = new Smr5AlarmDecoder();
      pushNotification = alarmDecoder.decodeSmr5alarm(in);
    } else {
      final Dsmr4AlarmDecoder alarmDecoder = new Dsmr4AlarmDecoder();
      pushNotification = alarmDecoder.decodeDsmr4alarm(in);
    }

    LOGGER.info("Decoded push notification: {}", pushNotification);
    out.add(pushNotification);
  }
}
