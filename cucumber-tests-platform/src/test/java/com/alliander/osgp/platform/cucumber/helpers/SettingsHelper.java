package com.alliander.osgp.platform.cucumber.helpers;

import java.util.HashMap;
import java.util.Map;

import cucumber.api.DataTable;

/**
 * Helper class, to provide easy options to extend settings in the form of
 * key-value pairs obtained from two column Cucumber {@link DataTable data
 * tables} that are transformed into a map.
 */
public class SettingsHelper {

    private SettingsHelper() {
        // Utility class, keep constructor private.
    }

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
}
