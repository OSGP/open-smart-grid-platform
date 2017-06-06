/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ScenarioContext class is used to store scenario specific data. It will be destroyed after each 
 */
public class ScenarioContext {
	
	/**
	 * The current context.
	 */
	public static ScenarioContext context = null;
	
	/**
	 * The data object to store the scenario specific context in.
	 */
    private final Map<String, Object> data = new HashMap<String, Object>();
	
	/**
	 * Get the current context and instantiate it.
	 * @return
	 */
	public static ScenarioContext current() {
		if (context == null) {
			context = new ScenarioContext();
		}
		
		return context;
	}
	
	public Object get(final Object key) {
        return this.data.get(key);
	}

    public void put(final String key, final Object value) {
        this.data.put(key, value);
    }

    public Object get(final String key, final String defaultValue) {
        if (!this.data.containsKey(key)) {
            return defaultValue;
        } else {
            return this.get(key);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(final String key) {
        return (List<T>) this.data.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> void addToList(final String key, final T value) {
        if (this.data.containsKey(key)) {
            ((List<T>) this.data.get(key)).add(value);
        } else {
            this.data.put(key, new ArrayList<>(Arrays.asList(value)));
        }
    }

}
