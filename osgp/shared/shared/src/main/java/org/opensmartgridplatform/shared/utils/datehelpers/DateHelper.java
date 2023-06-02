//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.utils.datehelpers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

  private DateHelper() {
    // avoid creation of class
    // use only static methods.
  }

  public static Date getGmtDate() {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
    return cal.getTime();
  }

  /**
   * Creates a java.util.Date value from a String. The String needs to be formatted in ISO 8601
   * format.
   *
   * @param dateText The String to parse.
   * @return the parsed Date.
   */
  public static Date dateFromIsoString(final String dateText) {

    final DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
    final ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateText, dtf);

    return Date.from(zonedDateTime.toInstant());
  }
}
