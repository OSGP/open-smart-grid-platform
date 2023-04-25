/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

@Slf4j
public class Mx382AlarmDecoder extends AlarmDecoder {

  private static final int NUMBER_OF_BYTES_FOR_ADDRESSING = 8;
  private static final byte EVENT_NOTIFICATION_REQUEST = (byte) 0xC2;
  private static final byte[] AMM_FORWARDED_ALARM_VERSION_0 =
      new byte[] {
        (byte) 0x128, (byte) 0x128, (byte) 0x128, (byte) 0x128, (byte) 0x128, (byte) 0x00
      };
  private static final byte[] ALARM_REGISTER_3 =
      new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x61, (byte) 0x62, (byte) 0x02, (byte) 0xFF};
  private static final byte[] DEVICE_ID_2_CLASS_ID = new byte[] {0x00, 0x01};
  private static final byte VALUE_ATTRIBUTE_ID = 0x02;
  private static final byte DSMR22_EQUIPMENT_IDENTIFIER_LENGTH = 0x10;
  private static final byte DSMR22_NUMBER_OF_BYTES_FOR_LOGICAL_NAME = 0x06;
  private static final byte ELEMENTS_IN_STRUCTURE = 0x03;

  /** DLMS data types used in the SMR5 push notification */
  private static final byte STRUCTURE = 0x02;

  private static final byte OCTET_STRING = 0x09;
  private static final byte DOUBLE_LONG_UNSIGNED = 0x06;

  private final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();

  public DlmsPushNotification decodeMx382alarm(final InputStream inputStream)
      throws UnrecognizedMessageDataException {

    // Skip addressing
    this.skip(inputStream, NUMBER_OF_BYTES_FOR_ADDRESSING);
    log.debug("skipped {} bytes", NUMBER_OF_BYTES_FOR_ADDRESSING);

    this.checkByte(inputStream, EVENT_NOTIFICATION_REQUEST, "event-notification-request");

    // Datetime is not used, so can be skipped as well
    this.skipDateTime(inputStream);

    this.builder.appendByte(STRUCTURE);
    this.builder.appendByte(ELEMENTS_IN_STRUCTURE);

    // Get equipment identifier and add it to the new push notification
    this.decodeEquipmentIdentifier(inputStream);

    // Add logical name and alarmbits to the new push notification
    this.addLogicalName();
    this.addAlarm();

    return this.builder.build();
  }

  private void skipDateTime(final InputStream inputStream) throws UnrecognizedMessageDataException {
    // The first byte of the datetime is the length byte
    final byte dateTimeLength = this.readByte(inputStream);
    log.info(toHexString(dateTimeLength));
    // Skip the rest of the datetime bytes
    this.skip(inputStream, dateTimeLength);
    log.debug("skipped {} bytes", dateTimeLength);
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
    // and add it Tag-Length-Value wise to the new push notification
    this.builder.appendByte(OCTET_STRING);
    this.builder.appendByte(DSMR22_EQUIPMENT_IDENTIFIER_LENGTH);
    this.builder.withEquipmentIdentifier(
        new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII));
    this.builder.appendBytes(equipmentIdentifierBytes);
  }

  private void addLogicalName() {
    this.builder.appendByte(OCTET_STRING);
    this.builder.appendByte(DSMR22_NUMBER_OF_BYTES_FOR_LOGICAL_NAME);
    this.builder.appendBytes(ALARM_REGISTER_3);
  }

  private void addAlarm() {
    // First byte should indicate double-long-unsigned
    this.builder.appendByte(DOUBLE_LONG_UNSIGNED);

    this.builder.withTriggerType(PUSH_ALARM_TRIGGER);
    final Set<AlarmTypeDto> alarmSet = new HashSet<>();
    alarmSet.add(AlarmTypeDto.LAST_GASP);
    this.builder.addAlarms(alarmSet);

    //    LAST_GASP(AlarmRegister 3, bit 0)
    final byte[] alarmBytes = {0x0, 0x0, 0x0, 0x01};
    this.builder.appendBytes(alarmBytes);
  }

  private void checkByte(final InputStream inputStream, final byte expectedByte, final String name)
      throws UnrecognizedMessageDataException {
    final byte readByte = this.readByte(inputStream);
    log.info(toHexString(readByte) + " == " + toHexString(expectedByte));
    if (readByte != expectedByte) {
      throw new UnrecognizedMessageDataException(
          String.format(
              "Expected a %s (%s), but encountered: (%s)",
              name, toHexString(expectedByte), toHexString(readByte)));
    }
    //    return readByte;
  }

  private void checkBytes(
      final InputStream inputStream, final byte[] expectedBytes, final String name)
      throws UnrecognizedMessageDataException {
    final byte[] readBytes = this.readBytes(inputStream, expectedBytes.length);
    log.info(toHexString(readBytes) + " == " + toHexString(expectedBytes));
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
