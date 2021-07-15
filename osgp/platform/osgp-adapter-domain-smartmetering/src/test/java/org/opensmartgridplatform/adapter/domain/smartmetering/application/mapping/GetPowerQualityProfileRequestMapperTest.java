/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequestData;

public class GetPowerQualityProfileRequestMapperTest {

  private final MonitoringMapper mapper = new MonitoringMapper();

  private static final Date DATE = new Date();
  private static final String DEVICE_NAME = "TEST10240000001";
  private static final String MAPPED_VALUE_MESSAGE = "mapped values should be identical.";

  @Test
  public void testGetPowerQualityProfileRequest() {
    final GetPowerQualityProfileRequest getPowerQualityProfileRequest = this.makeRequest();
    final GetPowerQualityProfileRequestData result =
        this.mapper.map(getPowerQualityProfileRequest, GetPowerQualityProfileRequestData.class);
    assertThat(result)
        .withFailMessage("mapping GetPowerQualityProfileRequest should not return null")
        .isNotNull();

    assertThat(result)
        .withFailMessage("mapping GetPowerQualityProfileRequest should return correct type")
        .isOfAnyClassIn(GetPowerQualityProfileRequestData.class);

    assertThat(result.getBeginDate()).withFailMessage(MAPPED_VALUE_MESSAGE).isEqualTo(DATE);
    assertThat(result.getEndDate()).withFailMessage(MAPPED_VALUE_MESSAGE).isEqualTo(DATE);
  }

  private GetPowerQualityProfileRequest makeRequest() {
    return new GetPowerQualityProfileRequest("PUBLIC", DATE, DATE, DEVICE_NAME);
  }
}
