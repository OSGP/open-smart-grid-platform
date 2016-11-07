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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.Assert;

import com.alliander.osgp.platform.cucumber.steps.Defaults;
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
     * Get the boolean value of the given key in the settings. If it didn't exist return the defaultValue.
     * @param settings The settings to get the value from.
     * @param key The key to get the boolean from.
     * @param defaultValue The default value if the key wasn't found.
     * @return
     */
    public static Boolean getBoolean(final Map<String, String> settings, final String key, final Boolean defaultValue) {
        if (!settings.containsKey(key)) {
            return defaultValue;
        }

        return Boolean.parseBoolean(settings.get(key));
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
            organizationIdentification = Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION;
        }
        
        // Validate the correlation-id starts with correct organization
        Assert.isTrue(correlationUid.startsWith(organizationIdentification));
        ScenarioContext.Current().put("CorrelationUid", correlationUid);
    }

    /**
     * When running automatic tests, it might be that not each project is started in tomcat. When 
     * the repo's are cleared at the beginning of a test run, you get some exceptions when the database wasn't
     * found. Therefore this method is created to ignore that. 
     * @param repo The repository to remove.
     */
    public static <T extends AbstractEntity> void cleanRepoAbstractEntity(final JpaRepository<T, Long> repo) {
        try {
            repo.deleteAll();
        } catch (final Exception e) {

        }
    }

    /**
     * When running automatic tests, it might be that not each project is started in tomcat. When 
     * the repo's are cleared at the beginning of a test run, you get some exceptions when the database wasn't
     * found. Therefore this method is created to ignore that. 
     * @param repo
     */
    public static <T extends Serializable> void cleanRepoSerializable(final JpaRepository<T, Long> repo) {
        try {
            repo.deleteAll();
        } catch (final Exception e) {

        }
    }
    
    /**
     * This is a generic method which will translate the given string to a datetime.
     * Supported:
     *   now + 3 months
     *   tomorrow - 1 year
     *   yesterday + 2 weeks
     *    
     * @param dateString
     * @return
     * @throws Exception
     */
	public static DateTime getDateTime(final String dateString) throws Exception {
		
		DateTime retval;
		
		String pattern = "([a-z]*)[ ]*([+-]?)[ ]*([0-9]*)[ ]*([a-z]*)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(dateString);
		
		if (m.groupCount() != 4) {
			throw new Exception("Incorrect dateString [" + dateString + "]");
		}
		
		m.find();
         
		String when = m.group(1).toLowerCase();
		String op = m.group(2);
		Integer numberToAddOrSubstract = Integer.parseInt(m.group(3));
		String what = m.group(4);
		
		switch (when) {
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
			throw new Exception("Incorrect dateString [" + dateString + "]");
		}

		switch (what) {
		case "days":
			if (op.equals("+")) {
				retval = retval.plusDays(numberToAddOrSubstract);
			} else {
				retval = retval.minusDays(numberToAddOrSubstract);
			}
		case "hours":
			if (op.equals("+")) {
				retval = retval.plusHours(numberToAddOrSubstract);
			} else {
				retval = retval.minusHours(numberToAddOrSubstract);
			}
		case "weeks":
			if (op.equals("+")) {
				retval = retval.plusWeeks(numberToAddOrSubstract);
			} else {
				retval = retval.minusWeeks(numberToAddOrSubstract);
			}
		case "months":
			if (op.equals("+")) {
				retval = retval.plusMonths(numberToAddOrSubstract);
			} else {
				retval = retval.minusMonths(numberToAddOrSubstract);
			}
		case "years":
			if (op.equals("+")) {
				retval = retval.plusYears(numberToAddOrSubstract);
			} else {
				retval = retval.minusYears(numberToAddOrSubstract);
			}
		}
		
		return retval;
	}
}
