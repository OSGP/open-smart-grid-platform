// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetPeriodicMeterReadsRequestBuilder {

  private static final PeriodType DEFAULT_PERIOD_TYPE = PeriodType.DAILY;
  private static final ZonedDateTime DEFAULT_BEGIN_DATE =
      ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
  private static final ZonedDateTime DEFAULT_END_DATE =
      ZonedDateTime.of(2017, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

  private PeriodType periodType;
  private XMLGregorianCalendar beginDate;
  private XMLGregorianCalendar endDate;

  public GetPeriodicMeterReadsRequestBuilder withDefaults() {
    return this.fromParameterMap(Collections.emptyMap());
  }

  public GetPeriodicMeterReadsRequestBuilder fromParameterMap(
      final Map<String, String> parameters) {
    this.periodType = this.getPeriodType(parameters);
    this.beginDate = this.getBeginDate(parameters);
    this.endDate = this.getEndDate(parameters);
    return this;
  }

  public GetPeriodicMeterReadsRequest build() {
    final GetPeriodicMeterReadsRequest request = new GetPeriodicMeterReadsRequest();
    request.setPeriodType(this.periodType);
    request.setBeginDate(this.beginDate);
    request.setEndDate(this.endDate);
    return request;
  }

  private PeriodType getPeriodType(final Map<String, String> parameters) {
    return getEnum(
        parameters,
        PlatformSmartmeteringKeys.KEY_PERIOD_TYPE,
        PeriodType.class,
        DEFAULT_PERIOD_TYPE);
  }

  private XMLGregorianCalendar getBeginDate(final Map<String, String> parameters) {
    final ZonedDateTime dateTime =
        getDate(parameters, PlatformSmartmeteringKeys.KEY_BEGIN_DATE, DEFAULT_BEGIN_DATE);
    return DateConverter.createXMLGregorianCalendar(Date.from(dateTime.toInstant()));
  }

  private XMLGregorianCalendar getEndDate(final Map<String, String> parameters) {
    final ZonedDateTime dateTime =
        getDate(parameters, PlatformSmartmeteringKeys.KEY_END_DATE, DEFAULT_END_DATE);
    return DateConverter.createXMLGregorianCalendar(Date.from(dateTime.toInstant()));
  }
}
