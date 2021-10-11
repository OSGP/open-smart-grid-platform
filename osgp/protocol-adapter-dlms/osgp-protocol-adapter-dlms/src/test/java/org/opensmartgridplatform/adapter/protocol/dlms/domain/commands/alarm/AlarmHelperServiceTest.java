/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

class AlarmHelperServiceTest {

  private final AlarmHelperService alarmHelperService = new AlarmHelperService();

  @Test
  void testConvertToLong() {
    final Set<AlarmTypeDto> alarmTypes = new HashSet<>();

    alarmTypes.add(AlarmTypeDto.CLOCK_INVALID);
    alarmTypes.add(AlarmTypeDto.PROGRAM_MEMORY_ERROR);
    alarmTypes.add(AlarmTypeDto.WATCHDOG_ERROR);
    alarmTypes.add(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1);
    alarmTypes.add(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1);
    alarmTypes.add(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1);

    assertThat(
            (long) this.alarmHelperService.toLongValue(DlmsObjectType.ALARM_REGISTER_1, alarmTypes))
        .isEqualTo(17895681L);
  }

  @Test
  void testConvertToAlarmTypes() {
    final long registerValue = Long.parseLong("00000001000100010001000100000001", 2);

    final Set<AlarmTypeDto> alarmTypes =
        this.alarmHelperService.toAlarmTypes(DlmsObjectType.ALARM_REGISTER_1, registerValue);

    assertThat(alarmTypes.contains(AlarmTypeDto.CLOCK_INVALID)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.PROGRAM_MEMORY_ERROR)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.WATCHDOG_ERROR)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1)).isTrue();
  }

  @Test
  void testBitPositions() {
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.CLOCK_INVALID)).isZero();
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.PROGRAM_MEMORY_ERROR)).isEqualTo(8);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.WATCHDOG_ERROR)).isEqualTo(12);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1))
        .isEqualTo(16);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1))
        .isEqualTo(20);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1))
        .isEqualTo(24);
  }

  @Test
  void testConvertToLongAlarmRegister2() {
    final Set<AlarmTypeDto> alarmTypes = new HashSet<>();

    alarmTypes.add(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L1);
    alarmTypes.add(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L2);
    alarmTypes.add(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L3);
    alarmTypes.add(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L1);
    alarmTypes.add(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L2);
    alarmTypes.add(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L3);

    assertThat(
            (long) this.alarmHelperService.toLongValue(DlmsObjectType.ALARM_REGISTER_2, alarmTypes))
        .isEqualTo(63L);
  }

  @Test
  void testConvertToAlarmTypesAlarmRegister2() {
    final long registerValue = Long.parseLong("00000000000000000000000001111111", 2);

    final Set<AlarmTypeDto> alarmTypes =
        this.alarmHelperService.toAlarmTypes(DlmsObjectType.ALARM_REGISTER_2, registerValue);

    assertThat(alarmTypes.contains(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L1)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L2)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L3)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L1)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L2)).isTrue();
    assertThat(alarmTypes.contains(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L3)).isTrue();
  }

  @Test
  void testBitPositionsAlarmRegister2() {
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L1))
        .isZero();
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L2))
        .isEqualTo(1);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L3))
        .isEqualTo(2);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L1))
        .isEqualTo(3);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L2))
        .isEqualTo(4);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L3))
        .isEqualTo(5);
  }

  private int toBitPositionRegister(final AlarmTypeDto alarmTypeDto) {
    return this.alarmHelperService.toBitPosition(alarmTypeDto);
  }
}
