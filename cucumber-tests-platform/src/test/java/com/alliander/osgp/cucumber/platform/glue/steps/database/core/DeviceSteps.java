/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;

public class DeviceSteps extends BaseDeviceSteps {

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    @Autowired
    private SsldRepository ssldRepository;

    @Then("^the channel of device \"([^\"]*)\" is cleared$")
    public void theChannelOfDeviceIsCleared(final String gmeter) {
        final SmartMeter gSmartmeter = this.smartMeterRepository.findByDeviceIdentification(gmeter);

        Assert.assertNotNull(gSmartmeter);

        Assert.assertNull(gSmartmeter.getChannel());
    }

    /**
     * Verify that the device exists in the database.
     *
     * @return
     */
    @And("^the device exists")
    public void theDeviceExists(final Map<String, String> settings) throws Throwable {
        final Device device = Wait.untilAndReturn(() -> {
            final Device entity = this.deviceRepository
                    .findByDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
            if (entity == null) {
                throw new Exception(
                        "Device with identification [" + settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION) + "]");
            }

            return entity;
        });

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
     * Checks whether the device does not exist in the database.
     *
     * @param deviceIdentification
     * @throws Throwable
     */
    @Then("^the device with id \"([^\"]*)\" should be removed$")
    public void theDeviceShouldBeRemoved(final String deviceIdentification) throws Throwable {
        Wait.until(() -> {
            final Device entity = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            Assert.assertNull("Device with identification [" + deviceIdentification + "] should be removed", entity);

            final List<DeviceAuthorization> devAuths = this.deviceAuthorizationRepository.findByDevice(entity);
            Assert.assertTrue("DeviceAuthorizations for device with identification [" + deviceIdentification + "] should be removed", devAuths.isEmpty());
        });
    }

    /**
     * Checks whether the device does not exist in the database.
     *
     * @param deviceIdentification
     * @throws Throwable
     */
    @Then("^the device with id \"([^\"]*)\" does not exist$")
    public void theDeviceWithIdDoesNotExist(final String deviceIdentification) throws Throwable {
        Wait.until(() -> {
            final Device entity = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            Assert.assertNull("Device with identification [" + deviceIdentification + "]", entity);
        });
    }

    @Then("^the device with device identification \"([^\"]*)\" should be active$")
    public void theDeviceWithDeviceIdentificationShouldBeActive(final String deviceIdentification) throws Throwable {

        Wait.until(() -> {
            final Device entity = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            Assert.assertNotNull("Device with identification [" + deviceIdentification + "]", entity);

            Assert.assertTrue(entity.isActive());
        });
    }

    /**
     *
     * @param deviceIdentification
     * @throws Throwable
     */
    @Then("^the device with device identification \"([^\"]*)\" should be inactive$")
    public void theDeviceWithDeviceIdentificationShouldBeInActive(final String deviceIdentification) throws Throwable {
        Wait.until(() -> {
            final Device entity = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            Assert.assertNotNull("Device with identification [" + deviceIdentification + "]", entity);

            Assert.assertFalse(entity.isActive());
        });
    }

    /**
     * Checks whether the device exists in the database..
     *
     * @param deviceIdentification
     * @return
     */
    @Then("^the device with id \"([^\"]*)\" exists$")
    public void theDeviceWithIdExists(final String deviceIdentification) throws Throwable {
        Wait.until(() -> {
            final Device entity = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            Assert.assertNotNull("Device with identification [" + deviceIdentification + "]", entity);

            final List<DeviceAuthorization> devAuths = this.deviceAuthorizationRepository.findByDevice(entity);

            Assert.assertNotNull(entity);
            Assert.assertTrue(devAuths.size() > 0);
        });
    }

    @Then("^the G-meter \"([^\"]*)\" is DeCoupled from device \"([^\"]*)\"$")
    public void theGMeterIsDecoupledFromDevice(final String gmeter, final String emeter) {
        Wait.until(() -> {
            final SmartMeter gSmartmeter = this.smartMeterRepository.findByDeviceIdentification(gmeter);
            final Device eDevice = this.deviceRepository.findByDeviceIdentification(emeter);

            Assert.assertNotNull(eDevice);
            Assert.assertNotNull(gSmartmeter);

            Assert.assertNull(gSmartmeter.getGatewayDevice());
        });
    }

    @Then("^the mbus device \"([^\"]*)\" is coupled to device \"([^\"]*)\" on MBUS channel (\\d+)$")
    public void theMbusDeviceIsCoupledToDeviceOnMBUSChannel(final String gmeter, final String emeter,
            final Short channel) {
        Wait.until(() -> {
            final SmartMeter gSmartmeter = this.smartMeterRepository.findByDeviceIdentification(gmeter);
            final Device eDevice = this.deviceRepository.findByDeviceIdentification(emeter);

            Assert.assertNotNull(eDevice);
            Assert.assertNotNull(gSmartmeter);

            Assert.assertEquals(gSmartmeter.getGatewayDevice(), eDevice);
            Assert.assertEquals(gSmartmeter.getChannel(), channel);
        });
    }

    @Then("^the mbus device \"([^\"]*)\" is not coupled to the device \"([^\"]*)\"$")
    public void theMbusDeviceIsNotCoupledToTheDevice(final String gmeter, final String emeter) {
        Wait.until(() -> {
            final SmartMeter gSmartmeter = this.smartMeterRepository.findByDeviceIdentification(gmeter);
            final Device eDevice = this.deviceRepository.findByDeviceIdentification(emeter);

            Assert.assertNotNull(eDevice);
            Assert.assertNotNull(gSmartmeter);

            Assert.assertNotEquals(gSmartmeter.getGatewayDevice(), eDevice);
        });
    }

    @Then("^the device contains$")
    public void theDeviceContains(final Map<String, String> expectedEntity) {
        Wait.until(() -> {
            final Device device = this.deviceRepository
                    .findByDeviceIdentification(getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

            Assert.assertEquals(getString(expectedEntity, PlatformKeys.IP_ADDRESS), device.getIpAddress());
        });
    }

}
