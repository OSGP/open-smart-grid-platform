/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.core;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getFloat;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getShort;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

import cucumber.api.java.en.Given;

@Transactional("txMgrCore")
public class SmartMeterSteps {

    private static final String DEFAULT_SUPPLIER = "Kaifa";

    @Autowired
    private SmartMeterRepository smartMeterRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private DeviceSteps deviceSteps;
    
    /**
     * Given a smart meter exists.
     * 
     * @param settings
     */
    @Given("^a smart meter$")
    public void aSmartMeter(final Map<String, String> settings) {
        
        final String deviceIdentification =getString(settings, "DeviceIdentification", Defaults.DEFAULT_DEVICE_IDENTIFICATION); 
    	SmartMeter smartMeter = new SmartMeter(
    	        deviceIdentification,
        		getString(settings, "Alias", Defaults.DEFAULT_ALIAS),
        		getString(settings, "ContainerCity", Defaults.DEFAULT_CONTAINER_CITY),
        		getString(settings, "ContainerPostalCode", Defaults.DEFAULT_CONTAINER_POSTALCODE),
        		getString(settings, "ContainerStreet", Defaults.DEFAULT_CONTAINER_STREET),
        		getString(settings, "ContainerNumber", Defaults.DEFAULT_CONTAINER_NUMBER),
        		getString(settings, "ContainerMunicipality", Defaults.DEFAULT_CONTAINER_MUNICIPALITY),
        		getFloat(settings, "GPSLatitude", Defaults.DEFAULT_LATITUDE),
        		getFloat(settings, "GPSLongitude", Defaults.DEFAULT_LONGITUDE)
        		);
    	
    	smartMeter.setSupplier(getString(settings, "Supplier", DEFAULT_SUPPLIER));
    	
        if (settings.containsKey(Keys.KEY_GATEWAY_DEVICE_ID)) {
            smartMeter.setChannel(getShort(settings, Keys.KEY_CHANNEL, Defaults.DEFAULT_CHANNEL));
            final Device smartEMeter = this.deviceRepository.findByDeviceIdentification(settings.get(Keys.KEY_GATEWAY_DEVICE_ID));
            smartMeter.updateGatewayDevice(smartEMeter);
        }
    	
    	smartMeterRepository.save(smartMeter);
    	
    	deviceSteps.updateDevice(deviceIdentification, settings);
    }
}
