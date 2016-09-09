/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database;

import java.net.InetAddress;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getLong;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getDate;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getFloat;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import org.junit.Assert;

public class DeviceSteps {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;
    
    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;
    
    private String DEFAULT_ORGANIZATION_IDENTIFICATION = "test-org";
    private Long DEFAULT_DEVICE_ID = new java.util.Random().nextLong();
    private Boolean DEFAULT_IS_ACTIVATED = true;
    private Boolean DEFAULT_ACTIVE = true;
    private String DEFAULT_ALIAS = "";
    private String DEFAULT_CONTAINER_CITY = "";
    private String DEFAULT_CONTAINER_POSTALCODE = "";
    private String DEFAULT_CONTAINER_STREET = "";
    private String DEFAULT_CONTAINER_NUMBER = "";
    private String DEFAULT_CONTAINER_MUNICIPALITY = "";
    private Float DEFAULT_LATITUDE = new Float(0);
    private Float DEFAULT_LONGITUDE = new Float(0);
    private String DEFAULT_DEVICE_TYPE = "SSLD";
    
    /**
     * Generic method which adds a device using the settings.
     * 
     * @param settings The settings for the device to be used.
     * @throws Throwable
     */
    @Given("^a device$")
    public void aDevice(final Map<String, String> settings) throws Throwable {
    	
    	// Set the required stuff
    	String deviceIdentification = settings.get("DeviceIdentification");
    	Device device = new Device(deviceIdentification);
    	
    	// Now set the optional stuff
    	device.setId(getLong(settings, "DeviceId", DEFAULT_DEVICE_ID));
    	device.setActivated(getBoolean(settings, "IsActivated", DEFAULT_IS_ACTIVATED));
    	device.setTechnicalInstallationDate(getDate(settings, "TechnicalInstallationDate").toDate());

    	if (settings.containsKey("DeviceModelId")) {
        	DeviceModel deviceModel = deviceModelRepository.findOne(getLong(settings, "DeviceModelId"));
        	device.setDeviceModel(deviceModel);
    	}
    	
    	device.updateRegistrationData(
    			InetAddress.getLocalHost(), 
    			getString(settings, "DeviceType", DEFAULT_DEVICE_TYPE));
    	
    	device.setVersion(getLong(settings, "Version"));
    	device.setActive(getBoolean(settings, "Active", DEFAULT_ACTIVE));
    	device.addOrganisation(getString(settings, "Organization", DEFAULT_ORGANIZATION_IDENTIFICATION));
    	device.updateMetaData(
    			getString(settings, "alias", DEFAULT_ALIAS), 
    			getString(settings, "containerCity", DEFAULT_CONTAINER_CITY), 
    			getString(settings, "containerPostalCode", DEFAULT_CONTAINER_POSTALCODE), 
    			getString(settings, "containerStreet", DEFAULT_CONTAINER_STREET), 
    			getString(settings, "containerNumber", DEFAULT_CONTAINER_NUMBER), 
    			getString(settings, "containerMunicipality", DEFAULT_CONTAINER_MUNICIPALITY), 
    			getFloat(settings, "gpsLatitude", DEFAULT_LATITUDE), 
    			getFloat(settings, "gpsLongitude", DEFAULT_LONGITUDE));

    	// 
    	Organisation organization = organizationRepository.findByOrganisationIdentification(
    			getString(settings, "OrganizationIdentification", DEFAULT_ORGANIZATION_IDENTIFICATION));
    	
    	// 
    	DeviceFunctionGroup functionGroup = getEnum(settings, "DeviceFunctionGroup", DeviceFunctionGroup.class, DeviceFunctionGroup.OWNER);
    	
    	device.addAuthorization(organization, functionGroup);
    	deviceRepository.save(device);
    }

	/**
	 * 
	 * @throws Throwable
	 */
    @Then("^the device with device identification \"([^\"]*)\" should be active$")
    public void theDeviceWithDeviceIdentificationShouldBeActive(final String deviceIdentification) throws Throwable {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        
        Assert.assertTrue(device.isActive());
    }

    /**
     * 
     * @param deviceIdentification
     * @throws Throwable
     */
    @Then("^the device with device identification \"([^\"]*)\" should be inactive$")
    public void theDeviceWithDeviceIdentificationShouldBeInActive(final String deviceIdentification) throws Throwable {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        Assert.assertFalse(device.isActive());
    }
}
