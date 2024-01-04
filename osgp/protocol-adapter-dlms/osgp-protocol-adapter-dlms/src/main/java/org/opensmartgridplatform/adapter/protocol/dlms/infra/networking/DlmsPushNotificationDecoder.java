// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DlmsPushNotificationDecoder {

  private static final Logger LOGGER = LoggerFactory.getLogger(DlmsPushNotificationDecoder.class);

  // see DLMS Green Book, 9.5 Abstract syntax of COSEM PDUs
  // data-notification [15],
  // event-notification-request [194]
  private static final byte DATA_NOTIFICATION = 0x0F;
  private static final byte EVENT_NOTIFICATION_REQUEST = (byte) 0xC2;

  public DlmsPushNotification decode(
      final byte[] message, final ConnectionProtocol connectionProtocol)
      throws UnrecognizedMessageDataException {
    /**
     * MX382 alarm examples (in HEX bytes):
     *
     * <p>00 01 00 67 00 66 00 28<br>
     * C2 09 0C<br>
     * XX XX XX XX XX XX XX XX XX XX XX XX<br>
     * 00 01 00 00 60 01 01 FF 02<br>
     * 09 10 YY YY YY YY YY YY YY YY YY YY YY YY YY YY YY YY<br>
     *
     * <p>8 bytes WPDU-header<br>
     * [194] event-notification-request [09] datetime present [12] time length<br>
     * [datetime.........................]<br>
     * [ 1] class-id (Device ID 2) [ 0.0.96.1.1.255] object instance id [2] attribute-id ~ Value<br>
     * [9] OCTET STRING [16] length [E-meter equipment identifier]<br>
     *
     * <p>DSMR4 alarm examples (in HEX bytes):
     *
     * <p>45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32 // Equipment id EXXXX123456789012<br>
     * 2C // Comma 00 00 0F 00 04 FF<br>
     * // Logical name 0.0.15.0.4.255<br>
     *
     * <p>45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32 // Equipment id EXXXX123456789012<br>
     * 2C // Comma<br>
     * 00 00 00 02 // Alarm register, with Replace battery set<br>
     *
     * <p>SMR5 alarm examples (in HEX bytes):
     *
     * <p>0F // Data-notification<br>
     * 00 00 00 01 // Long-invoke-id-and-priority (can be ignored)<br>
     * 00 // Date-time (empty)<br>
     * 02 02 // Structure with 2 elements<br>
     * 09 11 45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32 // Equipmentid EXXXX123456789012<br>
     * 09 06 00 00 19 09 00 FF // Logical name: Push setup schedule<br>
     *
     * <p>0F // Data-notification<br>
     * 00 00 00 01 // Long-invoke-id-and-priority (can be ignored)<br>
     * 00 // Date-time (empty)<br>
     * 02 03 // Structure with 3 elements<br>
     * 09 11 45 58 58 58 58 31 32 33 34 35 36 37 38 39 30 31 32 // Equipmentid EXXXX123456789012<br>
     * 09 06 00 01 19 09 00 FF // Logical name: Push setup alarms<br>
     * 06 00 00 00 02 // Alarm register, with Replace battery set<br>
     *
     * <p>Notes: - For MX382 and SMR5 alarms we get 8 addressing bytes in front of the alarm. These
     * bytes can be ignored here. - To check if the alarm is in MX382, DSMR4 or SMR5 format, check
     * the 9th byte (at index 8). If it is 0F, then it is SMR5, if it is C2 then it is MX382
     * otherwise it is DSMR4 because the 9th byte in DSMR4 is in the identifier and this should be a
     * number or a character, so it can't be ASCII code 0F or C2.
     */
    final DlmsPushNotification pushNotification;

    // Determine whether the alarm is in MX382, DSMR4 or SMR5 format.
    final boolean smr5alarm = message[8] == DATA_NOTIFICATION;
    final boolean mx382alarm = message[8] == EVENT_NOTIFICATION_REQUEST;

    final InputStream inputStream = new ByteArrayInputStream(message);
    LOGGER.info("Decoding alarm, SMR5 alarm: {}, MX382 alarm: {}", smr5alarm, mx382alarm);

    if (smr5alarm) {
      final Smr5AlarmDecoder alarmDecoder = new Smr5AlarmDecoder();
      pushNotification = alarmDecoder.decodeSmr5alarm(inputStream);
    } else if (mx382alarm) {
      final Mx382AlarmDecoder alarmDecoder = new Mx382AlarmDecoder();
      pushNotification = alarmDecoder.decodeMx382alarm(inputStream, connectionProtocol);
    } else {
      final Dsmr4AlarmDecoder alarmDecoder = new Dsmr4AlarmDecoder();
      pushNotification = alarmDecoder.decodeDsmr4alarm(inputStream);
    }

    LOGGER.info("Decoded push notification: {}", pushNotification);
    return pushNotification;
  }
}
