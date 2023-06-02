//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;

public class DlmsMeterValueConverterTest {

  @Test
  public void testCalculate() {
    final MonitoringMapper calculator = new MonitoringMapper();
    DlmsMeterValueDto response =
        new DlmsMeterValueDto(BigDecimal.valueOf(123456), DlmsUnitTypeDto.KWH);
    assertThat(calculator.map(response, OsgpMeterValue.class).getValue())
        .isEqualTo(BigDecimal.valueOf(123.456d));
    assertThat(calculator.map(response, OsgpMeterValue.class).getOsgpUnit())
        .isEqualTo(OsgpUnit.KWH);

    response = new DlmsMeterValueDto(BigDecimal.valueOf(123456), DlmsUnitTypeDto.M3);
    assertThat(calculator.map(response, OsgpMeterValue.class).getValue())
        .isEqualTo(BigDecimal.valueOf(123456d));
    assertThat(calculator.map(response, OsgpMeterValue.class).getOsgpUnit()).isEqualTo(OsgpUnit.M3);

    response = new DlmsMeterValueDto(BigDecimal.valueOf(123456), DlmsUnitTypeDto.M3_CORR);
    assertThat(calculator.map(response, OsgpMeterValue.class).getValue())
        .isEqualTo(BigDecimal.valueOf(123456d));
    assertThat(calculator.map(response, OsgpMeterValue.class).getOsgpUnit()).isEqualTo(OsgpUnit.M3);

    response = new DlmsMeterValueDto(BigDecimal.valueOf(123456), DlmsUnitTypeDto.MONTH);
    try {
      calculator.map(response, OsgpMeterValue.class);
      fail("dlms unit A not supported, expected IllegalArgumentException");
    } catch (final IllegalArgumentException ex) {

    }
  }
}
