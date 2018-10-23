/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class ReadSettingsHelper {
    /**
     * This shortcut makes a new Map from the given map with the given key value
     * pair
     *
     * @param settings
     *            the input map
     * @return an updated Map with the new key value pair
     */
    public static Map<String, String> addSetting(final Map<String, String> settings, final String key,
            final String value) {
        final Map<String, String> result = new HashMap<>();
        result.putAll(settings);
        result.put(key, value);
        return result;
    }

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
     * Get a float object based on the settings if the key exists.
     *
     * @param settings
     *            The settings
     * @param key
     *            The key in the settings for the float object.
     * @return The float object.
     */
    public static Float getFloat(final Map<String, String> settings, final String key) {
        if (StringUtils.isEmpty(settings.get(key))) {
            return null;
        }

        return Float.parseFloat(settings.get(key));
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

    public static Integer getInteger(final Map<String, String> settings, final String key) {
        if (StringUtils.isEmpty(settings.get(key))) {
            return null;
        }
        return Integer.parseInt(settings.get(key));
    }

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
            return null;
        }

        return Long.parseLong(settings.get(key));
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

        return Long.parseLong(settings.get(key));
    }

    public static Short getShort(final Map<String, String> settings, final String key) {
        Short value = null;
        if (settings.containsKey(key) && !settings.get(key).equalsIgnoreCase("null")) {
            value = Short.parseShort(settings.get(key));
        }
        return value;
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

        return Short.parseShort(settings.get(key));
    }

    public static String getString(final Map<String, String> settings, final String key) {
        String value = null;
        if (settings.containsKey(key) && !settings.get(key).equalsIgnoreCase("null")) {
            value = settings.get(key);
        }

        return value;
    }

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

        return Byte.parseByte(settings.get(key));
    }

    public static String getNullOrNonEmptyString(final Map<String, String> settings, final String key,
            final String defaultValue) {
        final String value = getString(settings, key, defaultValue);
        return ("".equals(value) ? null : value);
    }

    public static <E extends Enum<E>> E getEnum(final Map<String, String> settings, final String key,
            final Class<E> enumType) {
        if (!settings.containsKey(key) || settings.get(key).isEmpty()) {
            return null;
        }

        return Enum.valueOf(enumType, settings.get(key));
    }

    public static <E extends Enum<E>> E getEnum(final Map<String, String> settings, final String key,
            final Class<E> enumType, final E defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return getEnum(settings, key, enumType);
    }

}
