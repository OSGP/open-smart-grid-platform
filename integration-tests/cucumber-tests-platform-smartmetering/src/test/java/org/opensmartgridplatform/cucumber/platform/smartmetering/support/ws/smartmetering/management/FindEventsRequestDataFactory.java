// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.GregorianCalendar;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventLogCategory;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class FindEventsRequestDataFactory {

  public static FindEventsRequestData fromParameterMap(
      final Map<String, String> requestParameters) {

    final EventLogCategory eventLogCategory =
        EventLogCategory.fromValue(
            getString(requestParameters, PlatformSmartmeteringKeys.EVENT_TYPE));
    final XMLGregorianCalendar beginDate =
        createXMLGregorianCalendar(requestParameters, PlatformSmartmeteringKeys.KEY_BEGIN_DATE);
    final XMLGregorianCalendar endDate =
        createXMLGregorianCalendar(requestParameters, PlatformSmartmeteringKeys.KEY_END_DATE);

    final FindEventsRequestData findEventsRequestData = new FindEventsRequestData();

    findEventsRequestData.setEventLogCategory(eventLogCategory);
    findEventsRequestData.setFrom(beginDate);
    findEventsRequestData.setUntil(endDate);

    return findEventsRequestData;
  }

  private static final XMLGregorianCalendar createXMLGregorianCalendar(
      final Map<String, String> settings, final String key) {

    final DateTime dateTime = getDate(settings, key, new DateTime());

    try {
      final GregorianCalendar gregorianCalendar = new GregorianCalendar();
      gregorianCalendar.setTime(dateTime.toDate());
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    } catch (final DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
}
