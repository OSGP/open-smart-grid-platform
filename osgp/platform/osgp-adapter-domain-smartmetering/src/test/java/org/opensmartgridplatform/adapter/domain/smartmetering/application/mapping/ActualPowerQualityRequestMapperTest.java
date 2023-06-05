// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest;

public class ActualPowerQualityRequestMapperTest {

  private final MonitoringMapper mapper = new MonitoringMapper();

  @Test
  public void testGetActualPowerQualityRequest() {
    final ActualPowerQualityRequest actualPowerQualityRequest =
        new ActualPowerQualityRequest("PUBLIC");
    final ActualPowerQualityRequest result =
        this.mapper.map(actualPowerQualityRequest, ActualPowerQualityRequest.class);
    assertThat(result).isNotNull().isInstanceOf(ActualPowerQualityRequest.class);
    assertThat(result.getProfileType()).isEqualTo("PUBLIC");
  }
}
