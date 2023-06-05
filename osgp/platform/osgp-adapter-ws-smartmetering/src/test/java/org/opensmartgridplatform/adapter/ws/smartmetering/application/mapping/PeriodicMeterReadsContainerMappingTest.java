// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCodeFlag;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReads;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;

public class PeriodicMeterReadsContainerMappingTest {

  private static final PeriodType PERIODTYPE = PeriodType.DAILY;
  private static final Date DATE = new Date();
  private static final BigDecimal VALUE = new BigDecimal(1.0);
  private static final OsgpUnit OSGP_UNIT = OsgpUnit.M3;
  private static final OsgpUnitType OSGP_UNITTYPE = OsgpUnitType.M_3;
  private static final AmrProfileStatusCodeFlag AMRCODEFLAG =
      AmrProfileStatusCodeFlag.CLOCK_INVALID;
  private MonitoringMapper monitoringMapper = new MonitoringMapper();

  /** Tests the mapping of a PeriodicMeterReadsContainer object with a filled List and Set. */
  @Test
  public void testMappingWithFilledListAndSet() {

    // build test data
    final OsgpMeterValue osgpMeterValue = new OsgpMeterValue(VALUE, OSGP_UNIT);
    final Set<AmrProfileStatusCodeFlag> flagSet = new TreeSet<>();
    flagSet.add(AMRCODEFLAG);
    final AmrProfileStatusCode amrProfileStatusCode = new AmrProfileStatusCode(flagSet);

    final PeriodicMeterReads periodicMeterReads =
        new PeriodicMeterReads(DATE, osgpMeterValue, osgpMeterValue, amrProfileStatusCode);
    final List<PeriodicMeterReads> periodicMeterReadsList = new ArrayList<>();
    periodicMeterReadsList.add(periodicMeterReads);
    final PeriodicMeterReadsContainer periodicMeterReadsContainer =
        new PeriodicMeterReadsContainer(PERIODTYPE, periodicMeterReadsList);

    // actual mapping
    final PeriodicMeterReadsResponse periodicMeterReadsResponse =
        this.monitoringMapper.map(periodicMeterReadsContainer, PeriodicMeterReadsResponse.class);

    // check mapping
    assertThat(periodicMeterReadsResponse).isNotNull();
    assertThat(periodicMeterReadsResponse.getPeriodicMeterReads()).isNotNull();
    assertThat(periodicMeterReadsResponse.getPeriodType()).isNotNull();
    assertThat(periodicMeterReadsResponse.getPeriodicMeterReads().get(0)).isNotNull();
    assertThat(periodicMeterReadsResponse.getPeriodicMeterReads().get(0).getActiveEnergyExport())
        .isNotNull();
    assertThat(periodicMeterReadsResponse.getPeriodicMeterReads().get(0).getActiveEnergyImport())
        .isNotNull();
    assertThat(periodicMeterReadsResponse.getPeriodicMeterReads().get(0).getAmrProfileStatusCode())
        .isNotNull();

    assertThat(periodicMeterReadsResponse.getPeriodType().name()).isEqualTo(PERIODTYPE.name());
    assertThat(periodicMeterReadsResponse.getPeriodicMeterReads().size())
        .isEqualTo(periodicMeterReadsList.size());
    assertThat(
            periodicMeterReadsResponse
                .getPeriodicMeterReads()
                .get(0)
                .getActiveEnergyImport()
                .getValue())
        .isEqualTo(VALUE);
    assertThat(
            periodicMeterReadsResponse
                .getPeriodicMeterReads()
                .get(0)
                .getActiveEnergyImport()
                .getUnit()
                .name())
        .isEqualTo(OSGP_UNITTYPE.name());
    assertThat(
            periodicMeterReadsResponse
                .getPeriodicMeterReads()
                .get(0)
                .getActiveEnergyExport()
                .getValue())
        .isEqualTo(VALUE);
    assertThat(
            periodicMeterReadsResponse
                .getPeriodicMeterReads()
                .get(0)
                .getActiveEnergyExport()
                .getUnit()
                .name())
        .isEqualTo(OSGP_UNITTYPE.name());
    assertThat(
            periodicMeterReadsResponse
                .getPeriodicMeterReads()
                .get(0)
                .getAmrProfileStatusCode()
                .getAmrProfileStatusCodeFlag()
                .get(0)
                .name())
        .isEqualTo(AMRCODEFLAG.name());
    // For more information on the mapping of Date to XmlGregorianCalendar
    // objects, refer to the DateMappingTest
  }

  /** Tests the mapping of a PeriodMeterReadsContainer object with an empty List. */
  @Test
  public void testWithEmptyList() {

    // build test data
    final List<PeriodicMeterReads> periodicMeterReadsList = new ArrayList<>();
    final PeriodicMeterReadsContainer periodicMeterReadsContainer =
        new PeriodicMeterReadsContainer(PERIODTYPE, periodicMeterReadsList);

    // actual mapping
    final PeriodicMeterReadsResponse periodicMeterReadsResponse =
        this.monitoringMapper.map(periodicMeterReadsContainer, PeriodicMeterReadsResponse.class);

    // check mapping
    assertThat(periodicMeterReadsResponse).isNotNull();
    assertThat(periodicMeterReadsResponse.getPeriodicMeterReads().isEmpty()).isTrue();
    assertThat(periodicMeterReadsResponse.getPeriodType().name()).isEqualTo(PERIODTYPE.name());
  }
}
