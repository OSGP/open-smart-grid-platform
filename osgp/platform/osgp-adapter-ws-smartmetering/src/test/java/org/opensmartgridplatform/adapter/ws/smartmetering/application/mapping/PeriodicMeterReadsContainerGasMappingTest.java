//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas;

public class PeriodicMeterReadsContainerGasMappingTest {

  private static final PeriodType PERIODTYPE = PeriodType.DAILY;
  private static final Date DATE = new Date();
  private static final BigDecimal VALUE = new BigDecimal(1.0);
  private static final OsgpUnit OSGP_UNIT = OsgpUnit.M3;
  private static final OsgpUnitType OSGP_UNITTYPE = OsgpUnitType.M_3;
  private static final AmrProfileStatusCodeFlag AMRCODEFLAG =
      AmrProfileStatusCodeFlag.CLOCK_INVALID;
  private MonitoringMapper monitoringMapper = new MonitoringMapper();

  /** Tests if mapping a PeriodicMeterReadsContainerGas object with an empty list succeeds. */
  @Test
  public void testWithEmptyList() {

    // build test data
    final List<PeriodicMeterReadsGas> periodicMeterReadsGasList = new ArrayList<>();
    final PeriodicMeterReadsContainerGas periodicMeterReadsContainerGas =
        new PeriodicMeterReadsContainerGas(PERIODTYPE, periodicMeterReadsGasList);

    // actual mapping
    final PeriodicMeterReadsGasResponse periodicMeterReadsGasResponse =
        this.monitoringMapper.map(
            periodicMeterReadsContainerGas, PeriodicMeterReadsGasResponse.class);

    // check mapping
    assertThat(periodicMeterReadsGasResponse).isNotNull();
    assertThat(periodicMeterReadsGasResponse.getPeriodicMeterReadsGas()).isNotNull();
    assertThat(periodicMeterReadsGasResponse.getPeriodType()).isNotNull();

    assertThat(periodicMeterReadsGasResponse.getPeriodicMeterReadsGas().isEmpty()).isTrue();
    assertThat(periodicMeterReadsGasResponse.getPeriodType().name()).isEqualTo(PERIODTYPE.name());
  }

  /**
   * Tests if mapping a PeriodicMeterReadsContainerGas object with a filled List and Set succeeds.
   */
  @Test
  public void testWithFilledList() {

    // build test data
    final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(VALUE, OSGP_UNIT);
    final Set<AmrProfileStatusCodeFlag> flagSet = new TreeSet<>();
    flagSet.add(AMRCODEFLAG);
    final AmrProfileStatusCode amrProfileStatusCode = new AmrProfileStatusCode(flagSet);

    final PeriodicMeterReadsGas periodicMeterReadsGas =
        new PeriodicMeterReadsGas(DATE, osgpMeterValue, DATE, amrProfileStatusCode);
    final List<PeriodicMeterReadsGas> periodicMeterReadsList = new ArrayList<>();
    periodicMeterReadsList.add(periodicMeterReadsGas);
    final PeriodicMeterReadsContainerGas periodicMeterReadsContainer =
        new PeriodicMeterReadsContainerGas(PERIODTYPE, periodicMeterReadsList);

    // actual mapping
    final PeriodicMeterReadsGasResponse periodicMeterReadsResponseGas =
        this.monitoringMapper.map(periodicMeterReadsContainer, PeriodicMeterReadsGasResponse.class);

    // check mapping

    assertThat(periodicMeterReadsResponseGas).isNotNull();
    assertThat(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas()).isNotNull();
    assertThat(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0)).isNotNull();
    assertThat(
            periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getAmrProfileStatusCode())
        .isNotNull();
    assertThat(
            periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlag())
        .isNotNull();
    assertThat(
            periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlag()
                .get(0))
        .isNotNull();
    assertThat(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().get(0).getConsumption())
        .isNotNull();
    assertThat(
            periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getConsumption()
                .getUnit())
        .isNotNull();
    assertThat(
            periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getConsumption()
                .getValue())
        .isNotNull();

    assertThat(periodicMeterReadsResponseGas.getPeriodType().name()).isEqualTo(PERIODTYPE.name());
    assertThat(periodicMeterReadsResponseGas.getPeriodicMeterReadsGas().size())
        .isEqualTo(periodicMeterReadsList.size());
    assertThat(
            periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getConsumption()
                .getValue())
        .isEqualTo(VALUE);
    assertThat(
            periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getConsumption()
                .getUnit())
        .isEqualTo(OSGP_UNITTYPE);
    assertThat(
            periodicMeterReadsResponseGas
                .getPeriodicMeterReadsGas()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlag()
                .get(0)
                .name())
        .isEqualTo(AMRCODEFLAG.name());
    // For more information on the mapping of Date to XmlGregorianCalendar
    // objects, refer to the DateMappingTest
  }
}
