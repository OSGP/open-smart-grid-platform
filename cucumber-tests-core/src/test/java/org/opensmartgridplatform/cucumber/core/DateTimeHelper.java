/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.core;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

public class DateTimeHelper {

    private static final String TIME_FORMAT = "HH:mm";
    private static final String CET_TIMEZONE = "Europe/Paris";

    /**
     * This is a generic method which will translate the given string to a
     * datetime. Supported:
     * <p>
     * <ul>
     * <li>now + 3 months
     * <li>now + 1 minutes
     * <li>tomorrow - 1 year
     * <li>yesterday + 2 weeks
     * <li>today at midday
     * <li>yesterday at midnight
     * <li>now at midday + 1 week
     * </ul>
     *
     * @throws Exception
     */
    public static DateTime getDateTime(final String dateString) {

        DateTime retval;

        if (dateString.isEmpty()) {
            return null;
        }

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
            retval = DateTime.now().plusDays(1);
            break;
        case "yesterday":
            retval = DateTime.now().minusDays(1);
            break;
        case "now":
        case "today":
            retval = DateTime.now();
            break;
        default:
            throw new IllegalArgumentException("Invalid dateString [" + dateString
                    + "], expected the string to begin with tomorrow, yesterday or now or today");
        }

        // Normalize the seconds and milliseconds to zero
        retval = retval.withSecondOfMinute(0);
        retval = retval.withMillisOfSecond(0);

        if (whenMatcher.groupCount() > 1 && whenMatcher.group(2).equals("at")) {

            switch (whenMatcher.group(3)) {
            case "midday":
                retval = retval.withHourOfDay(12);
                break;
            case "midnight":
                retval = retval.withHourOfDay(0);
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid dateString [" + dateString + "], expected \"midday\" or \"midnight\"");
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

    public static DateTime getDateTime2(final String startDate, final DateTime defaultStartDate) {
        if (startDate == null) {
            return defaultStartDate;
        }
        final DateTime dateTime = getDateTime(startDate);
        if (dateTime == null) {
            return defaultStartDate;
        }
        return dateTime;
    }

    /**
     * Get time of sunrise/sunset
     */
    public static DateTime getSunriseSunsetTime(final String actionTimeType, final DateTime date,
            final Location location) {
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

    /**
     * Shifts a time to from the system's timezone to CET. It assumes the time
     * is for the current date.
     *
     * @param time
     *            Time in system's timezone, formatted as HH:mm
     * @return Time in CET, formatted as HH:mm
     */
    public static String shiftSystemZoneToCET(final String time) {
        return DateTimeHelper.shiftTimeToOtherZone(time, true);
    }

    /**
     * Shifts a time to from CET to the system's timezone. It assumes the time
     * is for the current date.
     *
     * @param time
     *            Time in system's timezone, formatted as HH:mm
     * @return Time in CET, formatted as HH:mm
     */
    public static String shiftCETToSystemZone(final String time) {
        return DateTimeHelper.shiftTimeToOtherZone(time, false);
    }

    /**
     * Shifts a time to another timezone. It assumes the time is for the current
     * date.
     *
     * @param time
     *            Time in original timezone, formatted as HH:mm
     * @param positiveShift
     *            Indicates if a positive or negative shift should be done
     * @return Shifted time, formatted as HH:mm
     */
    private static String shiftTimeToOtherZone(final String time, final boolean positiveShift) {
        // Extract hours and minutes from the time parameter
        final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);
        final DateTime parsedTime = timeFormatter.parseDateTime(time);

        // Determine current CET offset in hours for the system timezone.
        final int UTCOffsetForCET = DateTimeZone.forID(CET_TIMEZONE).getOffset(new DateTime());
        final int UTCOffsetForSystem = DateTimeZone.getDefault().getOffset(new DateTime());
        final int offsetHours = (UTCOffsetForCET - UTCOffsetForSystem) / (3600 * 1000) * (positiveShift ? 1 : -1);

        // Add offset
        final DateTime shiftedTime = new DateTime()
                .withTime(parsedTime.getHourOfDay(), parsedTime.getMinuteOfHour(), 0, 0).plusHours(offsetHours);

        return timeFormatter.print(shiftedTime);
    }

}
