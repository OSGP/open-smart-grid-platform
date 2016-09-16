/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.core;

import java.util.HashMap;
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
	private Map<String, Object> Data = new HashMap<String, Object>();
	
	/**
	 * Get the current context and instantiate it.
	 * @return
	 */
	public static ScenarioContext Current() {
		if (context == null) {
			context = new ScenarioContext();
		}
		
		return context;
	}
	
	public Object get(final Object key) {
	    return this.Data.get(key);
	}

    public void put(final String key, final Object value) {
        this.Data.put(key, value);
    }

    public Object get(String key, String defaultValue) {
        if (!this.Data.containsKey(key)) {
            return defaultValue;
        } else {
            return this.get(key);
        }
    }
}
