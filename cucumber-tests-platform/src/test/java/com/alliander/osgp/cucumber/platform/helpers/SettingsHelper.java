/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.helpers;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.alliander.osgp.cucumber.platform.inputparsers.XmlGregorianCalendarInputParser;

import cucumber.api.DataTable;

/**
 * Helper class to deal with settings from Cucumber scenarios.
 * <p>
 * Provides easy options to extend settings in the form of key-value pairs
 * obtained from two column Cucumber {@link DataTable data tables} that are
 * transformed into a map.
 * <p>
 * Has helper methods to get specific types of values for potentially indexed
 * keys from a String based map.
 */
public class SettingsHelper {

    /**
     * Mixes in default mappings to an existing Map of key-value pairs.
     *
     * @param original
     *            settings to be extended with provided {@code defaults}.
     * @param defaults
     *            key-value pairs to be mixed in if the key is not already
     *            present in {@code original}.
     * @return a new Map of key-value pairs containing all mappings from
     *         {@code original} with added mappings from the {@code defaults}
     *         for which the keys were not present in {@code original}.
     */
    public static <K, V> Map<K, V> addAsDefaults(final Map<K, V> original, final Map<K, V> defaults) {

        if (defaults == null || defaults.isEmpty()) {
            return original;
        }

        final Map<K, V> result = new HashMap<>();
        if (original != null) {
            result.putAll(original);
        }

        for (final Map.Entry<K, V> defaultEntry : defaults.entrySet()) {
            if (!result.containsKey(defaultEntry.getKey())) {
                result.put(defaultEntry.getKey(), defaultEntry.getValue());
            }
        }

        return result;
    }

    /**
     * Adds a mapping of {@code key} to {@code value} to the mappings from
     * {@code original} if the given {@code key} is not already present.
     *
     * @param original
     *            settings to be extended with provided {@code key} to
     *            {@code value} mapping.
     * @param key
     *            key for a new mapping if not already present in
     *            {@code original}.
     * @param value
     *            value for the given {@code key}.
     * @return a new Map of key-value pairs containing all mappings from
     *         {@code original} with an added mapping from the {@code key} to
     *         {@code value} if the {@code key} was already not present in
     *         {@code original}.
     */
    public static <K, V> Map<K, V> addDefault(final Map<K, V> original, final K key, final V value) {

        final Map<K, V> result = new HashMap<>();
        if (original != null) {
            result.putAll(original);
        }

        if (!result.containsKey(key)) {
            result.put(key, value);
        }

        return result;
    }

    public static Double getDoubleValue(final Map<String, String> settings, final String keyPrefix,
            final int... indexes) {
        final String stringValue = getStringValue(settings, keyPrefix, indexes);
        if (stringValue == null) {
            return null;
        }
        return Double.valueOf(stringValue);
    }

    public static Byte getByteValue(final Map<String, String> settings, final String keyPrefix, final int... indexes) {
        final String stringValue = getStringValue(settings, keyPrefix, indexes);
        if (stringValue == null) {
            return null;
        }
        return Byte.valueOf(stringValue);
    }

    public static Integer getIntegerValue(final Map<String, String> settings, final String keyPrefix,
            final int... indexes) {
        final String stringValue = getStringValue(settings, keyPrefix, indexes);
        if (stringValue == null) {
            return null;
        }
        return Integer.valueOf(stringValue);
    }

    public static Long getLongValue(final Map<String, String> settings, final String keyPrefix, final int... indexes) {
        final String stringValue = getStringValue(settings, keyPrefix, indexes);
        if (stringValue == null) {
            return null;
        }
        return Long.valueOf(stringValue);
    }

    public static BigInteger getBigIntegerValue(final Map<String, String> settings, final String keyPrefix,
            final int... indexes) {
        final String stringValue = getStringValue(settings, keyPrefix, indexes);
        if (stringValue == null) {
            return null;
        }
        return new BigInteger(stringValue);
    }

    public static String getStringValue(final Map<String, String> settings, final String keyPrefix,
            final int... indexes) {
        final String key = makeKey(keyPrefix, indexes);
        return settings.get(key);
    }

    public static XMLGregorianCalendar getXmlGregorianCalendarValue(final Map<String, String> settings,
            final String keyPrefix, final int... indexes) {
        final String stringValue = getStringValue(settings, keyPrefix, indexes);
        if (stringValue == null) {
            return null;
        }
        return XmlGregorianCalendarInputParser.parse(stringValue);
    }

    public static boolean hasKey(final Map<String, String> settings, final String keyPrefix, final int... indexes) {
        return settings.containsKey(makeKey(keyPrefix, indexes));
    }

    public static String makeKey(final String keyPrefix, final int... indexes) {
        if (indexes == null || indexes.length == 0) {
            return keyPrefix;
        }
        final StringBuilder sb = new StringBuilder(keyPrefix);
        for (final int index : indexes) {
            sb.append('_').append(index);
        }
        return sb.toString();
    }

    private SettingsHelper() {
        // Utility class, keep constructor private.
    }
}
