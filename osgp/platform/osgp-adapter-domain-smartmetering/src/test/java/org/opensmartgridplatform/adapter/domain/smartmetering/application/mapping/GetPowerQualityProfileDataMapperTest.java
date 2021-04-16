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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;

public class GetPowerQualityProfileDataMapperTest {

  private static final String MAPPED_FIELD_VALUE_MESSAGE =
      "Mapped field should have the same value.";

  private static final DateTime BEGIN_DATE = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);
  private static final DateTime END_DATE = new DateTime(2017, 2, 1, 0, 0, 0, DateTimeZone.UTC);

  private final MonitoringMapper mapper = new MonitoringMapper();

  @Test
  public void shouldConvertValueObjectToDto() {
    final GetPowerQualityProfileRequestData source =
        new GetPowerQualityProfileRequestData("PUBLIC", BEGIN_DATE.toDate(), END_DATE.toDate());
    final GetPowerQualityProfileRequestDataDto result =
        this.mapper.map(source, GetPowerQualityProfileRequestDataDto.class);

    assertThat(result.getBeginDate())
        .withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(BEGIN_DATE.toDate());
    assertThat(result.getEndDate())
        .withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(END_DATE.toDate());
  }
}
