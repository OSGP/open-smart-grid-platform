/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;

import cucumber.api.java.en.Given;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;

public class ManifacturSteps {
    
    @Autowired
    private ManufacturerRepository repo;
    
    private String DEFAULT_MANUFACTURER_ID = "1";
    private String DEFAULT_NAME = "Test Manufacturer";
    private Boolean DEFAULT_USEPREFIX = true;
    
    /**
     * Generic method which adds a manufacturer using the settings.
     * 
     * @param settings The settings for the manufacturer to be used.
     * @throws Throwable
     */
    @Given("^a manufacturer")
    public void aManufacturer(final Map<String, String> settings) throws Throwable {
    	
    	Manufacturer entity = new Manufacturer(
    			getString(settings, "ManufacturerId", DEFAULT_MANUFACTURER_ID),
    			getString(settings, "Name", DEFAULT_NAME),
    			getBoolean(settings, "UsePrefix", DEFAULT_USEPREFIX));

		repo.save(entity);
	}
}