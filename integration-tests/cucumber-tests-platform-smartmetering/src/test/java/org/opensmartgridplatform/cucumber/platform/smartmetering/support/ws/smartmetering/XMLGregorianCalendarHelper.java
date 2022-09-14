/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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
