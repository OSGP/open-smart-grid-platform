// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class PeriodicReadsRequestDataFactory {

  public static PeriodicReadsRequestData fromParameterMap(
      final Map<String, String> requestParameters) {

    final PeriodType periodType =
        PeriodType.fromValue(
            getString(requestParameters, PlatformSmartmeteringKeys.KEY_PERIOD_TYPE, "DAILY"));
    final XMLGregorianCalendar beginDate =
        createXMLGregorianCalendar(requestParameters, PlatformKeys.KEY_BEGIN_DATE);
    final XMLGregorianCalendar endDate =
        createXMLGregorianCalendar(requestParameters, PlatformKeys.KEY_END_DATE);

    final PeriodicReadsRequestData periodicReadsRequestData = new PeriodicReadsRequestData();
    periodicReadsRequestData.setBeginDate(beginDate);
    periodicReadsRequestData.setEndDate(endDate);
    periodicReadsRequestData.setPeriodType(periodType);

    return periodicReadsRequestData;
  }

  private static final XMLGregorianCalendar createXMLGregorianCalendar(
      final Map<String, String> settings, final String key) {

    final ZonedDateTime dateTime = getDate(settings, key, ZonedDateTime.now());
    return DateConverter.createXMLGregorianCalendar(Date.from(dateTime.toInstant()));
  }
}
