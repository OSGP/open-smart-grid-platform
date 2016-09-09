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

import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.ManufacturerRepository;

import cucumber.api.java.en.Given;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getLong;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;

public class DeviceModelSteps {
    
    @Autowired
    private DeviceModelRepository repo;

    @Autowired
    private ManufacturerRepository manufacturerRepo;

    private String DEFAULT_MODELCODE= "Test Model Code";
    private String DEFAULT_DESCRIPTION = "Test Model";
    private Boolean DEFAULT_FILESTORAGE = true;
    
    /**
     * Generic method which adds a device model using the settings.
     * 
     * @param settings The settings for the device model to be used.
     * @throws Throwable
     */
    @Given("^a device model")
    public void aDeviceModel(final Map<String, String> settings) throws Throwable {
    	
    	Manufacturer manufacturer = manufacturerRepo.findByName(settings.get("ManufacturerName"));
    	    
    	DeviceModel entity = new DeviceModel(
    			manufacturer,
    			getString(settings, "ModelCode", DEFAULT_MODELCODE),
    			getString(settings, "Description", DEFAULT_DESCRIPTION),
    			getBoolean(settings, "FileStorage", DEFAULT_FILESTORAGE));

    	entity.setVersion(getLong(settings, "Version"));
		
		repo.save(entity);
	}
}