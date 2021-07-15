/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
