// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActiveEnergyValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MeterReads;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActiveEnergyValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsResponseDto;

public class MonitoringMapperTest {
  private MonitoringMapper mapper;

  @BeforeEach
  public void setUp() throws Exception {
    this.mapper = new MonitoringMapper();
    this.mapper.configure(new DefaultMapperFactory.Builder().build());
  }

  @Test
  public void convertsMeterReadsResponseDtoToMeterReads() {
    final Date logTime = new Date(123L * 24 * 60 * 60 * 1000);
    final ActiveEnergyValuesDto activeEnergyValuesDto =
        new ActiveEnergyValuesDto(
            new DlmsMeterValueDto(new BigDecimal("12.34"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.35"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.36"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.37"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.38"), DlmsUnitTypeDto.M3),
            new DlmsMeterValueDto(new BigDecimal("12.39"), DlmsUnitTypeDto.M3));
    final MeterReadsResponseDto source = new MeterReadsResponseDto(logTime, activeEnergyValuesDto);

    final MeterReads result = this.mapper.map(source, MeterReads.class);

    final ActiveEnergyValues activeEnergyValues =
        new ActiveEnergyValues(
            new OsgpMeterValue(new BigDecimal("12.340"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.350"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.360"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.370"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.380"), OsgpUnit.M3),
            new OsgpMeterValue(new BigDecimal("12.390"), OsgpUnit.M3));
    final MeterReads expected = new MeterReads(logTime, activeEnergyValues);
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);
  }
}
