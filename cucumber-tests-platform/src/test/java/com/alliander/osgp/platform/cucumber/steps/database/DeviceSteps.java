/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getDate;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getFloat;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getLong;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getShort;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

@Transactional("txMgrCore")
@Component
public class DeviceSteps {

    public static String DEFAULT_DEVICE_IDENTIFICATION = "test-device";
    public static String DEFAULT_DEVICE_TYPE = "OSLP";
    public static String DEFAULT_PROTOCOL = "OSLP";
    public static String DEFAULT_PROTOCOL_VERSION = "1.0";
    @SuppressWarnings("unused")
	private final Long DEFAULT_DEVICE_ID = new java.util.Random().nextLong();
    private static Boolean DEFAULT_IS_ACTIVATED = true;
    private static Boolean DEFAULT_ACTIVE = true;
    private static String DEFAULT_ALIAS = "";
    private static String DEFAULT_CONTAINER_CITY = "";
    private static String DEFAULT_CONTAINER_POSTALCODE = "";
    private static String DEFAULT_CONTAINER_STREET = "";
    private static String DEFAULT_CONTAINER_NUMBER = "";
    private static String DEFAULT_CONTAINER_MUNICIPALITY = "";
    private final Float DEFAULT_LATITUDE = new Float(0);
    private final Float DEFAULT_LONGITUDE = new Float(0);
    private final Short DEFAULT_CHANNEL = new Short((short) 1);

//    private static String SMART_E_METER_DEVTYPE = "Smart-E-Meter";
//    private static String SMART_G_METER_DEVTYPE = "Smart-G-Meter";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    /**
     * Generic method which adds a device using the settings.
     *
     * @param settings
     *            The settings for the device to be used.
     * @throws Throwable
     */
    @Given("^a device$")
    public void aDevice(final Map<String, String> settings) throws Throwable {

        // Set the required stuff
        final String deviceIdentification = settings.get(Keys.KEY_DEVICE_IDENTIFICATION);
        final Device device = new Device(deviceIdentification);

        this.updateDevice(device, settings);
    }

    @Given("^a smart meter$")
    public void aSmartMeter(final Map<String, String> inputSettings) {
    	final SmartMeter smartMeter = new SmartMeter(
        		getString(inputSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION),
        		getString(inputSettings, Keys.KEY_ALIAS, DEFAULT_ALIAS),
        		getString(inputSettings, Keys.KEY_CITY, DEFAULT_CONTAINER_CITY),
        		getString(inputSettings, Keys.KEY_POSTCODE, DEFAULT_CONTAINER_POSTALCODE),
        		getString(inputSettings, Keys.KEY_STREET, DEFAULT_CONTAINER_STREET),
        		getString(inputSettings, Keys.KEY_NUMBER, DEFAULT_CONTAINER_NUMBER),
        		getString(inputSettings, Keys.KEY_MUNICIPALITY, DEFAULT_CONTAINER_MUNICIPALITY),
        		getFloat(inputSettings, Keys.KEY_LATITUDE, this.DEFAULT_LATITUDE),
        		getFloat(inputSettings, Keys.KEY_LONGITUDE, this.DEFAULT_LONGITUDE)
        		);

    	final Protocol protocol = ProtocolHelper.getProtocol(Protocol.ProtocolType.DSMR);
        Map<String, String> settings = this.putSetting(inputSettings, Keys.KEY_PROTOCOL, protocol.getProtocol());
        settings = this.putSetting(settings, Keys.KEY_PROTOCOL_VERSION, protocol.getVersion());

        if (settings.containsKey(Keys.KEY_GATEWAY_DEVICE_ID)) {
            smartMeter.setChannel(getShort(settings, Keys.KEY_CHANNEL, this.DEFAULT_CHANNEL));
            final Device smartEMeter = this.deviceRepository.findByDeviceIdentification(settings.get(Keys.KEY_GATEWAY_DEVICE_ID));
            smartMeter.updateGatewayDevice(smartEMeter);
        }

        this.smartMeterRepository.save(smartMeter);

    	final Device device = this.deviceRepository.findByDeviceIdentification(getString(inputSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    	this.updateDevice(device, settings);
    }

    private Map<String, String> putSetting(final Map<String, String> settings, final String key, final String value) {
        final Map<String, String> result = new HashMap<String, String>();
        result.putAll(settings);
        result.put(key, value);
        return result;
    }

    private void updateDevice(Device device, final Map<String, String> settings) {

        // Now set the optional stuff
        device.setActivated(getBoolean(settings, Keys.KEY_IS_ACTIVATED, DEFAULT_IS_ACTIVATED));
        device.setTechnicalInstallationDate(getDate(settings, Keys.KEY_TECH_INSTALL_DATE).toDate());

        final DeviceModel deviceModel = this.deviceModelRepository
                .findByModelCode(getString(settings, Keys.KEY_DEVICE_MODEL, Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));
        device.setDeviceModel(deviceModel);

        device.updateProtocol(this.protocolInfoRepository.findByProtocolAndProtocolVersion(
                getString(settings, Keys.KEY_PROTOCOL, DeviceSteps.DEFAULT_PROTOCOL),
                getString(settings, Keys.KEY_PROTOCOL_VERSION, DeviceSteps.DEFAULT_PROTOCOL_VERSION)));

        device.updateRegistrationData(InetAddress.getLoopbackAddress(),
                getString(settings, Keys.KEY_DEVICE_TYPE, DeviceSteps.DEFAULT_DEVICE_TYPE));

        device.setVersion(getLong(settings, Keys.KEY_VERSION));
        device.setActive(getBoolean(settings, Keys.KEY_ACTIVE, DEFAULT_ACTIVE));
        device.addOrganisation(getString(settings, Keys.KEY_ORGANIZATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        device.updateMetaData(getString(settings, Keys.KEY_ALIAS, DEFAULT_ALIAS),
                getString(settings, Keys.KEY_CITY, DEFAULT_CONTAINER_CITY),
                getString(settings, Keys.KEY_POSTCODE, DEFAULT_CONTAINER_POSTALCODE),
                getString(settings, Keys.KEY_STREET, DEFAULT_CONTAINER_STREET),
                getString(settings, Keys.KEY_NUMBER, DEFAULT_CONTAINER_NUMBER),
                getString(settings, Keys.KEY_MUNICIPALITY, DEFAULT_CONTAINER_MUNICIPALITY),
                getFloat(settings, Keys.KEY_LATITUDE, this.DEFAULT_LATITUDE),
                getFloat(settings, Keys.KEY_LONGITUDE, this.DEFAULT_LONGITUDE));

        device = this.deviceRepository.save(device);

        final Organisation organization = this.organizationRepository.findByOrganisationIdentification(
                getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        final DeviceFunctionGroup functionGroup = getEnum(settings, Keys.KEY_DEVICE_FUNCTION_GRP, DeviceFunctionGroup.class,
                DeviceFunctionGroup.OWNER);

        final DeviceAuthorization authorization = device.addAuthorization(organization, functionGroup);
        final Device savedDevice = this.deviceRepository.save(device);
        this.deviceAuthorizationRepository.save(authorization);

        ScenarioContext.Current().put(Keys.KEY_DEVICE_IDENTIFICATION, savedDevice.getDeviceIdentification());
    }

    /**
     *
     * @throws Throwable
     */
    @Then("^the device with device identification \"([^\"]*)\" should be active$")
    public void theDeviceWithDeviceIdentificationShouldBeActive(final String deviceIdentification) throws Throwable {

    	boolean success = false;
        int count = 0;
    	while (!success) {
    		try {
    			if (count > 120) {
	                Assert.fail("Failed");
	            }

	            // Wait for next try to retrieve a response
	            count++;
	            Thread.sleep(1000);

    	        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

    	        Assert.assertTrue(device.isActive());

    	        success = true;
    		}
    		catch(final Exception e)
    		{
    			// Do nothing
    		}
    	}
    }

	/**
     *
     * @param deviceIdentification
     * @throws Throwable
     */
    @Then("^the device with device identification \"([^\"]*)\" should be inactive$")
    public void theDeviceWithDeviceIdentificationShouldBeInActive(final String deviceIdentification) throws Throwable {
    	boolean success = false;
    	int count = 0;
    	while (!success) {
    		try {
    			if (count > 120) {
	                Assert.fail("Failed");
	            }

	            // Wait for next try to retrieve a response
	            count++;
	            Thread.sleep(1000);

		        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
		        Assert.assertFalse(device.isActive());

		        success = true;
    		}
    		catch(final Exception e)
    		{
    			// Do nothing
    		}
    	}
    }

    /**
     * check that the given device is inserted
     *
     * @param deviceIdentification
     * @return
     */
    @Then("^the device with the id \"([^\"]*)\" exists$")
    public void theDeviceWithIdExists(final String deviceIdentification) throws Throwable {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        final List<DeviceAuthorization> devAuths = this.deviceAuthorizationRepository.findByDevice(device);

        Assert.assertNotNull(device);
        Assert.assertTrue(devAuths.size() > 0);
    }

}
