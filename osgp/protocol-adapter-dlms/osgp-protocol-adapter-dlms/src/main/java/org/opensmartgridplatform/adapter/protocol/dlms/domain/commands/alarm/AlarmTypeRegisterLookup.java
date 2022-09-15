/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

/**
 * Gives the position of the alarm code as indicated by the AlarmType in the bit string
 * representation of the alarm register.
 *
 * <p>A position of 0 means the least significant bit, up to the maximum of 31 for the most
 * significant bit. Since the 4 most significant bits in the object are not used according to the
 * DSMR documentation, the practical meaningful most significant bit is bit 27.
 */
@Getter
public enum AlarmTypeRegisterLookup {
  // Bits for group: Other Alarms
  CLOCK_INVALID(AlarmTypeDto.CLOCK_INVALID, DlmsObjectType.ALARM_REGISTER_1, 0),
  REPLACE_BATTERY(AlarmTypeDto.REPLACE_BATTERY, DlmsObjectType.ALARM_REGISTER_1, 1),
  POWER_UP(AlarmTypeDto.POWER_UP, DlmsObjectType.ALARM_REGISTER_1, 2),
  AUXILIARY_EVENT(AlarmTypeDto.AUXILIARY_EVENT, DlmsObjectType.ALARM_REGISTER_1, 3),
  CONFIGURATION_CHANGED(AlarmTypeDto.CONFIGURATION_CHANGED, DlmsObjectType.ALARM_REGISTER_1, 4),
  // bits 5 to 7 are not used

  // Bits for group: Critical Alarms
  PROGRAM_MEMORY_ERROR(AlarmTypeDto.PROGRAM_MEMORY_ERROR, DlmsObjectType.ALARM_REGISTER_1, 8),
  RAM_ERROR(AlarmTypeDto.RAM_ERROR, DlmsObjectType.ALARM_REGISTER_1, 9),
  NV_MEMORY_ERROR(AlarmTypeDto.NV_MEMORY_ERROR, DlmsObjectType.ALARM_REGISTER_1, 10),
  MEASUREMENT_SYSTEM_ERROR(
      AlarmTypeDto.MEASUREMENT_SYSTEM_ERROR, DlmsObjectType.ALARM_REGISTER_1, 11),
  WATCHDOG_ERROR(AlarmTypeDto.WATCHDOG_ERROR, DlmsObjectType.ALARM_REGISTER_1, 12),
  FRAUD_ATTEMPT(AlarmTypeDto.FRAUD_ATTEMPT, DlmsObjectType.ALARM_REGISTER_1, 13),
  // bits 14 and 15 are not used

  // Bits for group: M-Bus Alarms
  COMMUNICATION_ERROR_M_BUS_CHANNEL_1(
      AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1, DlmsObjectType.ALARM_REGISTER_1, 16),
  COMMUNICATION_ERROR_M_BUS_CHANNEL_2(
      AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_2, DlmsObjectType.ALARM_REGISTER_1, 17),
  COMMUNICATION_ERROR_M_BUS_CHANNEL_3(
      AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_3, DlmsObjectType.ALARM_REGISTER_1, 18),
  COMMUNICATION_ERROR_M_BUS_CHANNEL_4(
      AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_4, DlmsObjectType.ALARM_REGISTER_1, 19),
  FRAUD_ATTEMPT_M_BUS_CHANNEL_1(
      AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1, DlmsObjectType.ALARM_REGISTER_1, 20),
  FRAUD_ATTEMPT_M_BUS_CHANNEL_2(
      AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_2, DlmsObjectType.ALARM_REGISTER_1, 21),
  FRAUD_ATTEMPT_M_BUS_CHANNEL_3(
      AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_3, DlmsObjectType.ALARM_REGISTER_1, 22),
  FRAUD_ATTEMPT_M_BUS_CHANNEL_4(
      AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_4, DlmsObjectType.ALARM_REGISTER_1, 23),

  // Bits for group: Reserved
  NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1(
      AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1, DlmsObjectType.ALARM_REGISTER_1, 24),
  NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2(
      AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2, DlmsObjectType.ALARM_REGISTER_1, 25),
  NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3(
      AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3, DlmsObjectType.ALARM_REGISTER_1, 26),
  NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4(
      AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4, DlmsObjectType.ALARM_REGISTER_1, 27),
  PHASE_OUTAGE_DETECTED_L1(
      AlarmTypeDto.PHASE_OUTAGE_DETECTED_L1, DlmsObjectType.ALARM_REGISTER_1, 28),
  PHASE_OUTAGE_DETECTED_L2(
      AlarmTypeDto.PHASE_OUTAGE_DETECTED_L2, DlmsObjectType.ALARM_REGISTER_1, 29),
  PHASE_OUTAGE_DETECTED_L3(
      AlarmTypeDto.PHASE_OUTAGE_DETECTED_L3, DlmsObjectType.ALARM_REGISTER_1, 30),
  PHASE_OUTAGE_TEST_INDICATION(
      AlarmTypeDto.PHASE_OUTAGE_TEST_INDICATION, DlmsObjectType.ALARM_REGISTER_1, 31),

  // Bits for group: alarm register 2
  VOLTAGE_SAG_IN_PHASE_DETECTED_L1(
      AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L1, DlmsObjectType.ALARM_REGISTER_2, 0),
  VOLTAGE_SAG_IN_PHASE_DETECTED_L2(
      AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L2, DlmsObjectType.ALARM_REGISTER_2, 1),
  VOLTAGE_SAG_IN_PHASE_DETECTED_L3(
      AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L3, DlmsObjectType.ALARM_REGISTER_2, 2),
  VOLTAGE_SWELL_IN_PHASE_DETECTED_L1(
      AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L1, DlmsObjectType.ALARM_REGISTER_2, 3),
  VOLTAGE_SWELL_IN_PHASE_DETECTED_L2(
      AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L2, DlmsObjectType.ALARM_REGISTER_2, 4),
  VOLTAGE_SWELL_IN_PHASE_DETECTED_L3(
      AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L3, DlmsObjectType.ALARM_REGISTER_2, 5),

  LAST_GASP(AlarmTypeDto.LAST_GASP, DlmsObjectType.ALARM_REGISTER_3, 0),
  LAST_GASP_TEST(AlarmTypeDto.LAST_GASP_TEST, DlmsObjectType.ALARM_REGISTER_3, 1);

  final AlarmTypeDto alarmTypeDto;
  final DlmsObjectType alarmRegisterDlmsObjectType;
  final int bit;

  AlarmTypeRegisterLookup(
      final AlarmTypeDto alarmTypeDto,
      final DlmsObjectType alarmRegisterDlmsObjectType,
      final int bit) {
    this.alarmTypeDto = alarmTypeDto;
    this.alarmRegisterDlmsObjectType = alarmRegisterDlmsObjectType;
    this.bit = bit;
  }

  public static Set<DlmsObjectType> getAlarmRegisters() {
    return Arrays.stream(AlarmTypeRegisterLookup.values())
        .map(AlarmTypeRegisterLookup::getAlarmRegisterDlmsObjectType)
        .collect(Collectors.toSet());
  }

  public static Set<AlarmTypeRegisterLookup> findByAlarmRegister(
      final DlmsObjectType dlmsObjectTypeAlarmRegister) {
    return Arrays.stream(AlarmTypeRegisterLookup.values())
        .filter(
            alarmTypeRegisterLookup ->
                alarmTypeRegisterLookup
                    .getAlarmRegisterDlmsObjectType()
                    .equals(dlmsObjectTypeAlarmRegister))
        .collect(Collectors.toSet());
  }

  public static AlarmTypeRegisterLookup getByAlarmType(final AlarmTypeDto alarmType) {
    return Arrays.stream(AlarmTypeRegisterLookup.values())
        .filter(
            alarmTypeRegisterLookup -> alarmTypeRegisterLookup.getAlarmTypeDto().equals(alarmType))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unexpected alarmType: " + alarmType));
  }

  public static Set<AlarmTypeDto> getAlarmTypesForRegister(
      final DlmsObjectType alarmRegisterDlmsObjectType) {
    return findByAlarmRegister(alarmRegisterDlmsObjectType).stream()
        .map(AlarmTypeRegisterLookup::getAlarmTypeDto)
        .collect(Collectors.toSet());
  }
}
