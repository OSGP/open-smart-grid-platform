// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequestData;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;

public class GetPowerQualityProfileDataMapperTest {

  private static final String MAPPED_FIELD_VALUE_MESSAGE =
      "Mapped field should have the same value.";

  private static final ZonedDateTime BEGIN_DATE =
      ZonedDateTime.of(2017, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
  private static final ZonedDateTime END_DATE =
      ZonedDateTime.of(2017, 2, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

  private final MonitoringMapper mapper = new MonitoringMapper();

  @Test
  public void shouldConvertValueObjectToDto() {
    final GetPowerQualityProfileRequestData source =
        new GetPowerQualityProfileRequestData(
            "PUBLIC", Date.from(BEGIN_DATE.toInstant()), Date.from(END_DATE.toInstant()));
    final GetPowerQualityProfileRequestDataDto result =
        this.mapper.map(source, GetPowerQualityProfileRequestDataDto.class);

    assertThat(result.getBeginDate())
        .withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(Date.from(BEGIN_DATE.toInstant()));
    assertThat(result.getEndDate())
        .withFailMessage(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(Date.from(END_DATE.toInstant()));
  }
}
