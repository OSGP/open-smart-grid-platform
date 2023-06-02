//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmRegister;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

public class AlarmRegisterMappingTest {

  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  // Constructor for AlarmRegister(Dto) doesn´t allow a null Set.
  @Test
  public void testWithNullSet() {
    final Set<AlarmTypeDto> alarmTypes = null;
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              new AlarmRegisterResponseDto(alarmTypes);
            });
  }

  // Test if mapping with an empty set succeeds
  @Test
  public void testWithEmptySet() {
    // build test data
    final Set<AlarmTypeDto> alarmTypes = new TreeSet<>();
    final AlarmRegisterResponseDto alarmRegisterDto = new AlarmRegisterResponseDto(alarmTypes);
    // actual mapping
    final AlarmRegister alarmRegister =
        this.monitoringMapper.map(alarmRegisterDto, AlarmRegister.class);
    // test mapping
    assertThat(alarmRegister).isNotNull();
    assertThat(alarmRegister.getAlarmTypes()).isEmpty();
  }

  // Test if mapping with a non-empty set succeeds
  @Test
  public void testWithNonEmptySet() {
    // build test data
    final Set<AlarmTypeDto> alarmTypes = new TreeSet<>();
    alarmTypes.add(AlarmTypeDto.CLOCK_INVALID);
    final AlarmRegisterResponseDto alarmRegisterDto = new AlarmRegisterResponseDto(alarmTypes);
    // actual mapping
    final AlarmRegister alarmRegister =
        this.monitoringMapper.map(alarmRegisterDto, AlarmRegister.class);
    // test mapping
    assertThat(alarmRegister).isNotNull();
    assertThat(alarmRegister.getAlarmTypes().size())
        .isEqualTo(alarmRegisterDto.getAlarmTypes().size());
    assertThat(alarmRegister.getAlarmTypes().contains(AlarmType.CLOCK_INVALID)).isTrue();
  }
}
