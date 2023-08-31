package org.opensmartgridplatform.shared.utils;

import java.time.Instant;
import java.time.Instant;
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

  public static ZonedDateTime shiftZoneToUTC(final ZonedDateTime dateTime) {
    final int offset = dateTime.getZone().getRules().getOffset(Instant.now()).getTotalSeconds();
    return dateTime.plusSeconds(offset).withZoneSameInstant(ZoneId.of("UTC"));
  }

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
