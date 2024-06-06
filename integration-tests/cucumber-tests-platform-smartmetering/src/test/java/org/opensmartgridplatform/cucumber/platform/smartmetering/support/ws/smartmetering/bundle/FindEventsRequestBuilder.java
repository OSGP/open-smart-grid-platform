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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventLogCategory;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class FindEventsRequestBuilder {

  private static final EventLogCategory DEFAULT_EVENT_LOG_CATEGORY =
      EventLogCategory.FRAUD_DETECTION_LOG;
  private static final ZonedDateTime DEFAULT_FROM =
      ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
  private static final ZonedDateTime DEFAULT_UNTIL =
      ZonedDateTime.of(2017, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

  private EventLogCategory eventLogCategory;
  private XMLGregorianCalendar from;
  private XMLGregorianCalendar until;

  public FindEventsRequestBuilder withDefaults() {
    return this.fromParameterMap(Collections.emptyMap());
  }

  public FindEventsRequestBuilder fromParameterMap(final Map<String, String> parameters) {
    this.eventLogCategory = this.getEventLogCategory(parameters);
    this.from = this.getFrom(parameters);
    this.until = this.getUntil(parameters);
    return this;
  }

  public FindEventsRequest build() {
    final FindEventsRequest request = new FindEventsRequest();
    request.setEventLogCategory(this.eventLogCategory);
    request.setFrom(this.from);
    request.setUntil(this.until);
    return request;
  }

  private EventLogCategory getEventLogCategory(final Map<String, String> parameters) {
    return getEnum(
        parameters,
        PlatformSmartmeteringKeys.EVENT_LOG_CATEGORY,
        EventLogCategory.class,
        DEFAULT_EVENT_LOG_CATEGORY);
  }

  private XMLGregorianCalendar getFrom(final Map<String, String> parameters) {
    final ZonedDateTime dateTime =
        getDate(parameters, PlatformSmartmeteringKeys.FROM_DATE, DEFAULT_FROM);
    return DateConverter.createXMLGregorianCalendar(Date.from(dateTime.toInstant()));
  }

  private XMLGregorianCalendar getUntil(final Map<String, String> parameters) {
    final ZonedDateTime dateTime =
        getDate(parameters, PlatformSmartmeteringKeys.UNTIL_DATE, DEFAULT_UNTIL);
    return DateConverter.createXMLGregorianCalendar(Date.from(dateTime.toInstant()));
  }
}
