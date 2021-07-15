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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PowerQualityValue;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityValueDto;

public class ActualPowerQualityResponseDataMapperTest {

  private final MonitoringMapper mapper = new MonitoringMapper();

  private static final Class<?>[] EXPECTED_CLASS =
      new Class<?>[] {String.class, Date.class, BigDecimal.class, Long.class};

  @Test
  public void testConvertActualPowerQualityResponse() {
    final ActualPowerQualityDataDto responseDto =
        new ActualPowerQualityDataDto(
            new ArrayList<PowerQualityObjectDto>(), this.makePowerQualityValueDtos());
    final ActualPowerQualityData response =
        this.mapper.map(responseDto, ActualPowerQualityData.class);
    assertThat(response).isNotNull();

    assertThat(response.getPowerQualityValues()).hasSize(EXPECTED_CLASS.length);

    int i = 0;
    for (final PowerQualityValue powerQualityValue : response.getPowerQualityValues()) {
      final Class<?> clazz = powerQualityValue.getValue().getClass();
      assertThat(clazz)
          .withFailMessage("the return class should be of the same type")
          .isEqualTo(EXPECTED_CLASS[i++]);
    }
  }

  private List<PowerQualityValueDto> makePowerQualityValueDtos() {
    return Arrays.asList(
        new PowerQualityValueDto("Test"),
        new PowerQualityValueDto(new Date()),
        new PowerQualityValueDto(new BigDecimal(123.45d)),
        new PowerQualityValueDto(12345L));
  }
}
