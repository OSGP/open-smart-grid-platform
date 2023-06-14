// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequestData;

public class GetGetPowerQualityProfileRequestMappingTest {

  private static final String MAPPED_OBJECT_NULL_MESSAGE = "Mapped object should not be null.";
  private static final String MAPPED_FIELD_VALUE_MESSAGE =
      "Mapped field should have the same value.";

  private static final DateTime BEGIN_DATE = new DateTime(2017, 1, 1, 0, 0, 0, DateTimeZone.UTC);
  private static final DateTime END_DATE = new DateTime(2017, 2, 1, 0, 0, 0, DateTimeZone.UTC);

  private final MonitoringMapper mapper = new MonitoringMapper();

  private GetPowerQualityProfileRequest makeRequest() {
    final GetPowerQualityProfileRequest result = new GetPowerQualityProfileRequest();
    result.setProfileType("PRIVATE");
    result.setBeginDate(this.toGregorianCalendar(BEGIN_DATE));
    result.setEndDate(this.toGregorianCalendar(END_DATE));
    result.setSelectedValues(new CaptureObjectDefinitions());
    return result;
  }

  @Test
  public void shouldConvertGetPowerQualityProfileRequest() {
    final GetPowerQualityProfileRequest source = this.makeRequest();
    final GetPowerQualityProfileRequestData result =
        this.mapper.map(source, GetPowerQualityProfileRequestData.class);
    assertThat(result).as(MAPPED_OBJECT_NULL_MESSAGE).isNotNull();

    assertThat(result.getProfileType())
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getProfileType());

    assertThat(this.toGregorianCalendar(new DateTime(result.getBeginDate())))
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getBeginDate());
    assertThat(this.toGregorianCalendar(new DateTime(result.getEndDate())))
        .as(MAPPED_FIELD_VALUE_MESSAGE)
        .isEqualTo(source.getEndDate());
  }

  private XMLGregorianCalendar toGregorianCalendar(final DateTime dateTime) {
    final GregorianCalendar gcal = new GregorianCalendar();
    gcal.setTime(dateTime.toDate());
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
    } catch (final DatatypeConfigurationException e) {
      throw new RuntimeException("error creating XMLGregorianCalendar");
    }
  }
}
