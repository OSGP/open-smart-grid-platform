// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.core;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeHelper.class);
  private static final String TIME_FORMAT = "HH:mm";
  private static final String CET_TIMEZONE = "Europe/Paris";

  /**
   * @return CET/CEST time zone based on ID {@value #CET_TIMEZONE}
   */
  public static DateTimeZone getCentralEuropeanTimeZone() {
    return DateTimeZone.forID(CET_TIMEZONE);
  }

  /**
   * @return CET/CEST time zone based on ID {@value #CET_TIMEZONE}
   */
  public static ZoneId getCentralEuropeanZoneId() {
    return ZoneId.of(CET_TIMEZONE);
  }

  /**
   * This is a generic method which will translate the given string to a datetime using central
   * European time (CET/CEST). Supported:
   *
   * <p>
   *
   * <ul>
   *   <li>now + 3 months
   *   <li>now + 1 minutes
   *   <li>tomorrow - 1 year
   *   <li>yesterday + 2 weeks
   *   <li>today at midday
   *   <li>today at noon
   *   <li>yesterday at midnight
   *   <li>now at midday + 1 week
   * </ul>
   */
  public static DateTime getDateTime(final String dateString) {
    if (dateString.isEmpty()) {
      return null;
    }

    DateTime retval = DateTime.now(getCentralEuropeanTimeZone());

    final String pattern = "([a-z ]*)[ ]*([+-]?)[ ]*([0-9]*)[ ]*([a-z]*)";
    final Pattern r = Pattern.compile(pattern);
    final Matcher m = r.matcher(dateString);

    if (m.groupCount() > 4) {
      throw new IllegalArgumentException("Invalid dateString [" + dateString + "]");
    }

    m.find();

    final String when = m.group(1).toLowerCase();
    final String op = m.group(2);
    final String offset = m.group(3);
    final String what = m.group(4);

    Integer numberToAddOrSubstract = 0;
    if (!offset.isEmpty()) {
      numberToAddOrSubstract = Integer.parseInt(offset);
    }

    final String whenPattern = "([a-z]*)[ ]*([a-z]*)[ ]*([a-z]*)?";
    final Matcher whenMatcher = Pattern.compile(whenPattern).matcher(when);
    whenMatcher.find();
    switch (whenMatcher.group(1)) {
      case "tomorrow":
        retval = retval.plusDays(1);
        break;
      case "yesterday":
        retval = retval.minusDays(1);
        break;
      case "now":
      case "today":
        break;
      default:
        throw new IllegalArgumentException(
            "Invalid dateString ["
                + dateString
                + "], expected the string to begin with tomorrow, yesterday or now or today");
    }

    // Normalize the seconds and milliseconds to zero
    retval = retval.withSecondOfMinute(0);
    retval = retval.withMillisOfSecond(0);

    if (whenMatcher.groupCount() > 1 && whenMatcher.group(2).equals("at")) {

      switch (whenMatcher.group(3)) {
        case "midday":
        case "noon":
          retval = retval.withHourOfDay(12);
          break;
        case "midnight":
          retval = retval.withHourOfDay(0);
          break;
        default:
          throw new IllegalArgumentException(
              "Invalid dateString ["
                  + dateString
                  + "], expected \"midday\", \"noon\" or \"midnight\"");
      }
      retval = retval.withMinuteOfHour(0);
      retval = retval.withSecondOfMinute(0);
    }

    if (op.equals("+")) {
      switch (what) {
        case "days":
          retval = retval.plusDays(numberToAddOrSubstract);
          break;
        case "minutes":
          retval = retval.plusMinutes(numberToAddOrSubstract);
          break;
        case "hours":
          retval = retval.plusHours(numberToAddOrSubstract);
          break;
        case "weeks":
          retval = retval.plusWeeks(numberToAddOrSubstract);
          break;
        case "months":
          retval = retval.plusMonths(numberToAddOrSubstract);
          break;
        case "years":
          retval = retval.plusYears(numberToAddOrSubstract);
          break;
      }
    } else {
      switch (what) {
        case "days":
          retval = retval.minusDays(numberToAddOrSubstract);
          break;
        case "hours":
          retval = retval.minusHours(numberToAddOrSubstract);
          break;
        case "minutes":
          retval = retval.minusMinutes(numberToAddOrSubstract);
          break;
        case "weeks":
          retval = retval.minusWeeks(numberToAddOrSubstract);
          break;
        case "months":
          retval = retval.minusMonths(numberToAddOrSubstract);
          break;
        case "years":
          retval = retval.minusYears(numberToAddOrSubstract);
          break;
      }
    }

    return retval;
  }

  public static ZonedDateTime getZonedDateTime(final String dateString) {
    return dateTimeToZonedDateTime(getDateTime(dateString));
  }

  public static Instant getInstant(final String dateString) {
    final ZonedDateTime dateTime = getZonedDateTime(dateString);
    return dateTime.toInstant();
  }

  public static DateTime getDateTime2(final String startDate, final DateTime defaultStartDate) {
    if (startDate == null) {
      return defaultStartDate;
    }
    DateTime dateTime;
    try {
      dateTime = getDateTime(startDate);
    } catch (final IllegalArgumentException e) {
      LOGGER.debug(
          "The string {} could not be parsed by DateTimeHelper.getDateTime, lets org.joda.time.DateTime");
      dateTime = DateTime.parse(startDate);
    }
    if (dateTime == null) {
      return defaultStartDate;
    }
    return dateTime;
  }

  public static ZonedDateTime getZonedDateTime2(
      final String startDate, final ZonedDateTime defaultStartDate) {
    return dateTimeToZonedDateTime(
        getDateTime2(startDate, zonedDateTimeToDateTime(defaultStartDate)));
  }

  /** Get time of sunrise/sunset */
  public static DateTime getSunriseSunsetTime(
      final String actionTimeType, final DateTime date, final Location location) {
    final SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "UTC");

    Calendar officialTransition = null;

    final Calendar calender = Calendar.getInstance();

    if (actionTimeType.equalsIgnoreCase("SUNSET")) {
      calender.setTime(date.toDate());
      officialTransition = calculator.getOfficialSunsetCalendarForDate(calender);
    } else if (actionTimeType.equalsIgnoreCase("SUNRISE")) {
      calender.setTime(date.plusDays(1).toDate());
      officialTransition = calculator.getOfficialSunriseCalendarForDate(calender);
    }

    if (officialTransition == null) {
      return null;
    }

    return new DateTime(officialTransition.getTimeInMillis());
  }

  public static ZonedDateTime getSunriseSunsetZonedDateTime(
      final String actionTimeType, final ZonedDateTime date, final Location location) {
    return dateTimeToZonedDateTime(
        getSunriseSunsetTime(actionTimeType, zonedDateTimeToDateTime(date), location));
  }

  /**
   * Shifts a DateTime from the system's timezone to UTC.
   *
   * @param dateTime The DateTime in local system's timezone.
   * @return shifted DateTime in UTC
   */
  public static final DateTime shiftSystemZoneToUtc(final DateTime dateTime) {
    return dateTime
        .plusSeconds(ZonedDateTime.now().getOffset().getTotalSeconds())
        .withZone(DateTimeZone.UTC);
  }

  /**
   * Shifts a time to from the system's timezone to CET. It assumes the time is for the current
   * date.
   *
   * @param time Time in system's timezone, formatted as HH:mm
   * @return Time in CET, formatted as HH:mm
   */
  public static String shiftSystemZoneToCET(final String time) {
    return DateTimeHelper.shiftTimeToOtherZone(time, true);
  }

  /**
   * Shifts a time to from CET to the system's timezone. It assumes the time is for the current
   * date.
   *
   * @param time Time in system's timezone, formatted as HH:mm
   * @return Time in CET, formatted as HH:mm
   */
  public static String shiftCETToSystemZone(final String time) {
    return DateTimeHelper.shiftTimeToOtherZone(time, false);
  }

  /**
   * Shifts a time to another timezone. It assumes the time is for the current date.
   *
   * @param time Time in original timezone, formatted as HH:mm
   * @param positiveShift Indicates if a positive or negative shift should be done
   * @return Shifted time, formatted as HH:mm
   */
  private static String shiftTimeToOtherZone(final String time, final boolean positiveShift) {
    // Extract hours and minutes from the time parameter
    final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);
    final DateTime parsedTime = timeFormatter.parseDateTime(time);

    // Determine current CET offset in hours for the system timezone.
    final int UTCOffsetForCET = DateTimeZone.forID(CET_TIMEZONE).getOffset(new DateTime());
    final int UTCOffsetForSystem = DateTimeZone.getDefault().getOffset(new DateTime());
    final int offsetHours =
        (UTCOffsetForCET - UTCOffsetForSystem) / (3600 * 1000) * (positiveShift ? 1 : -1);

    // Add offset
    final DateTime shiftedTime =
        new DateTime()
            .withTime(parsedTime.getHourOfDay(), parsedTime.getMinuteOfHour(), 0, 0)
            .plusHours(offsetHours);

    return timeFormatter.print(shiftedTime);
  }

  public static ZonedDateTime dateTimeToZonedDateTime(final DateTime dateTime) {
    if (dateTime == null) {
      return null;
    }

    return dateTime.toGregorianCalendar().toZonedDateTime();
  }

  private static DateTime zonedDateTimeToDateTime(final ZonedDateTime zonedDateTime) {
    if (zonedDateTime == null) {
      return null;
    }

    final long millis = zonedDateTime.toInstant().toEpochMilli();
    final DateTimeZone dateTimeZone =
        DateTimeZone.forTimeZone(TimeZone.getTimeZone(zonedDateTime.getZone()));
    return new DateTime(millis, dateTimeZone);
  }
}
