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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActiveEnergyValues;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MeterReads;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;

public class MeterReadsMappingTest {

  private static final Date DATE = new Date();
  private static final BigDecimal VALUE = new BigDecimal(1.0);
  private static final OsgpUnit OSGP_UNIT = OsgpUnit.M3;
  private static final OsgpUnitType OSGP_UNITTYPE = OsgpUnitType.M_3;
  private MonitoringMapper monitoringMapper = new MonitoringMapper();

  /** Method checks mapping of OsgpMeterValue objects to MeterValue objects. */
  private void checkOsgpMeterValueMapping(final MeterValue meterValue) {

    assertThat(meterValue).isNotNull();
    assertThat(meterValue.getUnit()).isEqualTo(OSGP_UNITTYPE);
    assertThat(meterValue.getValue()).isEqualTo(VALUE);
  }

  /** Tests if a MeterReads object can be mapped. */
  @Test
  public void testMeterReadsMapping() {

    // build test data
    final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(VALUE, OSGP_UNIT);
    final MeterReads meterReads =
        new MeterReads(
            DATE,
            new ActiveEnergyValues(
                osgpMeterValue,
                osgpMeterValue,
                osgpMeterValue,
                osgpMeterValue,
                osgpMeterValue,
                osgpMeterValue));

    // actual mapping
    final ActualMeterReadsResponse actualMeterReadsResponse =
        this.monitoringMapper.map(meterReads, ActualMeterReadsResponse.class);

    // check mapping
    assertThat(actualMeterReadsResponse).isNotNull();
    assertThat(actualMeterReadsResponse.getLogTime()).isNotNull();
    this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyExport());
    this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyExportTariffOne());
    this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyExportTariffTwo());
    this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyImport());
    this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyImportTariffOne());
    this.checkOsgpMeterValueMapping(actualMeterReadsResponse.getActiveEnergyImportTariffTwo());
    // For more information on the mapping of Date to XmlGregorianCalendar
    // objects, refer to the DateMappingTest
  }
}
