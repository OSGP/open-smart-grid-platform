// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequest;

public class GetPowerQualityProfileRequestMappingTest {

  private static final String DEVICE_NAME = "TEST10240000001";

  private static final Date DATE = new Date();
  private final MonitoringMapper mapper = new MonitoringMapper();

  @Test
  public void convertGetPowerQualityProfileRequest() {
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .GetPowerQualityProfileRequest
        source = this.makeRequest();
    final Object result = this.mapper.map(source, GetPowerQualityProfileRequest.class);

    assertThat(result)
        .as("mapping GetPowerQualityProfileRequest should not return null")
        .isNotNull();
    assertThat(result)
        .as("mapping GetPowerQualityProfileRequest  should return correct type")
        .isInstanceOf(GetPowerQualityProfileRequest.class);

    final GetPowerQualityProfileRequest target = (GetPowerQualityProfileRequest) result;

    assertThat(target.getDeviceIdentification()).isEqualTo(source.getDeviceIdentification());
    assertThat(target.getProfileType()).isEqualTo(source.getProfileType());
    final ZonedDateTime targetEndDate =
        ZonedDateTime.ofInstant(target.getEndDate().toInstant(), ZoneId.systemDefault());
    assertThat(targetEndDate.getYear()).isEqualTo(source.getBeginDate().getYear());
  }

  private XMLGregorianCalendar makeGregorianCalendar() {
    final GregorianCalendar gcal = new GregorianCalendar();
    gcal.setTime(DATE);
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
    } catch (final DatatypeConfigurationException e) {
      throw new RuntimeException("error creating XMLGregorianCalendar");
    }
  }

  private org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
          .GetPowerQualityProfileRequest
      makeRequest() {
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .GetPowerQualityProfileRequest
        result =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                .GetPowerQualityProfileRequest();
    result.setProfileType("PRIVATE");
    result.setDeviceIdentification(DEVICE_NAME);
    result.setBeginDate(this.makeGregorianCalendar());
    result.setEndDate(this.makeGregorianCalendar());
    return result;
  }
}
