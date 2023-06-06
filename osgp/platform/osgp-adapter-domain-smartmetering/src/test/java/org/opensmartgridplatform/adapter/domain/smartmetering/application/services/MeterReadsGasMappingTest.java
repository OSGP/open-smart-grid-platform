// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MeterReadsGas;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsGasResponseDto;

public class MeterReadsGasMappingTest {

  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  // Test the mapping of a complete MeterReadsGasDto object
  @Test
  public void testMeterReadsGasMappingTest() {
    // build test data
    final DlmsMeterValueDto consumption =
        new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitTypeDto.M3);
    final MeterReadsGasResponseDto meterReadsGasDto =
        new MeterReadsGasResponseDto(new Date(), consumption, new Date());
    // actual mapping
    final MeterReadsGas meterReadsGas =
        this.monitoringMapper.map(meterReadsGasDto, MeterReadsGas.class);
    // test mapping
    assertThat(meterReadsGas).isNotNull();
    assertThat(meterReadsGas.getLogTime()).isEqualTo(meterReadsGasDto.getLogTime());
    assertThat(meterReadsGas.getCaptureTime()).isEqualTo(meterReadsGasDto.getCaptureTime());

    final BigDecimal bigDecimal1 = consumption.getValue();
    final BigDecimal bigDecimal2 = meterReadsGas.getConsumption().getValue();
    assertThat(bigDecimal1.compareTo(bigDecimal2) == 0).isTrue();
    assertThat(meterReadsGas.getConsumption().getOsgpUnit()).isEqualTo(OsgpUnit.M3);
  }

  // Test mapping when DlmsMeterValue is null;
  @Test
  public void testWithNullDlmsMeterValueDto() {
    // build test data
    final DlmsMeterValueDto consumption = null;
    final MeterReadsGasResponseDto meterReadsGasDto =
        new MeterReadsGasResponseDto(new Date(), consumption, new Date());
    // actual mapping
    final MeterReadsGas meterReadsGas =
        this.monitoringMapper.map(meterReadsGasDto, MeterReadsGas.class);
    // test mapping
    assertThat(meterReadsGas).isNotNull();
    assertThat(meterReadsGas.getLogTime()).isEqualTo(meterReadsGasDto.getLogTime());
    assertThat(meterReadsGas.getCaptureTime()).isEqualTo(meterReadsGasDto.getCaptureTime());
    assertThat(meterReadsGas.getConsumption()).isNull();
  }

  // Dates can never be null, because of the way the constructor for a
  // MeterReadsGasDto is defined.
  @Test
  public void testWithNullDates() {
    final DlmsMeterValueDto consumption =
        new DlmsMeterValueDto(new BigDecimal(1.0), DlmsUnitTypeDto.M3);
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              new MeterReadsGasResponseDto(null, consumption, null);
            });
  }
}
