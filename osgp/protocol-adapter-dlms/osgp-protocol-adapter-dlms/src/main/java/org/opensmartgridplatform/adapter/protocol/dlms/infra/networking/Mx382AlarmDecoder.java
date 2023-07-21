// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import java.io.ByteArrayInputStream;
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
      new byte[] {0x00, 0x01, 0x00, 0x67, 0x00, 0x66, 0x00, 0x1d};
  private static final byte[] WPDU_HEADER_WITH_DATE =
      new byte[] {0x00, 0x01, 0x00, 0x67, 0x00, 0x66, 0x00, 0x2a};
  private static final byte[] AMM_FORWARDED_ALARM_VERSION_0 =
      new byte[] {(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x00};
  static final byte[] CLASS_ID_1 = new byte[] {0x00, 0x01};
  static final byte ATTRIBUTE_ID_2 = 0x02;

  public static final int DATA_TYPE_OCTET_STRING = 0x09;

  public static final String EXPECTED_MESSAGE_TEMPLATE = "Expected a %s (%s), within message: (%s)";

  private final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();

  public DlmsPushNotification decodeMx382alarm(final InputStream inputStream)
      throws UnrecognizedMessageDataException {
    final byte[] fullMessage = this.readAllBytes(inputStream);
    final ByteArrayInputStream messageStream = new ByteArrayInputStream(fullMessage);

    this.handleWpduHeaderBytes(fullMessage, messageStream, "WPDU-header");
    this.handleByte(
        fullMessage, messageStream, EVENT_NOTIFICATION_REQUEST, "event-notification-request");

    // Datetime is read and copied
    this.handleDateTime(messageStream);

    this.handleCosemAttributeDescriptor(fullMessage, messageStream);
    // Get equipment identifier and add it to the new push notification
    this.handleEquipmentIdentifier(messageStream);

    // Add alarmbits to the new push notification
    this.addAlarm();

    return this.builder.build();
  }

  private void handleDateTime(final InputStream inputStream)
      throws UnrecognizedMessageDataException {
    // The first byte tells if datetime is provided 0x00 is not provided and 0x09 is provided
    final byte dataType = this.readByte(inputStream);

    // read the rest of the datetime bytes date is provided
    if (dataType == DATA_TYPE_OCTET_STRING) {
      // The next byte of the datetime is the length byte
      final byte lengthOfData = this.readByte(inputStream);
      this.readBytes(inputStream, lengthOfData);
    }
  }

  private void handleEquipmentIdentifier(final InputStream inputStream)
      throws UnrecognizedMessageDataException {

    final byte dataType = this.readByte(inputStream);
    this.builder.appendByte(dataType);
    final byte length = this.readByte(inputStream);
    this.builder.appendByte(length);

    // Read the equipment identifier from incoming mx382 message
    final byte[] equipmentIdentifierBytes = this.readBytes(inputStream, length);
    this.builder.withEquipmentIdentifier(
        new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII));
  }

  private void handleCosemAttributeDescriptor(
      final byte[] fullMessage, final InputStream inputStream)
      throws UnrecognizedMessageDataException {
    // Check bytes of incoming mx382 message
    this.checkBytes(fullMessage, inputStream, CLASS_ID_1, "class-id");
    this.checkBytes(
        fullMessage, inputStream, AMM_FORWARDED_ALARM_VERSION_0, "logical name forwarded alarm");
    this.handleByte(fullMessage, inputStream, ATTRIBUTE_ID_2, "attribute-id");
  }

  private void addAlarm() {
    this.builder.withTriggerType(PUSH_ALARM_TRIGGER);
    final Set<AlarmTypeDto> alarmSet = new HashSet<>();
    alarmSet.add(AlarmTypeDto.LAST_GASP);
    this.builder.addAlarms(alarmSet);
  }

  private void handleByte(
      final byte[] fullMessage,
      final InputStream inputStream,
      final byte expectedByte,
      final String name)
      throws UnrecognizedMessageDataException {
    final byte readByte = this.readByte(inputStream);
    this.builder.appendByte(readByte);
    if (readByte != expectedByte) {
      throw new UnrecognizedMessageDataException(
          String.format(EXPECTED_MESSAGE_TEMPLATE, expectedByte, name, toHexString(fullMessage)));
    }
  }

  private void handleWpduHeaderBytes(
      final byte[] fullMessage, final InputStream inputStream, final String name)
      throws UnrecognizedMessageDataException {
    final byte[] readBytes = this.readBytes(inputStream, WPDU_HEADER.length);
    this.builder.appendBytes(readBytes);
    if (!Arrays.equals(readBytes, WPDU_HEADER)
        && !Arrays.equals(readBytes, WPDU_HEADER_WITH_DATE)) {
      throw new UnrecognizedMessageDataException(
          String.format(
              EXPECTED_MESSAGE_TEMPLATE,
              toHexString(WPDU_HEADER) + " or " + toHexString(WPDU_HEADER_WITH_DATE),
              name,
              toHexString(fullMessage)));
    }
  }

  private void checkBytes(
      final byte[] fullMessage,
      final InputStream inputStream,
      final byte[] expectedBytes,
      final String name)
      throws UnrecognizedMessageDataException {
    final byte[] readBytes = this.readBytes(inputStream, expectedBytes.length);
    this.builder.appendBytes(readBytes);
    if (!Arrays.equals(readBytes, expectedBytes)) {
      throw new UnrecognizedMessageDataException(
          String.format(
              EXPECTED_MESSAGE_TEMPLATE,
              toHexString(expectedBytes),
              name,
              toHexString(fullMessage)));
    }
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
