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
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.net.InetAddress;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

@Transactional("txMgrCore")
public class DeviceSteps {

    private static final String DEFAULT_SUPPLIER = "Kaifa";
    public static String DEFAULT_DEVICE_IDENTIFICATION = "test-device";
    public static String DEFAULT_DEVICE_TYPE = "OSLP";
    public static String DEFAULT_PROTOCOL = "OSLP";
    public static String DEFAULT_PROTOCOL_VERSION = "1.0";
    @SuppressWarnings("unused")
    private Long DEFAULT_DEVICE_ID = new java.util.Random().nextLong();
    private static Boolean DEFAULT_IS_ACTIVATED = true;
    private static Boolean DEFAULT_ACTIVE = true;
    private static String DEFAULT_ALIAS = "";
    private static String DEFAULT_CONTAINER_CITY = "";
    private static String DEFAULT_CONTAINER_POSTALCODE = "";
    private static String DEFAULT_CONTAINER_STREET = "";
    private static String DEFAULT_CONTAINER_NUMBER = "";
    private static String DEFAULT_CONTAINER_MUNICIPALITY = "";
    private Float DEFAULT_LATITUDE = new Float(0);
    private Float DEFAULT_LONGITUDE = new Float(0);

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
    
    @Autowired
    private SsldRepository ssldRepository;

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
        final String deviceIdentification = settings.get("DeviceIdentification");
        final Ssld ssld = new Ssld(deviceIdentification);
        
        ssld.setPublicKeyPresent(getBoolean(settings, "PublicKeyPresent", Defaults.DEFAULT_PUBLICKEYPRESENT));
        
        this.ssldRepository.save(ssld);
        
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        this.updateDevice(device, settings);
    }

    /**
     * Given a smart meter exists.
     * 
     * @param settings
     */
    @Given("^a smart meter$")
    public void aSmartMeter(final Map<String, String> settings) {
    	SmartMeter smartMeter = new SmartMeter(
        		getString(settings, "DeviceIdentification", Defaults.DEFAULT_DEVICE_IDENTIFICATION),
        		getString(settings, "Alias", DEFAULT_ALIAS),
        		getString(settings, "ContainerCity", DEFAULT_CONTAINER_CITY),
        		getString(settings, "ContainerPostalCode", DEFAULT_CONTAINER_POSTALCODE),
        		getString(settings, "ContainerStreet", DEFAULT_CONTAINER_STREET),
        		getString(settings, "ContainerNumber", DEFAULT_CONTAINER_NUMBER),
        		getString(settings, "ContainerMunicipality", DEFAULT_CONTAINER_MUNICIPALITY),
        		getFloat(settings, "GPSLatitude", DEFAULT_LATITUDE),
        		getFloat(settings, "GPSLongitude", DEFAULT_LONGITUDE)
        		);
    	
    	smartMeter.setSupplier(getString(settings, "Supplier", DEFAULT_SUPPLIER));
    	
    	smartMeterRepository.save(smartMeter);
    	
    	Device device = deviceRepository.findByDeviceIdentification(getString(settings, "DeviceIdentification", Defaults.DEFAULT_DEVICE_IDENTIFICATION));	
    	updateDevice(device, settings);
    }

    /**
     * Update an existing device with the given settings.
     * 
     * @param device
     * @param settings
     */
    private void updateDevice(Device device, final Map<String, String> settings) {

        // Now set the optional stuff
        device.setActivated(getBoolean(settings, "IsActivated", DEFAULT_IS_ACTIVATED));
        device.setTechnicalInstallationDate(getDate(settings, "TechnicalInstallationDate").toDate());

        final DeviceModel deviceModel = this.deviceModelRepository
                .findByModelCode(getString(settings, "DeviceModel", Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));
        device.setDeviceModel(deviceModel);

        // TODO: add protocol information in controlled place
        device.updateProtocol(this.protocolInfoRepository.findByProtocolAndProtocolVersion(
                getString(settings, "Protocol", DeviceSteps.DEFAULT_PROTOCOL),
                getString(settings, "ProtocolVersion", DeviceSteps.DEFAULT_PROTOCOL_VERSION)));

        device.updateRegistrationData(InetAddress.getLoopbackAddress(),
                getString(settings, "DeviceType", DeviceSteps.DEFAULT_DEVICE_TYPE));

        device.setVersion(getLong(settings, "Version"));
        device.setActive(getBoolean(settings, "Active", DEFAULT_ACTIVE));
        if (getString(settings, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION).toLowerCase() != "null") {
            device.addOrganisation(getString(settings, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        }
        device.updateMetaData(getString(settings, "Alias", DEFAULT_ALIAS),
                getString(settings, "containerCity", DEFAULT_CONTAINER_CITY),
                getString(settings, "containerPostalCode", DEFAULT_CONTAINER_POSTALCODE),
                getString(settings, "containerStreet", DEFAULT_CONTAINER_STREET),
                getString(settings, "containerNumber", DEFAULT_CONTAINER_NUMBER),
                getString(settings, "containerMunicipality", DEFAULT_CONTAINER_MUNICIPALITY),
                getFloat(settings, "gpsLatitude", this.DEFAULT_LATITUDE),
                getFloat(settings, "gpsLongitude", this.DEFAULT_LONGITUDE));

        device = this.deviceRepository.save(device);
        
        final Organisation organization = this.organizationRepository.findByOrganisationIdentification(
                getString(settings, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        if (getString(settings, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION).toLowerCase() != "null") {
            final DeviceFunctionGroup functionGroup = getEnum(settings, "DeviceFunctionGroup", DeviceFunctionGroup.class,
                    DeviceFunctionGroup.OWNER);
            final DeviceAuthorization authorization = device.addAuthorization(organization, functionGroup);
            final Device savedDevice = this.deviceRepository.save(device);
            this.deviceAuthorizationRepository.save(authorization);
            ScenarioContext.Current().put("DeviceIdentification", savedDevice.getDeviceIdentification());
        }

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
            } catch (final Exception e) {
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
            } catch (final Exception e) {
                // Do nothing
            }
        }
    }

    /**
     * Verify that the device exists in the database.
     *
     * @return
     */
    @And("^the device exists")
    public void theDeviceExists(final Map<String, String> settings) throws Throwable {
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

                final Device device = this.deviceRepository.findByDeviceIdentification(settings.get("DeviceIdentification"));
                Assert.assertNotNull(device);
                
                if (settings.containsKey("Alias")) {
                    Assert.assertEquals(settings.get("Alias"), device.getAlias());
                }
                if (settings.containsKey("OrganizationIdentification")) {
                    Assert.assertEquals(settings.get("OrganizationIdentification"), device.getOwner().getOrganisationIdentification());
                }
                if (settings.containsKey("ContainerPostalCode")) {
                    Assert.assertEquals(settings.get("ContainerPostalCode"), device.getContainerPostalCode());
                }
                if (settings.containsKey("ContainerCity")) {
                    Assert.assertEquals(settings.get("ContainerCity"), device.getContainerCity());
                }
                if (settings.containsKey("ContainerStreet")) {
                    Assert.assertEquals(settings.get("ContainerStreet"), device.getContainerStreet());
                }
                if (settings.containsKey("ContainerNumber")) {
                    Assert.assertEquals(settings.get("ContainerNumber"), device.getContainerNumber());
                }
                if (settings.containsKey("ContainerMunicipality")) {
                    Assert.assertEquals(settings.get("ContainerMunicipality"), device.getContainerMunicipality());
                }
                if (settings.containsKey("GpsLatitude")) {
                    Assert.assertTrue(Float.parseFloat(settings.get("GpsLatitude")) == device.getGpsLatitude());
                }
                if (settings.containsKey("GpsLongitude")) {
                    Assert.assertTrue(Float.parseFloat(settings.get("GpsLongitude")) == device.getGpsLongitude());
                }
                if (settings.containsKey("Activated")) {
                    Assert.assertTrue(Boolean.parseBoolean(settings.get("Activated")) == device.isActivated());
                }
                if (settings.containsKey("HasSchedule") || settings.containsKey("PublicKeyPresent")) {
                    final Ssld ssld = this.ssldRepository.findByDeviceIdentification(settings.get("DeviceIdentification"));
                    
                    if (settings.containsKey("HasSchedule")){
                        Assert.assertTrue(Boolean.parseBoolean(settings.get("HasSchedule")) == ssld.getHasSchedule());
                    }
                    if (settings.containsKey("PublicKeyPresent")){
                        Assert.assertTrue(Boolean.parseBoolean(settings.get("PublicKeyPresent")) == ssld.isPublicKeyPresent());
                    }
                }
                if (settings.containsKey("DeviceModel")) {
                    Assert.assertEquals(settings.get("DeviceModel"), device.getDeviceModel().getModelCode());
                }
                
                success = true;
            } catch (final Exception | AssertionError e) {
                // Do nothing
            }
        }
    }
}
