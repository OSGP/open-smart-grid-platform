/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum AlarmType {

  // Bits for group: Other Alarms
  CLOCK_INVALID(AlarmRegister.ALARM_REGISTER_1, 0, false),
  REPLACE_BATTERY(AlarmRegister.ALARM_REGISTER_1, 1, false),
  POWER_UP(AlarmRegister.ALARM_REGISTER_1, 2, false),
  AUXILIARY_EVENT(AlarmRegister.ALARM_REGISTER_1, 3, false),
  CONFIGURATION_CHANGED(AlarmRegister.ALARM_REGISTER_1, 4, false),
  // bits 5 to 7 are not used

  // Bits for group: Critical Alarms
  PROGRAM_MEMORY_ERROR(AlarmRegister.ALARM_REGISTER_1, 8, false),
  RAM_ERROR(AlarmRegister.ALARM_REGISTER_1, 9, false),
  NV_MEMORY_ERROR(AlarmRegister.ALARM_REGISTER_1, 10, false),
  MEASUREMENT_SYSTEM_ERROR(AlarmRegister.ALARM_REGISTER_1, 11, false),
  WATCHDOG_ERROR(AlarmRegister.ALARM_REGISTER_1, 12, false),
  FRAUD_ATTEMPT(AlarmRegister.ALARM_REGISTER_1, 13, false),
  // bits 14 and 15 are not used

  // Bits for group: M-Bus Alarms
  COMMUNICATION_ERROR_M_BUS_CHANNEL_1(AlarmRegister.ALARM_REGISTER_1, 16, false),
  COMMUNICATION_ERROR_M_BUS_CHANNEL_2(AlarmRegister.ALARM_REGISTER_1, 17, false),
  COMMUNICATION_ERROR_M_BUS_CHANNEL_3(AlarmRegister.ALARM_REGISTER_1, 18, false),
  COMMUNICATION_ERROR_M_BUS_CHANNEL_4(AlarmRegister.ALARM_REGISTER_1, 19, false),
  FRAUD_ATTEMPT_M_BUS_CHANNEL_1(AlarmRegister.ALARM_REGISTER_1, 20, false),
  FRAUD_ATTEMPT_M_BUS_CHANNEL_2(AlarmRegister.ALARM_REGISTER_1, 21, false),
  FRAUD_ATTEMPT_M_BUS_CHANNEL_3(AlarmRegister.ALARM_REGISTER_1, 22, false),
  FRAUD_ATTEMPT_M_BUS_CHANNEL_4(AlarmRegister.ALARM_REGISTER_1, 23, false),

  // Bits for group: Reserved
  NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1(AlarmRegister.ALARM_REGISTER_1, 24, false),
  NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2(AlarmRegister.ALARM_REGISTER_1, 25, false),
  NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3(AlarmRegister.ALARM_REGISTER_1, 26, false),
  NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4(AlarmRegister.ALARM_REGISTER_1, 27, false),

  PHASE_OUTAGE_DETECTED_L1(AlarmRegister.ALARM_REGISTER_1, 28, true),
  PHASE_OUTAGE_DETECTED_L2(AlarmRegister.ALARM_REGISTER_1, 29, true),
  PHASE_OUTAGE_DETECTED_L3(AlarmRegister.ALARM_REGISTER_1, 30, true),
  /*
   * This type is disabled, because it is a special type, that is not yet implemented
   *
   *  PHASE_OUTAGE_TEST_INDICATION(
   *     AlarmRegister.ALARM_REGISTER_1, 31, true),
   */

  // Bits for group: alarm register 2
  VOLTAGE_SAG_IN_PHASE_DETECTED_L1(AlarmRegister.ALARM_REGISTER_2, 0, true),
  VOLTAGE_SAG_IN_PHASE_DETECTED_L2(AlarmRegister.ALARM_REGISTER_2, 1, true),
  VOLTAGE_SAG_IN_PHASE_DETECTED_L3(AlarmRegister.ALARM_REGISTER_2, 2, true),
  VOLTAGE_SWELL_IN_PHASE_DETECTED_L1(AlarmRegister.ALARM_REGISTER_2, 3, true),
  VOLTAGE_SWELL_IN_PHASE_DETECTED_L2(AlarmRegister.ALARM_REGISTER_2, 4, true),
  VOLTAGE_SWELL_IN_PHASE_DETECTED_L3(AlarmRegister.ALARM_REGISTER_2, 5, true),

  LAST_GASP(AlarmRegister.ALARM_REGISTER_3, 0, true),
  LAST_GASP_TEST(AlarmRegister.ALARM_REGISTER_3, 1, true);

  final AlarmRegister alarmRegister;

  final int bit;

  final boolean pushedToP5;

  AlarmType(final AlarmRegister alarmRegister, final int bit, final boolean pushedToP5) {
    this.alarmRegister = alarmRegister;
    this.bit = bit;
    this.pushedToP5 = pushedToP5;
  }

  public static List<AlarmType> findAllByAlarmRegister(
      final AlarmRegister alarmRegister, final boolean pushedToP5) {
    return Arrays.stream(values())
        .filter(alarmType -> alarmType.getAlarmRegister().equals(alarmRegister))
        .filter(alarmType -> alarmType.isPushedToP5() == pushedToP5)
        .collect(Collectors.toList());
  }
}
