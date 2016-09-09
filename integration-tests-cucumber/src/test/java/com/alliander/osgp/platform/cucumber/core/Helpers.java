/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.core;

import java.io.Serializable;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.Assert;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

public class Helpers {

    protected static final String XPATH_MATCHER_CORRELATIONUID = "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}";

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

        final String value = settings.get(key);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
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

    /**
     *
     * @param settings
     * @param key
     * @param defaultValue
     * @return
     */
    public static Boolean getBoolean(final Map<String, String> settings, final String key, final Boolean defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        final Boolean value = Boolean.parseBoolean(settings.get(key));
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
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
        if (!settings.containsKey(key)) {
            return DateTime.now();
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
     * @param enumType
     * @param defaultValue
     * @return
     */
    public static <E extends Enum<E>> E getEnum(final Map<String, String> settings, final String key,
            final Class<E> enumType, final E defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return Enum.valueOf(enumType, settings.get(key));
    }

    /**
     *
     * @param repo
     * @param entityType
     * @param serialiable
     * @return
     */
    public static AbstractEntity WaitForEntity(final JpaRepository repo, final Class<AbstractEntity> entityType,
            final Serializable serialiable) {
        AbstractEntity entity = null;

        while (entity == null) {
            entity = (AbstractEntity) repo.findOne(serialiable);
        }

        return entity;
    }

    /**
     * Check the correlationUid in the response and save it in the current
     * scenarioContext.
     *
     * @param response
     *            The response to find the correlationUid in.
     * @param organizationIdentification
     *            The organizationIdentifier used. Default test-org will be
     *            used.
     * @throws Throwable
     */
    public static void saveCorrelationUidInScenarioContext(final String correlationUid,
            String organizationIdentification) throws Throwable {
        if (organizationIdentification == null || organizationIdentification.isEmpty()) {
            organizationIdentification = "test-org";
        }

        // TODO why is this check required? It currently does not seem to work

        // Pattern correlationUidPattern =
        // Pattern.compile(organizationIdentification +
        // XPATH_MATCHER_CORRELATIONUID);
        // Matcher correlationUidMatcher =
        // correlationUidPattern.matcher(correlationUid);
        // correlationUidMatcher.find();
        //
        // ScenarioContext.Current().Data.put("CorrelationUid",
        // correlationUidMatcher.group());

        // Validate the correlation-id starts with correct organization
        Assert.isTrue(correlationUid.startsWith(organizationIdentification));
        ScenarioContext.Current().Data.put("CorrelationUid", correlationUid);
    }

    public static <T extends AbstractEntity> void cleanRepoAbstractEntity(final JpaRepository<T, Long> repo) {
        try {
            repo.deleteAll();
        } catch (final Exception e) {

        }
    }

    public static <T extends Serializable> void cleanRepoSerializable(final JpaRepository<T, Long> repo) {
        try {
            repo.deleteAll();
        } catch (final Exception e) {

        }
    }
}
