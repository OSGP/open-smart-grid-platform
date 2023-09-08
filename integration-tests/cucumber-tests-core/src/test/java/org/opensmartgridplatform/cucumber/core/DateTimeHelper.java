// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.core;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.opensmartgridplatform.shared.utils.JavaTimeHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeHelper.class);
  private static final String TIME_FORMAT = "HH:mm";
  private static final String CET_TIMEZONE = "Europe/Paris";

  /**
   * @return CET/CEST time zone based on ID {@value #CET_TIMEZONE}
   */
  public static ZoneId getCentralEuropeanTimeZone() {
    return ZoneId.of(CET_TIMEZONE);
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
  public static ZonedDateTime getDateTime(final String dateString) {
    if (dateString.isEmpty()) {
      return null;
    }

    ZonedDateTime retval = ZonedDateTime.now(getCentralEuropeanTimeZone());

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
    retval = retval.withSecond(0);
    retval = retval.withNano(0);

    if (whenMatcher.groupCount() > 1 && whenMatcher.group(2).equals("at")) {

      switch (whenMatcher.group(3)) {
        case "midday":
        case "noon":
          retval = retval.withHour(12);
          break;
        case "midnight":
          retval = retval.withHour(0);
          break;
        default:
          throw new IllegalArgumentException(
              "Invalid dateString ["
                  + dateString
                  + "], expected \"midday\", \"noon\" or \"midnight\"");
      }
      retval = retval.withMinute(0);
      retval = retval.withSecond(0);
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
    return getDateTime(dateString);
  }

  public static Instant getInstant(final String dateString) {
    final ZonedDateTime dateTime = getZonedDateTime(dateString);
    return dateTime.toInstant();
  }

  public static ZonedDateTime getDateTime2(
      final String startDate, final ZonedDateTime defaultStartDate) {
    if (startDate == null) {
      return defaultStartDate;
    }
    ZonedDateTime dateTime;
    try {
      dateTime = getDateTime(startDate);
    } catch (final IllegalArgumentException e) {
      LOGGER.debug("The string {} could not be parsed by DateTimeHelper.getDateTime");
      dateTime = JavaTimeHelpers.parseToZonedDateTime(startDate);
    }
    if (dateTime == null) {
      return defaultStartDate;
    }
    return dateTime;
  }

  public static ZonedDateTime getZonedDateTime2(
      final String startDate, final ZonedDateTime defaultStartDate) {
    return getDateTime2(startDate, defaultStartDate);
  }

  /** Get time of sunrise/sunset */
  public static ZonedDateTime getSunriseSunsetTime(
      final String actionTimeType, final ZonedDateTime date, final Location location) {
    final SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "UTC");

    Calendar officialTransition = null;

    final Calendar calender = Calendar.getInstance();

    if (actionTimeType.equalsIgnoreCase("SUNSET")) {
      calender.setTime(Date.from(date.toInstant()));
      officialTransition = calculator.getOfficialSunsetCalendarForDate(calender);
    } else if (actionTimeType.equalsIgnoreCase("SUNRISE")) {
      calender.setTime(Date.from(date.plusDays(1).toInstant()));
      officialTransition = calculator.getOfficialSunriseCalendarForDate(calender);
    }

    if (officialTransition == null) {
      return null;
    }

    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(officialTransition.getTimeInMillis()), ZoneId.systemDefault());
  }

  public static ZonedDateTime getSunriseSunsetZonedDateTime(
      final String actionTimeType, final ZonedDateTime date, final Location location) {
    return getSunriseSunsetTime(actionTimeType, date, location);
  }

  /**
   * Shifts a DateTime from the system's timezone to UTC.
   *
   * @param dateTime The DateTime in local system's timezone.
   * @return shifted DateTime in UTC
   */
  public static final ZonedDateTime shiftSystemZoneToUtc(final ZonedDateTime dateTime) {
    return JavaTimeHelpers.shiftZoneToUTC(dateTime);
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
    final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
    final LocalTime parsedTime = LocalTime.parse(time, timeFormatter);

    // Determine current CET offset in hours for the system timezone.
    final int UTCOffsetForCET =
        ZoneId.of(CET_TIMEZONE).getRules().getOffset(Instant.now()).getTotalSeconds();
    final int UTCOffsetForSystem =
        ZoneId.systemDefault().getRules().getOffset(Instant.now()).getTotalSeconds();
    final int offsetHours =
        (UTCOffsetForCET - UTCOffsetForSystem) / (3600) * (positiveShift ? 1 : -1);

    // Add offset
    final ZonedDateTime shiftedTime =
        ZonedDateTime.now()
            .withHour(parsedTime.getHour())
            .withMinute(parsedTime.getMinute())
            .withSecond(0)
            .withNano(0)
            .plusHours(offsetHours);

    return timeFormatter.format(shiftedTime);
  }
}
