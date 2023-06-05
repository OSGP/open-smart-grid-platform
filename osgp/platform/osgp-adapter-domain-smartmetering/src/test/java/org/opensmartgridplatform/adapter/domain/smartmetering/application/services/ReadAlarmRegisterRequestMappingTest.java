// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;

public class ReadAlarmRegisterRequestMappingTest {

  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  // Test if mapping a ReadAlarmRegisterRequest succeeds when it's String is
  // non-null
  @Test
  public void testReadAlarmRegisterRequestMappingTest() {
    // build test data
    final String deviceId = "device1";
    final ReadAlarmRegisterRequest request = new ReadAlarmRegisterRequest(deviceId);
    // actual mapping
    final ReadAlarmRegisterRequestDto requestDto =
        this.monitoringMapper.map(request, ReadAlarmRegisterRequestDto.class);
    // test mapping
    assertThat(requestDto).isNotNull();
    assertThat(requestDto.getDeviceIdentification()).isEqualTo(deviceId);
  }

  // Test if mapping a ReadAlarmRegisterRequest succeeds when it's String is
  // null
  @Test
  public void testWithNullString() {
    // build test data
    final String deviceId = null;
    final ReadAlarmRegisterRequest request = new ReadAlarmRegisterRequest(deviceId);
    // actual mapping
    final ReadAlarmRegisterRequestDto requestDto =
        this.monitoringMapper.map(request, ReadAlarmRegisterRequestDto.class);
    // test mapping
    assertThat(requestDto).isNotNull();
    assertThat(requestDto.getDeviceIdentification()).isNull();
  }
}
