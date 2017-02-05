/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.glue.steps.database.core;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.platform.cucumber.Keys;
import com.alliander.osgp.platform.cucumber.config.CoreDeviceConfiguration;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;

public class DeviceSteps extends BaseDeviceSteps {

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private SsldRepository ssldRepository;

    @Then("^the device with device identification \"([^\"]*)\" should be active$")
    public void theDeviceWithDeviceIdentificationShouldBeActive(final String deviceIdentification) throws Throwable {

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > this.configuration.getTimeout()) {
                Assert.fail("Failed");
            }

            // Wait for next try to retrieve a response
            count++;
            Thread.sleep(1000);

            final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

            if (device == null) {
                continue;
            }

            Assert.assertTrue(device.isActive());

            success = true;
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
            if (count > this.configuration.getTimeout()) {
                Assert.fail("Failed");
            }

            // Wait for next try to retrieve a response
            count++;
            Thread.sleep(1000);

            final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            if (device == null)
                continue;

            Assert.assertFalse(device.isActive());

            success = true;
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

            // Wait for next try to retrieve a response
            device = this.deviceRepository.findByDeviceIdentification(settings.get(Keys.DEVICE_IDENTIFICATION));
            if (device == null) {
                continue;
            }

            success = true;
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
