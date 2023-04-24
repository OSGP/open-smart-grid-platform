/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.EXTERNAL_TRIGGER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.INTERNAL_TRIGGER_ALARM;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SCHEDULER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_ALARM;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_SCHEDULER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_UDP;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigSmr50;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigSmr55;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mx382AlarmDecoder extends AlarmDecoder {

  private static final int SMR5_NUMBER_OF_BYTES_FOR_ADDRESSING = 8;

  private final DlmsObjectConfigSmr50 dlmsObjectConfigSmr50 = new DlmsObjectConfigSmr50();
  private final DlmsObjectConfigSmr50 dlmsObjectConfigSmr55 = new DlmsObjectConfigSmr55();

  private static final Logger LOGGER = LoggerFactory.getLogger(Mx382AlarmDecoder.class);

  /** DLMS data types used in the SMR5 push notification */
  private static final byte STRUCTURE = 0x02;

  private static final byte OCTET_STRING = 0x09;
  private static final byte DOUBLE_LONG_UNSIGNED = 0x06;

  private final DlmsPushNotification.Builder builder = new DlmsPushNotification.Builder();

  public DlmsPushNotification decodeMx382alarm(final InputStream inputStream)
      throws UnrecognizedMessageDataException {

    // Skip addressing and the 0xC2 byte
    this.skip(inputStream, SMR5_NUMBER_OF_BYTES_FOR_ADDRESSING + 1);

    // Datetime is not used, so can be skipped as well
    this.skipDateTime(inputStream);

    this.builder.appendByte(STRUCTURE);

    final byte dataLength = 1;
    this.builder.appendByte(dataLength);

    // Decode elements
    this.decodeEquipmentIdentifier(inputStream);
    this.decodeLogicalName(inputStream, alarmRegisterExpected);

    if (PUSH_ALARM_TRIGGER.equals(this.builder.getTriggerType())) {
      this.decodePushAlarm(dataLength, inputStream);
    } else if (PUSH_UDP_TRIGGER.equals(this.builder.getTriggerType())) {
      this.decodePushUdp(dataLength, inputStream);
    }
    return this.builder.build();
  }

  private void decodePushUdp(final byte dataLength, final InputStream inputStream)
      throws UnrecognizedMessageDataException {
    if (dataLength >= 3) {
      this.decodeAlarms(inputStream, DlmsObjectType.ALARM_REGISTER_3);
    }
  }

  private void decodePushAlarm(final byte dataLength, final InputStream inputStream)
      throws UnrecognizedMessageDataException {
    if (dataLength >= 3) {
      this.decodeAlarms(inputStream, DlmsObjectType.ALARM_REGISTER_1);
    }
    if (dataLength == 4) {
      // SMR 5.2
      this.decodeAlarms(inputStream, DlmsObjectType.ALARM_REGISTER_2);
    }
  }

  private void skipDateTime(final InputStream inputStream) throws UnrecognizedMessageDataException {
    // The first byte of the datetime is the length byte
    final byte dateTimeLength = this.readByte(inputStream);

    // Skip the rest of the datetime bytes
    this.skip(inputStream, dateTimeLength);
  }

  private void decodeEquipmentIdentifier(final InputStream inputStream)
      throws UnrecognizedMessageDataException {

    // First byte should indicate octet-string
    final byte dataTypeByte = this.readByte(inputStream);
    if (dataTypeByte != OCTET_STRING) {
      throw new UnrecognizedMessageDataException(
          "Expected an octet-string (0x09), but encountered: " + dataTypeByte);
    }
    this.builder.appendByte(dataTypeByte);

    // Next byte should be the length of the octet-string
    final byte dataLength = this.readByte(inputStream);
    if (dataLength != EQUIPMENT_IDENTIFIER_LENGTH) {
      throw new UnrecognizedMessageDataException(
          "Expected an identifier with length "
              + EQUIPMENT_IDENTIFIER_LENGTH
              + ", but specified length is: "
              + dataLength);
    }
    this.builder.appendByte(dataLength);

    // Read the identifier
    final byte[] equipmentIdentifierBytes = this.readBytes(inputStream, dataLength);

    this.builder.withEquipmentIdentifier(
        new String(equipmentIdentifierBytes, StandardCharsets.US_ASCII));
    this.builder.appendBytes(equipmentIdentifierBytes);
  }

  private void decodeLogicalName(final InputStream inputStream, final boolean alarmExpected)
      throws UnrecognizedMessageDataException {
    // First byte should indicate octet-string
    final byte dataTypeByte = this.readByte(inputStream);
    if (dataTypeByte != OCTET_STRING) {
      throw new UnrecognizedMessageDataException(
          "Expected an octet-string (0x09), but encountered: " + dataTypeByte);
    }
    this.builder.appendByte(dataTypeByte);

    // Next byte should be the length of the octet-string
    final byte dataLength = this.readByte(inputStream);
    if (dataLength != NUMBER_OF_BYTES_FOR_LOGICAL_NAME) {
      throw new UnrecognizedMessageDataException(
          "Expected a logical name with length "
              + NUMBER_OF_BYTES_FOR_LOGICAL_NAME
              + ", but specified length is: "
              + dataLength);
    }
    this.builder.appendByte(dataLength);

    // Next bytes are the logical name
    this.decodeObisCodeData(inputStream, alarmExpected);
  }

  private void decodeAlarms(final InputStream inputStream, final DlmsObjectType dlmsObjectType)
      throws UnrecognizedMessageDataException {
    // First byte should indicate double-long-unsigned
    final byte dataTypeByte = this.readByte(inputStream);
    if (dataTypeByte != DOUBLE_LONG_UNSIGNED) {
      throw new UnrecognizedMessageDataException(
          "Expected a double-long-unsigned (0x06), but encountered: " + dataTypeByte);
    }
    this.builder.appendByte(dataTypeByte);

    // Next bytes are the alarm
    this.decodeAlarmRegisterData(inputStream, this.builder, dlmsObjectType);
  }

  private void decodeObisCodeData(
      final InputStream inputStream, final boolean alarmRegisterExpected)
      throws UnrecognizedMessageDataException {

    final byte[] logicalNameBytes = this.readBytes(inputStream, NUMBER_OF_BYTES_FOR_LOGICAL_NAME);

    try {
      if (!alarmRegisterExpected && this.isLogicalNameSmsTrigger(logicalNameBytes)) {
        this.builder.withTriggerType(PUSH_SMS_TRIGGER);
      } else if (!alarmRegisterExpected && this.isLogicalNameCsdTrigger(logicalNameBytes)) {
        LOGGER.warn("CSD Push notification not supported");
        this.builder.withTriggerType(PUSH_CSD_TRIGGER);
      } else if (!alarmRegisterExpected && this.isLogicalNameSchedulerTrigger(logicalNameBytes)) {
        LOGGER.warn("Scheduler Push notification not supported");
        this.builder.withTriggerType(PUSH_SCHEDULER_TRIGGER);
      } else if (alarmRegisterExpected && this.isLogicalNameAlarmTrigger(logicalNameBytes)) {
        this.builder.withTriggerType(PUSH_ALARM_TRIGGER);
      } else if (alarmRegisterExpected && this.isLogicalNameUdpTrigger(logicalNameBytes)) {
        this.builder.withTriggerType(PUSH_UDP_TRIGGER);
      } else {
        LOGGER.warn("Unknown Push notification not supported. Unable to decode");
        this.builder.withTriggerType("");
      }
    } catch (final ProtocolAdapterException e) {
      throw new UnrecognizedMessageDataException("Error decoding logical name", e);
    }

    this.builder.withAlarms(null);
    this.builder.appendBytes(logicalNameBytes);
  }

  private boolean isLogicalNameSmsTrigger(final byte[] logicalNameBytes)
      throws ProtocolAdapterException {
    // SMR5 has one general object for the SMS and CSD triggers
    return Arrays.equals(
        this.dlmsObjectConfigSmr50.getObisForObject(EXTERNAL_TRIGGER).bytes(), logicalNameBytes);
  }

  private boolean isLogicalNameCsdTrigger(final byte[] logicalNameBytes)
      throws ProtocolAdapterException {
    // SMR5 has one general object for the SMS and CSD triggers
    return Arrays.equals(
        this.dlmsObjectConfigSmr50.getObisForObject(EXTERNAL_TRIGGER).bytes(), logicalNameBytes);
  }

  private boolean isLogicalNameSchedulerTrigger(final byte[] logicalNameBytes)
      throws ProtocolAdapterException {
    return Arrays.equals(
            this.dlmsObjectConfigSmr50.getObisForObject(PUSH_SCHEDULER).bytes(), logicalNameBytes)
        || Arrays.equals(
            this.dlmsObjectConfigSmr50.getObisForObject(PUSH_SETUP_SCHEDULER).bytes(),
            logicalNameBytes);
  }

  private boolean isLogicalNameAlarmTrigger(final byte[] logicalNameBytes)
      throws ProtocolAdapterException {
    return Arrays.equals(
            this.dlmsObjectConfigSmr50.getObisForObject(INTERNAL_TRIGGER_ALARM).bytes(),
            logicalNameBytes)
        || Arrays.equals(
            this.dlmsObjectConfigSmr50.getObisForObject(PUSH_SETUP_ALARM).bytes(),
            logicalNameBytes);
  }

  private boolean isLogicalNameUdpTrigger(final byte[] logicalNameBytes)
      throws ProtocolAdapterException {
    return Arrays.equals(
        this.dlmsObjectConfigSmr55.getObisForObject(PUSH_SETUP_UDP).bytes(), logicalNameBytes);
  }
}
