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

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

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

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    @Autowired
    private DeviceAuthorizationRepository authorisationRepository;

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
        Device device = new Device(deviceIdentification);

        // Now set the optional stuff
        device.setId(getLong(settings, "DeviceId", this.DEFAULT_DEVICE_ID));
        device.setActivated(getBoolean(settings, "IsActivated", this.DEFAULT_IS_ACTIVATED));
        device.setTechnicalInstallationDate(getDate(settings, "TechnicalInstallationDate").toDate());

        if (settings.containsKey("DeviceModelId")) {
            final DeviceModel deviceModel = this.deviceModelRepository.findOne(getLong(settings, "DeviceModelId"));
            device.setDeviceModel(deviceModel);
        }
        // TODO: add protocol information in controlled place
        device.updateProtocol(this.protocolInfoRepository.findByProtocolAndProtocolVersion("OSLP", "1.0"));

        // TODO: Add metadata if required
        device.updateRegistrationData(InetAddress.getLocalHost(),
                getString(settings, "DeviceType", this.DEFAULT_DEVICE_TYPE));

        device.setVersion(getLong(settings, "Version"));
        device.setActive(getBoolean(settings, "Active", this.DEFAULT_ACTIVE));
        device.addOrganisation(getString(settings, "Organization", this.DEFAULT_ORGANIZATION_IDENTIFICATION));
        device.updateMetaData(getString(settings, "alias", this.DEFAULT_ALIAS),
                getString(settings, "containerCity", this.DEFAULT_CONTAINER_CITY),
                getString(settings, "containerPostalCode", this.DEFAULT_CONTAINER_POSTALCODE),
                getString(settings, "containerStreet", this.DEFAULT_CONTAINER_STREET),
                getString(settings, "containerNumber", this.DEFAULT_CONTAINER_NUMBER),
                getString(settings, "containerMunicipality", this.DEFAULT_CONTAINER_MUNICIPALITY),
                getFloat(settings, "gpsLatitude", this.DEFAULT_LATITUDE),
                getFloat(settings, "gpsLongitude", this.DEFAULT_LONGITUDE));

        //
        final Organisation organization = this.organizationRepository.findByOrganisationIdentification(
                getString(settings, "OrganizationIdentification", this.DEFAULT_ORGANIZATION_IDENTIFICATION));

        //
        final DeviceFunctionGroup functionGroup = getEnum(settings, "DeviceFunctionGroup", DeviceFunctionGroup.class,
                DeviceFunctionGroup.OWNER);

        device = this.deviceRepository.save(device);
        final DeviceAuthorization auth = device.addAuthorization(organization, functionGroup);
        this.authorisationRepository.save(auth);
        this.deviceRepository.save(device);
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
