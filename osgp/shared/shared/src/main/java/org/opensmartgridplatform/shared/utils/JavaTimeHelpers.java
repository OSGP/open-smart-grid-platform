package org.opensmartgridplatform.shared.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.GregorianCalendar;

public class JavaTimeHelpers {

  public static long getMillisFrom(final Temporal datetime) {
    return datetime.get(ChronoField.MILLI_OF_SECOND);
  }

  public static String formatDate(final Date date, final DateTimeFormatter formatter) {
    return formatter.format(date.toInstant().atZone(ZoneId.systemDefault()));
  }

  public static String formatDate(final Temporal date, final DateTimeFormatter formatter) {
    return formatter.format(date);
  }

  public static ZonedDateTime gregorianCalendarToZonedDateTime(
      final GregorianCalendar gregorianCalendar, final ZoneId zoneId) {
    return ZonedDateTime.ofInstant(gregorianCalendar.toInstant(), zoneId);
  }

  public static int getOffsetForZonedDateTimeInMillis(final ZonedDateTime dateTime) {
    return dateTime.getOffset().getTotalSeconds() * 1000;
  }

  public static boolean isDayLightSavingsActive(final ZonedDateTime dateTime) {
    return dateTime.getZone().getRules().isDaylightSavings(dateTime.toInstant());
  }

  /**
   * Parses different kind of date strings to a ZonedDateTime. This method should not be used in
   * production code. This code is created because the cucumber tests provides date strings in the
   * following formats: - yyyy-mm-dd - yyyy-mm-dd:hh:mm:ss - yyyy-mm-dd:hh:mm:ss Z
   *
   * @param date the date string to parse
   * @return ZonedDateTime parsed from the provided string
   */
  @Deprecated
  public static ZonedDateTime parseToZonedDateTime(final String date) {
    ZonedDateTime zonedDateTime;
    try {
      zonedDateTime = ZonedDateTime.parse(date);
    } catch (final DateTimeParseException firstAttempt) {
      try {
        zonedDateTime = LocalDateTime.parse(date).atZone(ZoneId.systemDefault());
      } catch (final DateTimeParseException secondAttempt) {
        zonedDateTime = LocalDate.parse(date).atStartOfDay().atZone(ZoneId.systemDefault());
      }
    }
    return zonedDateTime;
  }
}
