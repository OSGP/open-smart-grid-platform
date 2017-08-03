/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.core;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

public class Helpers {

    private static final Logger LOGGER = LoggerFactory.getLogger(Helpers.class);
    private static final String TIME_FORMAT = "HH:mm";
    private static final String CET_TIMEZONE = "Europe/Paris";

    protected static final String XPATH_MATCHER_CORRELATIONUID = "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}";

    /**
     * This shortcut makes a new Map from the given map with the given key value
     * pair
     *
     * @param settings
     *            the input map
     * @param key
     * @param value
     * @return an updated Map with the new key value pair
     */
    public static Map<String, String> addSetting(final Map<String, String> settings, final String key,
            final String value) {
        final Map<String, String> result = new HashMap<>();
        result.putAll(settings);
        result.put(key, value);
        return result;
    }

    /**
     * @param settings
     * @param key
     * @return
     */
    public static Boolean getBoolean(final Map<String, String> settings, final String key) {
        return Boolean.parseBoolean(settings.get(key));
    }

    /**
     * Get the boolean value of the given key in the settings. If it didn't
     * exist return the defaultValue.
     *
     * @param settings
     *            The settings to get the value from.
     * @param key
     *            The key to get the boolean from.
     * @param defaultValue
     *            The default value if the key wasn't found.
     * @return
     */
    public static Boolean getBoolean(final Map<String, String> settings, final String key, final Boolean defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return getBoolean(settings, key);
    }

    /**
     * Get a date time object based on the settings if the key exists.
     *
     * @param settings
     *            The settings
     * @param key
     *            The key in the settings for the date time.
     * @return The date time.
     */
    public static DateTime getDate(final Map<String, String> settings, final String key) {
        return getDate(settings, key, DateTime.now());
    }

    /**
     * Get a date time object based on the settings if the key exists.
     *
     * @param settings
     *            The settings
     * @param key
     *            The key in the settings for the date time.
     * @param defaultDate
     *            The default date to return.
     * @return The date time.
     */
    public static DateTime getDate(final Map<String, String> settings, final String key, final DateTime defaultDate) {
        if (!settings.containsKey(key)) {
            return defaultDate;
        }

        return DateTime.parse(settings.get(key));
    }

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
     * @param dateString
     * @return
     * @throws Exception
     */
    public static DateTime getDateTime(final String dateString) throws Exception {

        DateTime retval;

        if (dateString.isEmpty()) {
            return null;
        }

        final String pattern = "([a-z ]*)[ ]*([+-]?)[ ]*([0-9]*)[ ]*([a-z]*)";
        final Pattern r = Pattern.compile(pattern);
        final Matcher m = r.matcher(dateString);

        if (m.groupCount() > 4) {
            throw new Exception("Incorrect dateString [" + dateString + "]");
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
            throw new Exception("Incorrect dateString [" + dateString
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
                throw new Exception("Incorrect dateString [" + dateString + "], expected \"midday\" or \"midnight\"");
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

    /**
     *
     * @param startDate
     * @param defaultStartDate
     * @return
     * @throws Exception
     */
    public static DateTime getDateTime2(final String startDate, final DateTime defaultStartDate) throws Exception {
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
     *
     * @param actionTimeType
     * @param date
     * @param location
     * @return DateTime
     * @throws Exception
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
        return Helpers.shiftTimeToOtherZone(time, true);
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
        return Helpers.shiftTimeToOtherZone(time, false);
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

    public static Location getCurrentLocationByLatitudeAndLongitude(final double latitude, final double longitude) {
        return new Location(latitude, longitude);
    }

    public static <E extends Enum<E>> E getEnum(final Map<String, String> settings, final String key,
            final Class<E> enumType) {
        if (!settings.containsKey(key) || settings.get(key).isEmpty()) {
            return null;
        }

        return Enum.valueOf(enumType, settings.get(key));
    }

    /**
     *
     * @param settings
     * @param key
     * @param enumType
     * @param defaultValue
     * @return
     */
    public static <E extends Enum<E>> E getEnum(final Map<String, String> settings, final String key,
            final Class<E> enumType, final E defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return getEnum(settings, key, enumType);
    }

    /**
     * Get a float object based on the settings if the key exists.
     *
     * @param settings
     *            The settings
     * @param key
     *            The key in the settings for the float object.
     * @param defaultValue
     *            The default value to return if the key wasn't found.
     * @return The float object.
     */
    public static Float getFloat(final Map<String, String> settings, final String key, final Float defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return Float.parseFloat(settings.get(key));
    }

    /**
     *
     * @param settings
     * @param key
     * @param defaultValue
     * @return
     */
    public static Integer getInteger(final Map<String, String> settings, final String key) {
        if (settings.get(key) == null) {
            return null;
        }
        return Integer.parseInt(settings.get(key));
    }

    /**
     *
     * @param settings
     * @param key
     * @param defaultValue
     * @return
     */
    public static Integer getInteger(final Map<String, String> settings, final String key, final Integer defaultValue) {

        if (!settings.containsKey(key) || settings.get(key).isEmpty()) {
            return defaultValue;
        }

        return getInteger(settings, key);
    }

    /**
     * Get a long value for the key from the settings.
     *
     * @param settings
     *            The settings to get the key from.
     * @param key
     *            The key
     * @return The long
     */
    public static Long getLong(final Map<String, String> settings, final String key) {

        if (!settings.containsKey(key)) {
            return new java.util.Random().nextLong();
        }

        final Long value = Long.parseLong(settings.get(key));
        return value;
    }

    /**
     * Get a long value for the key from the settings.
     *
     * @param settings
     *            The settings to get the key from.
     * @param key
     *            The key
     * @param defaultValue
     *            The default value if the key wasn't found.
     * @return The long
     */
    public static Long getLong(final Map<String, String> settings, final String key, final Long defaultValue) {

        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        final Long value = Long.parseLong(settings.get(key));
        return value;
    }

    public static Short getShort(final Map<String, String> settings, final String key) {
        return Short.parseShort(settings.get(key));
    }

    /**
     * Get a Short value for the key from the settings.
     *
     * @param settings
     *            The settings to get the key from.
     * @param key
     *            The key
     * @param defaultValue
     *            The default value if the key wasn't found.
     * @return The long
     */
    public static Short getShort(final Map<String, String> settings, final String key, final Short defaultValue) {

        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        final Short value = Short.parseShort(settings.get(key));
        return value;
    }

    public static String getString(final Map<String, String> settings, final String key) {
        String value = null;
        if (settings.containsKey(key)) {
            if (!settings.get(key).equalsIgnoreCase("null")) {
                value = settings.get(key);
            }
        }
        return value;
    }

    /**
     *
     * @param settings
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(final Map<String, String> settings, final String key, final String defaultValue) {

        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return getString(settings, key);
    }

    public static byte[] getHexDecoded(final Map<String, String> settings, final String key,
            final String defaultValue) {
        try {
            if (!settings.containsKey(key)) {
                return Hex.decodeHex(defaultValue.toCharArray());
            } else {
                return Hex.decodeHex(settings.get(key).toCharArray());
            }
        } catch (final DecoderException e) {
            throw new IllegalArgumentException("Could not hex decode value for key " + key, e);
        }

    }

    public static Byte getByte(final Map<String, String> settings, final String key, final Byte defaultValue) {

        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        final Byte value = Byte.parseByte(settings.get(key));
        return value;
    }

    public static String getNullOrNonEmptyString(final Map<String, String> settings, final String key,
            final String defaultValue) {
        final String value = getString(settings, key, defaultValue);
        return ("".equals(value) ? null : value);
    }
}
