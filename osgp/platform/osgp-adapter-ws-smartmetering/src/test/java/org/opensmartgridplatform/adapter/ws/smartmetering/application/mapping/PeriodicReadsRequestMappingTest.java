//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;

public class PeriodicReadsRequestMappingTest {

  private static final PeriodType PERIODTYPE = PeriodType.DAILY;
  private XMLGregorianCalendar xmlCalendar;
  private final MonitoringMapper monitoringMapper = new MonitoringMapper();

  /** Needed to initialize a XMLGregorianCalendar object. */
  @BeforeEach
  public void init() {
    try {
      this.xmlCalendar =
          DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
    } catch (final DatatypeConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests if a PeriodicReadsRequest object is mapped successfully when it is completely
   * initialized.
   */
  @Test
  public void testCompletePeriodicReadsRequestMapping() {

    // build test data
    final PeriodicReadsRequestData periodicReadsRequestData = new PeriodicReadsRequestData();
    periodicReadsRequestData.setBeginDate(this.xmlCalendar);
    periodicReadsRequestData.setEndDate(this.xmlCalendar);
    periodicReadsRequestData.setPeriodType(PERIODTYPE);
    final PeriodicReadsRequest periodicReadsRequest = new PeriodicReadsRequest();
    periodicReadsRequest.setPeriodicReadsRequestData(periodicReadsRequestData);

    // actual mapping
    final PeriodicMeterReadsQuery periodicMeterReadsQuery =
        this.monitoringMapper.map(periodicReadsRequest, PeriodicMeterReadsQuery.class);

    // check mapping
    assertThat(periodicMeterReadsQuery).isNotNull();
    assertThat(periodicMeterReadsQuery.getDeviceIdentification()).isNotNull();
    assertThat(periodicMeterReadsQuery.getPeriodType()).isNotNull();
    assertThat(periodicMeterReadsQuery.getBeginDate()).isNotNull();
    assertThat(periodicMeterReadsQuery.getEndDate()).isNotNull();

    assertThat(periodicMeterReadsQuery.getPeriodType().name()).isEqualTo(PERIODTYPE.name());
    assertThat(periodicMeterReadsQuery.isMbusDevice()).isFalse();
    assertThat(periodicMeterReadsQuery.getDeviceIdentification().isEmpty()).isTrue();
    // For more information on the mapping of Date to XmlGregorianCalendar
    // objects, refer to the DateMappingTest

  }

  /**
   * Tests if a NullPointerException is thrown when a PeriodicReadsRequest - with a
   * PeriodicReadsRequestData that is null - is mapped.
   */
  @Test
  public void testWithNullPeriodicReadsRequestData() {
    // build test data
    final PeriodicReadsRequest periodicReadsRequest = new PeriodicReadsRequest();
    periodicReadsRequest.setPeriodicReadsRequestData(null);

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              // actual mapping
              this.monitoringMapper.map(periodicReadsRequest, PeriodicMeterReadsQuery.class);
            });
  }
}
