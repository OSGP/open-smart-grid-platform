/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MeterReadsGas;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;

public class MeterReadsGasMappingTest {

  private static final Date DATE = new Date();
  private static final BigDecimal VALUE = new BigDecimal(1.0);
  private static final OsgpUnit OSGP_UNIT = OsgpUnit.M3;
  private static final OsgpUnitType OSGP_UNITTYPE = OsgpUnitType.M_3;
  private MonitoringMapper monitoringMapper = new MonitoringMapper();

  /** Tests if a MeterReadsGas object can be mapped */
  @Test
  public void testMeterReadsGasMapping() {

    // build test data
    final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(VALUE, OSGP_UNIT);
    final MeterReadsGas meterReadsGas = new MeterReadsGas(DATE, osgpMeterValue, DATE);

    // actual mapping
    final ActualMeterReadsGasResponse actualMeterReadsGasResponse =
        this.monitoringMapper.map(meterReadsGas, ActualMeterReadsGasResponse.class);

    // check mapping
    assertThat(actualMeterReadsGasResponse).isNotNull();
    assertThat(actualMeterReadsGasResponse.getConsumption()).isNotNull();
    assertThat(actualMeterReadsGasResponse.getConsumption().getUnit()).isNotNull();
    assertThat(actualMeterReadsGasResponse.getConsumption().getValue()).isNotNull();

    assertThat(actualMeterReadsGasResponse.getConsumption().getUnit()).isEqualTo(OSGP_UNITTYPE);
    assertThat(actualMeterReadsGasResponse.getConsumption().getValue()).isEqualTo(VALUE);
    // For more information on the mapping of Date to XmlGregorianCalendar
    // objects, refer to the DateMappingTest
  }
}
