// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.util.encoders.Hex;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

public class Mx382AlarmDecoder extends AlarmDecoder {

  private static final byte EVENT_NOTIFICATION_REQUEST = (byte) 0xC2;
  private static final byte[] WPDU_HEADER =
      new byte[] {0x00, 0x01, 0x00, 0x67, 0x00, 0x66, 0x00, 0x1b};
  private static final byte[] AMM_FORWARDED_ALARM_VERSION_0 =
      new byte[] {(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x00};
  private static final byte[] DEVICE_ID_2_CLASS_ID = new byte[] {0x00, 0x01};
  private static final byte VALUE_ATTRIBUTE_ID = 0x02;
  private static final byte DSMR22_EQUIPMENT_IDENTIFIER_LENGTH = 0x10;
  private final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();

  public DlmsPushNotification decodeMx382alarm(final InputStream inputStream)
      throws UnrecognizedMessageDataException {

    this.checkBytes(inputStream, WPDU_HEADER, "WPDU-header");
    this.checkByte(inputStream, EVENT_NOTIFICATION_REQUEST, "event-notification-request");

    // Datetime is not used, so can be skipped as well
    this.skipDateTime(inputStream);

    // Get equipment identifier and add it to the new push notification
    this.decodeEquipmentIdentifier(inputStream);

    // Add alarmbits to the new push notification
    this.addAlarm();

    return this.builder.build();
  }

  private void skipDateTime(final InputStream inputStream) throws UnrecognizedMessageDataException {
    // The first byte of the datetime is the length byte
    final byte dateTimeLength = this.readByte(inputStream);
    this.builder.appendByte(dateTimeLength);
    // Skip the rest of the datetime bytes
    final byte[] dateTimeBytes = this.readBytes(inputStream, dateTimeLength);
    this.builder.appendBytes(dateTimeBytes);
  }

  private void decodeEquipmentIdentifier(final InputStream inputStream)
      throws UnrecognizedMessageDataException {

    // Check bytes of incoming mx382 message
    this.checkBytes(inputStream, DEVICE_ID_2_CLASS_ID, "class-id");
    this.checkBytes(inputStream, AMM_FORWARDED_ALARM_VERSION_0, "logical name forwarded alarm");
    this.checkByte(inputStream, VALUE_ATTRIBUTE_ID, "attribute-id");

    // Read the equipment identifier from incoming mx382 message
    final byte[] equipmentIdentifierBytes =
        this.readBytes(inputStream, DSMR22_EQUIPMENT_IDENTIFIER_LENGTH);
    this.builder.withEquipmentIdentifier(
        new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII));
  }

  private void addAlarm() {
    this.builder.withTriggerType(PUSH_ALARM_TRIGGER);
    final Set<AlarmTypeDto> alarmSet = new HashSet<>();
    alarmSet.add(AlarmTypeDto.LAST_GASP);
    this.builder.addAlarms(alarmSet);
  }

  private void checkByte(final InputStream inputStream, final byte expectedByte, final String name)
      throws UnrecognizedMessageDataException {
    final byte readByte = this.readByte(inputStream);
    this.builder.appendByte(readByte);
    if (readByte != expectedByte) {
      throw new UnrecognizedMessageDataException(
          String.format(
              "Expected a %s (%s), but encountered: (%s)",
              name, toHexString(expectedByte), toHexString(readByte)));
    }
  }

  private void checkBytes(
      final InputStream inputStream, final byte[] expectedBytes, final String name)
      throws UnrecognizedMessageDataException {
    final byte[] readBytes = this.readBytes(inputStream, expectedBytes.length);
    this.builder.appendBytes(readBytes);
    if (!Arrays.equals(readBytes, expectedBytes)) {
      throw new UnrecognizedMessageDataException(
          String.format(
              "Expected a %s (%s), but encountered: (%s)",
              name, toHexString(expectedBytes), toHexString(readBytes)));
    }
  }

  private static String toHexString(final byte b) {
    return toHexString(new byte[] {b});
  }

  public static String toHexString(final byte[] ba) {
    final StringBuilder sb = new StringBuilder("[");
    boolean first = true;
    for (final byte b : ba) {
      if (!first) {
        sb.append(", ");
      } else {
        first = false;
      }
      sb.append("0x");
      sb.append(Hex.toHexString(new byte[] {b}));
    }
    sb.append("]");
    return sb.toString();
  }
}
