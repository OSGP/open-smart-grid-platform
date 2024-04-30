// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
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
    final long registerValue = Long.parseLong("10000001000100010001000100000001", 2);

    final Set<AlarmTypeDto> alarmTypes =
        this.alarmHelperService.toAlarmTypes(DlmsObjectType.ALARM_REGISTER_1, registerValue);

    assertThat(alarmTypes)
        .containsExactlyInAnyOrder(
            AlarmTypeDto.CLOCK_INVALID,
            AlarmTypeDto.PROGRAM_MEMORY_ERROR,
            AlarmTypeDto.WATCHDOG_ERROR,
            AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1,
            AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1,
            AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1,
            AlarmTypeDto.PHASE_OUTAGE_TEST_INDICATION);
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
    alarmTypes.add(AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L1);
    alarmTypes.add(AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L2);
    alarmTypes.add(AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L3);
    alarmTypes.add(AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L1);
    alarmTypes.add(AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L2);
    alarmTypes.add(AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L3);

    assertThat(
            (long) this.alarmHelperService.toLongValue(DlmsObjectType.ALARM_REGISTER_2, alarmTypes))
        .isEqualTo(16191L);
  }

  @Test
  void testConvertToLongAlarmRegister3() {
    final Set<AlarmTypeDto> alarmTypes = new HashSet<>();

    alarmTypes.add(AlarmTypeDto.LAST_GASP);
    alarmTypes.add(AlarmTypeDto.LAST_GASP_TEST);

    assertThat(
            (long) this.alarmHelperService.toLongValue(DlmsObjectType.ALARM_REGISTER_3, alarmTypes))
        .isEqualTo(3L);
  }

  @Test
  void testConvertToAlarmTypesAlarmRegister2() {
    final long registerValue = Long.parseLong("11111111111111", 2);
    final AlarmTypeDto emptyBit = null;

    final Set<AlarmTypeDto> alarmTypes =
        this.alarmHelperService.toAlarmTypes(DlmsObjectType.ALARM_REGISTER_2, registerValue);

    assertThat(alarmTypes)
        .containsExactlyInAnyOrder(
            AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L1,
            AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L2,
            AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L3,
            AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L1,
            AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L2,
            AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L3,
            AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L1,
            AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L2,
            AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L3,
            AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L1,
            AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L2,
            AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L3,
            emptyBit);
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

    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L1))
        .isEqualTo(8);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L2))
        .isEqualTo(9);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L3))
        .isEqualTo(10);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L1))
        .isEqualTo(11);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L2))
        .isEqualTo(12);
    assertThat((int) this.toBitPositionRegister(AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L3))
        .isEqualTo(13);
  }

  private int toBitPositionRegister(final AlarmTypeDto alarmTypeDto) {
    return this.alarmHelperService.toBitPosition(alarmTypeDto);
  }
}
