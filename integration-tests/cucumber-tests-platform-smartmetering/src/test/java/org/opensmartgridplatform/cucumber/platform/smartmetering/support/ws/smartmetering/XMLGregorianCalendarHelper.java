// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;

public class XMLGregorianCalendarHelper {

  public static XMLGregorianCalendar createXMLGregorianCalendar(
      final Map<String, String> settings, final String key) {

    final ZonedDateTime dateTime = getDate(settings, key, ZonedDateTime.now());
    return DateConverter.createXMLGregorianCalendar(Date.from(dateTime.toInstant()));
  }
}
