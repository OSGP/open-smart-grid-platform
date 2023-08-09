package org.opensmartgridplatform.shared.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.xml.datatype.XMLGregorianCalendar;

public class ZonedDateTimeUtils {
  private ZonedDateTimeUtils() {}

  static ZonedDateTime XmlGregorianCalanderToZonedDateTime(
      final XMLGregorianCalendar xmlGregorianCalendar) {
    return ZonedDateTime.ofInstant(
        xmlGregorianCalendar.toGregorianCalendar().toInstant(), ZoneId.systemDefault());
  }

  static ZonedDateTime XmlGregorianCalanderToZonedDateTime(
      final XMLGregorianCalendar xmlGregorianCalendar, final ZoneId zoneId) {
    return ZonedDateTime.ofInstant(xmlGregorianCalendar.toGregorianCalendar().toInstant(), zoneId);
  }
}
