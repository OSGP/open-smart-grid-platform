// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;

import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.time.DateTime;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;

public class XMLGregorianCalendarHelper {

  public static XMLGregorianCalendar createXMLGregorianCalendar(
      final Map<String, String> settings, final String key) {

    final DateTime dateTime = getDate(settings, key, new DateTime());
    return DateConverter.createXMLGregorianCalendar(dateTime.toDate());
  }
}
