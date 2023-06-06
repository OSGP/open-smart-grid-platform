// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.core;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class ReadSettingsHelper {
  /**
   * This shortcut makes a new Map from the given map with the given key value pair
   *
   * @param settings the input map
   * @return an updated Map with the new key value pair
   */
  public static Map<String, String> addSetting(
      final Map<String, String> settings, final String key, final String value) {
    final Map<String, String> result = new HashMap<>();
    result.putAll(settings);
    result.put(key, value);
    return result;
  }

  public static Boolean getBoolean(final Map<String, String> settings, final String key) {
    return Boolean.parseBoolean(settings.get(key));
  }

  /**
   * Get the boolean value of the given key in the settings. If it didn't exist return the
   * defaultValue.
   *
   * @param settings The settings to get the value from.
   * @param key The key to get the boolean from.
   * @param defaultValue The default value if the key wasn't found.
   */
  public static Boolean getBoolean(
      final Map<String, String> settings, final String key, final Boolean defaultValue) {
    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return defaultValue;
    }

    return getBoolean(settings, key);
  }

  /**
   * Get a date time object based on the settings if the key exists.
   *
   * @param settings The settings
   * @param key The key in the settings for the date time.
   * @return The date time.
   */
  public static DateTime getDate(final Map<String, String> settings, final String key) {
    return getDate(settings, key, DateTime.now());
  }

  /**
   * Get a date time object based on the settings if the key exists.
   *
   * @param settings The settings
   * @param key The key in the settings for the date time.
   * @param defaultDate The default date to return.
   * @return The date time.
   */
  public static DateTime getDate(
      final Map<String, String> settings, final String key, final DateTime defaultDate) {
    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return defaultDate;
    }

    return DateTime.parse(settings.get(key));
  }

  /**
   * Get a ZonedDateTime object based on the settings if the key exists.
   *
   * @param settings The settings
   * @param key The key in the settings for the date time.
   * @return The ZonedDateTime.
   */
  public static ZonedDateTime getZonedDateTime(
      final Map<String, String> settings, final String key) {
    return getZonedDateTime(settings, key, ZonedDateTime.now());
  }

  /**
   * Get a ZonedDateTime object based on the settings if the key exists.
   *
   * @param settings The settings
   * @param key The key in the settings for the date time.
   * @param defaultDate The default date to return.
   * @return The ZonedDateTime.
   */
  public static ZonedDateTime getZonedDateTime(
      final Map<String, String> settings, final String key, final ZonedDateTime defaultDate) {
    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return defaultDate;
    }

    String zonedDateTime = settings.get(key);
    if (zonedDateTime.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
      zonedDateTime = zonedDateTime + "T00:00:00Z";
    }

    return ZonedDateTime.parse(zonedDateTime);
  }

  /**
   * Get a Double object based on the settings if the key exists.
   *
   * @param settings The settings
   * @param key The key in the settings for the Double object.
   * @return The Double object.
   */
  public static Double getDouble(final Map<String, String> settings, final String key) {
    if (StringUtils.isEmpty(settings.get(key))) {
      return null;
    }

    return Double.parseDouble(settings.get(key));
  }

  /**
   * Get a Double object based on the settings if the key exists.
   *
   * @param settings The settings
   * @param key The key in the settings for the Double object.
   * @param defaultValue The default value to return if the key wasn't found.
   * @return The Double object.
   */
  public static Double getDouble(
      final Map<String, String> settings, final String key, final Double defaultValue) {
    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return defaultValue;
    }

    return Double.parseDouble(settings.get(key));
  }

  /**
   * Get a Float object based on the settings if the key exists.
   *
   * @param settings The settings
   * @param key The key in the settings for the Float object.
   * @return The Float object.
   */
  public static Float getFloat(final Map<String, String> settings, final String key) {
    if (StringUtils.isEmpty(settings.get(key))) {
      return null;
    }

    return Float.parseFloat(settings.get(key));
  }

  /**
   * Get a Float object based on the settings if the key exists.
   *
   * @param settings The settings
   * @param key The key in the settings for the Float object.
   * @param defaultValue The default value to return if the key wasn't found.
   * @return The Float object.
   */
  public static Float getFloat(
      final Map<String, String> settings, final String key, final Float defaultValue) {
    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
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

  public static Integer getInteger(
      final Map<String, String> settings, final String key, final Integer defaultValue) {

    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return defaultValue;
    }

    return getInteger(settings, key);
  }

  /**
   * Get a long value for the key from the settings.
   *
   * @param settings The settings to get the key from.
   * @param key The key
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
   * @param settings The settings to get the key from.
   * @param key The key
   * @param defaultValue The default value if the key wasn't found.
   * @return The long
   */
  public static Long getLong(
      final Map<String, String> settings, final String key, final Long defaultValue) {

    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
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
   * @param settings The settings to get the key from.
   * @param key The key
   * @param defaultValue The default value if the key wasn't found.
   * @return The long
   */
  public static Short getShort(
      final Map<String, String> settings, final String key, final Short defaultValue) {

    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return defaultValue;
    }

    return Short.parseShort(settings.get(key));
  }

  public static String getString(final Map<String, String> settings, final String key) {
    String value = null;
    if (settings.containsKey(key) && settings.get(key) == null) {
      /*
       * Empty values in feature files were a blank string by default,
       * which has changed to null. Handling this situation here is a way
       * to work around this, without updating feature definitions.
       */
      value = "";
    } else if (settings.containsKey(key)
        && settings.get(key) != null
        && !settings.get(key).equalsIgnoreCase("null")) {
      value = settings.get(key);
    }

    return value;
  }

  public static String getString(
      final Map<String, String> settings, final String key, final String defaultValue) {

    if (!settings.containsKey(key)) {
      return defaultValue;
    }

    return getString(settings, key);
  }

  public static byte[] getHexDecoded(
      final Map<String, String> settings, final String key, final String defaultValue) {
    try {
      if (!settings.containsKey(key)) {
        if (defaultValue == null) {
          return null;
        }
        return Hex.decodeHex(defaultValue.toCharArray());
      } else {
        return Hex.decodeHex(settings.get(key).toCharArray());
      }
    } catch (final DecoderException e) {
      throw new IllegalArgumentException("Could not hex decode value for key " + key, e);
    }
  }

  public static Byte getByte(
      final Map<String, String> settings, final String key, final Byte defaultValue) {

    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return defaultValue;
    }

    return Byte.parseByte(settings.get(key));
  }

  public static String getNullOrNonEmptyString(
      final Map<String, String> settings, final String key, final String defaultValue) {
    final String value = getString(settings, key, defaultValue);
    return ("".equals(value) ? null : value);
  }

  public static <E extends Enum<E>> E getEnum(
      final Map<String, String> settings, final String key, final Class<E> enumType) {
    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return null;
    }

    return Enum.valueOf(enumType, settings.get(key));
  }

  public static List<String> getStringList(
      final Map<String, String> settings, final String key, final String separator) {
    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return new ArrayList<>(0);
    }
    return Arrays.asList(settings.get(key).split(separator));
  }

  public static <E extends Enum<E>> E getEnum(
      final Map<String, String> settings,
      final String key,
      final Class<E> enumType,
      final E defaultValue) {
    if (!settings.containsKey(key) || StringUtils.isBlank(settings.get(key))) {
      return defaultValue;
    }

    return getEnum(settings, key, enumType);
  }
}
