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
	public static String getString(Map<String, String> settings, String key, String defaultValue) {
		
		if (!settings.containsKey(key)) {
			return defaultValue;
		}
		
		String value = settings.get(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * Get a long value for the key from the settings.
	 * @param settings The settings to get the key from.
	 * @param key The key
	 * @return The long
	 */
	public static Long getLong(Map<String, String> settings, String key) {
		
		if (!settings.containsKey(key)) {
			return new java.util.Random().nextLong();
		}
		
		Long value = Long.parseLong(settings.get(key));
		return value;
	}
	
	/**
	 * Get a long value for the key from the settings.
	 * @param settings The settings to get the key from.
	 * @param key The key
	 * @param defaultValue The default value if the key wasn't found.
	 * @return The long
	 */
	public static Long getLong(Map<String, String> settings, String key, Long defaultValue) {
		
		if (!settings.containsKey(key)) {
			return defaultValue;
		}
		
		Long value = Long.parseLong(settings.get(key));
		return value;
	}
	
	/**
	 * 
	 * @param settings
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static Boolean getBoolean(Map<String, String> settings, String key, Boolean defaultValue) {
		if (!settings.containsKey(key)) {
			return defaultValue;
		}
		
		Boolean value = Boolean.parseBoolean(settings.get(key));
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	/**
	 * Get a date time object based on the settings if the key exists.
	 * @param settings The settings 
	 * @param key The key in the settings for the date time.
	 * @return The date time.
	 */
	public static DateTime getDate(Map<String, String> settings, String key) {
		if (!settings.containsKey(key)) {
			return DateTime.now();
		}
		
		return DateTime.parse(settings.get(key));
	}
	
	/**
	 * Get a float object based on the settings if the key exists.
	 * @param settings The settings
	 * @param key The key in the settings for the float object.
	 * @param defaultValue The default value to return if the key wasn't found.
	 * @return The float object.
	 */
	public static Float getFloat(Map<String, String> settings, String key, Float defaultValue) {
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
	public static <E extends Enum<E>> E getEnum(final Map<String, String> settings, final String key, final Class<E> enumType, final E defaultValue) {
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
	public static AbstractEntity WaitForEntity(JpaRepository repo, Class<AbstractEntity> entityType, Serializable serialiable) {
		AbstractEntity entity = null;
		
		while (entity == null){
			entity = (AbstractEntity) repo.findOne(serialiable);
		}
		
		return entity;
	}

	/**
     * Check the correlationUid in the response and save it in the current scenarioContext.
     * 
     * @param response The response to find the correlationUid in.
     * @param organizationIdentification The organizationIdentifier used. Default test-org will be used.
     * @throws Throwable 
     */
    public static void saveCorrelationUidInScenarioContext(final String correlationUid, String organizationIdentification) throws Throwable {
    	if (organizationIdentification == null || organizationIdentification.isEmpty()) {
    		organizationIdentification = "test-org";
    	}
    	
    	Pattern correlationUidPattern = Pattern.compile(organizationIdentification + XPATH_MATCHER_CORRELATIONUID);
        Matcher correlationUidMatcher = correlationUidPattern.matcher(correlationUid);
        correlationUidMatcher.find();
        
        ScenarioContext.Current().Data.put("CorrelationUid", correlationUidMatcher.group());
    }
}
