/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.database.core;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getDate;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getEnum;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getFloat;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getInteger;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getLong;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
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
import com.alliander.osgp.domain.core.valueobjects.RelayType;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

@Transactional(value = "txMgrCore")
public class DeviceSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSteps.class);

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    @Autowired
    private OrganisationRepository organizationRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

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
    public Device aDevice(final Map<String, String> settings) throws Throwable {

        // Set the required stuff
        final String deviceIdentification = getString(settings, Keys.DEVICE_IDENTIFICATION);
        final Ssld ssld = new Ssld(deviceIdentification);

        ssld.setPublicKeyPresent(getBoolean(settings, Keys.PUBLICKEYPRESENT, Defaults.PUBLICKEYPRESENT));
        ssld.setHasSchedule(getBoolean(settings, Keys.HAS_SCHEDULE, Defaults.HASSCHEDULE));

        if (settings.containsKey(Keys.INTERNALID) || settings.containsKey(Keys.EXTERNALID)
                || settings.containsKey(Keys.RELAY_TYPE)) {
            final List<DeviceOutputSetting> dosList = new ArrayList<>();
            final int internalId = getInteger(settings, Keys.INTERNALID, Defaults.INTERNALID),
                    externalId = getInteger(settings, Keys.EXTERNALID, Defaults.EXTERNALID);
            final RelayType relayType = getEnum(settings, Keys.RELAY_TYPE, RelayType.class, RelayType.LIGHT);

            if (relayType != null) {
                dosList.add(new DeviceOutputSetting(internalId, externalId, relayType));

                ssld.updateOutputSettings(dosList);
            }
        }

        this.ssldRepository.save(ssld);

        Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        device = this.updateDevice(device, settings);
        
        return device;
    }

    /**
     * Update a device entity given its device identification.
     *
     * @param deviceIdentification
     *            The deviceIdentification.
     * @param settings
     *            The settings.
     */
    public Device updateDevice(final String deviceIdentification, final Map<String, String> settings) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        return this.updateDevice(device, settings);
    }

    /**
     * Update an existing device with the given settings.
     *
     * @param device
     * @param settings
     */
    private Device updateDevice(Device device, final Map<String, String> settings) {

        // Now set the optional stuff
        if (settings.containsKey(Keys.TECHNICAL_INSTALLATION_DATE) && !settings.get(Keys.TECHNICAL_INSTALLATION_DATE).isEmpty()) {
            device.setTechnicalInstallationDate(getDate(settings, Keys.TECHNICAL_INSTALLATION_DATE).toDate());
        }

        final DeviceModel deviceModel = this.deviceModelRepository
                .findByModelCode(getString(settings, Keys.DEVICE_MODEL, Defaults.DEVICE_MODEL_MODEL_CODE));
        device.setDeviceModel(deviceModel);

        device.updateProtocol(this.protocolInfoRepository.findByProtocolAndProtocolVersion(
                getString(settings, Keys.PROTOCOL, Defaults.PROTOCOL),
                getString(settings, Keys.PROTOCOL_VERSION, Defaults.PROTOCOL_VERSION)));

        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(this.configuration.getDeviceNetworkAddress());
        } catch (final UnknownHostException e) {
            inetAddress = InetAddress.getLoopbackAddress();
        }
        device.updateRegistrationData(inetAddress, getString(settings, Keys.DEVICE_TYPE, Defaults.DEVICE_TYPE));

        device.setVersion(getLong(settings, Keys.VERSION));
        device.setActive(getBoolean(settings, Keys.ACTIVE, Defaults.ACTIVE));
        if (getString(settings, Keys.ORGANIZATION_IDENTIFICATION, Defaults.ORGANIZATION_IDENTIFICATION)
                .toLowerCase() != "null") {
            device.addOrganisation(
                    getString(settings, Keys.ORGANIZATION_IDENTIFICATION, Defaults.ORGANIZATION_IDENTIFICATION));
        }
        device.updateMetaData(getString(settings, Keys.ALIAS, Defaults.ALIAS),
                getString(settings, "containerCity", Defaults.CONTAINER_CITY),
                getString(settings, "containerPostalCode", Defaults.CONTAINER_POSTALCODE),
                getString(settings, "containerStreet", Defaults.CONTAINER_STREET),
                getString(settings, "containerNumber", Defaults.CONTAINER_NUMBER),
                getString(settings, "containerMunicipality", Defaults.CONTAINER_MUNICIPALITY),
                getFloat(settings, "gpsLatitude", Defaults.LATITUDE),
                getFloat(settings, "gpsLongitude", Defaults.LONGITUDE));

        device.setActivated(getBoolean(settings, Keys.IS_ACTIVATED, Defaults.IS_ACTIVATED));
        device = this.deviceRepository.save(device);

        if (getString(settings, Keys.ORGANIZATION_IDENTIFICATION, Defaults.ORGANIZATION_IDENTIFICATION)
                .toLowerCase() != "null") {
            final Organisation organization = this.organizationRepository.findByOrganisationIdentification(
                    getString(settings, Keys.ORGANIZATION_IDENTIFICATION, Defaults.ORGANIZATION_IDENTIFICATION));
            final DeviceFunctionGroup functionGroup = getEnum(settings, Keys.DEVICEFUNCTIONGROUP,
                    DeviceFunctionGroup.class, DeviceFunctionGroup.OWNER);
            final DeviceAuthorization authorization = device.addAuthorization(organization, functionGroup);
            final Device savedDevice = this.deviceRepository.save(device);
            this.deviceAuthorizationRepository.save(authorization);
            ScenarioContext.Current().put(Keys.DEVICE_IDENTIFICATION, savedDevice.getDeviceIdentification());
            
            device = savedDevice;
        }
        
        return device;
    }

    @Then("^the device with device identification \"([^\"]*)\" should be active$")
    public void theDeviceWithDeviceIdentificationShouldBeActive(final String deviceIdentification) throws Throwable {

        boolean success = false;
        int count = 0;
        while (!success) {
            try {
                if (count > this.configuration.getTimeout()) {
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
                if (count > this.configuration.getTimeout()) {
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
        Device device = null;

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > this.configuration.getTimeout()) {
                Assert.fail("Failed");
            }

            count++;
            Thread.sleep(1000);
            LoggerFactory.getLogger(DeviceSteps.class).info("Sleeping ls " + count);

            try {
                // Wait for next try to retrieve a response

                device = this.deviceRepository.findByDeviceIdentification(settings.get(Keys.DEVICE_IDENTIFICATION));
                if (device == null) {
                    continue;
                }

                success = true;
            } catch (final Exception e) {
                LOGGER.info(e.getMessage());
            }
        }

        if (settings.containsKey("Alias")) {
            Assert.assertEquals(settings.get("Alias"), device.getAlias());
        }
        if (settings.containsKey("OrganizationIdentification")) {
            Assert.assertEquals(settings.get("OrganizationIdentification"),
                    device.getOwner().getOrganisationIdentification());
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
        if (settings.containsKey("Active")) {
            Assert.assertTrue(Boolean.parseBoolean(settings.get("Active")) == device.isActive());
        }
        if (settings.containsKey("HasSchedule") || settings.containsKey("PublicKeyPresent")) {
            final Ssld ssld = this.ssldRepository.findByDeviceIdentification(settings.get("DeviceIdentification"));

            if (settings.containsKey("HasSchedule")) {
                Assert.assertTrue(Boolean.parseBoolean(settings.get("HasSchedule")) == ssld.getHasSchedule());
            }
            if (settings.containsKey("PublicKeyPresent")) {
                Assert.assertTrue(Boolean.parseBoolean(settings.get("PublicKeyPresent")) == ssld.isPublicKeyPresent());
            }
        }
        if (settings.containsKey("DeviceModel")) {
            Assert.assertEquals(settings.get("DeviceModel"), device.getDeviceModel().getModelCode());
        }
    }

    /**
     * Checks whether the device exists in the database..
     *
     * @param deviceIdentification
     * @return
     */
    @Then("^the device with id \"([^\"]*)\" exists$")
    public void theDeviceWithIdExists(final String deviceIdentification) throws Throwable {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        final List<DeviceAuthorization> devAuths = this.deviceAuthorizationRepository.findByDevice(device);

        Assert.assertNotNull(device);
        Assert.assertTrue(devAuths.size() > 0);
    }

    /**
     * Checks whether the device does not exist in the database.
     *
     * @param deviceIdentification
     * @throws Throwable
     */
    @Then("^the device with id \"([^\"]*)\" does not exists$")
    public void theDeviceShouldBeRemoved(final String deviceIdentification) throws Throwable {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        final List<DeviceAuthorization> devAuths = this.deviceAuthorizationRepository.findByDevice(device);

        Assert.assertNotNull(device);
        Assert.assertTrue(devAuths.size() == 0);
    }

    @Then("^the mbus device \"([^\"]*)\" is coupled to device \"([^\"]*)\" on MBUS channel (\\d+)$")
    public void theMbusDeviceIsCoupledToDeviceOnMBUSChannel(final String gmeter, final String emeter,
            final Short channel) {

        final SmartMeter gSmartmeter = this.smartMeterRepository.findByDeviceIdentification(gmeter);
        final Device eDevice = this.deviceRepository.findByDeviceIdentification(emeter);

        Assert.assertNotNull(eDevice);
        Assert.assertNotNull(gSmartmeter);

        Assert.assertEquals(gSmartmeter.getGatewayDevice(), eDevice);
        Assert.assertEquals(gSmartmeter.getChannel(), channel);
    }

    @Then("^the mbus device \"([^\"]*)\" is not coupled to the device \"([^\"]*)\"$")
    public void theMbusDeviceIsNotCoupledToTheDevice(final String gmeter, final String emeter) {
        final SmartMeter gSmartmeter = this.smartMeterRepository.findByDeviceIdentification(gmeter);
        final Device eDevice = this.deviceRepository.findByDeviceIdentification(emeter);

        Assert.assertNotNull(eDevice);
        Assert.assertNotNull(gSmartmeter);

        Assert.assertNotEquals(gSmartmeter.getGatewayDevice(), eDevice);
    }

    @Then("^the G-meter \"([^\"]*)\" is DeCoupled from device \"([^\"]*)\"$")
    public void theGMeterIsDecoupledFromDevice(final String gmeter, final String emeter) {
        final SmartMeter gSmartmeter = this.smartMeterRepository.findByDeviceIdentification(gmeter);
        final Device eDevice = this.deviceRepository.findByDeviceIdentification(emeter);

        Assert.assertNotNull(eDevice);
        Assert.assertNotNull(gSmartmeter);

        Assert.assertNull(gSmartmeter.getGatewayDevice());
    }

    @Then("^the channel of device \"([^\"]*)\" is cleared$")
    public void theChannelOfDeviceIsCleared(final String gmeter) {
        final SmartMeter gSmartmeter = this.smartMeterRepository.findByDeviceIdentification(gmeter);

        Assert.assertNotNull(gSmartmeter);

        Assert.assertNull(gSmartmeter.getChannel());
    }

}
