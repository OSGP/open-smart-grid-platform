/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.beans.factory.annotation.Autowired;

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
    public void theChannelOfDeviceIsCleared(final String gMeter) {
        final SmartMeter mbusDevice = this.smartMeterRepository.findByDeviceIdentification(gMeter);

        Assert.assertNotNull("No MbusDevice found", mbusDevice);

        Assert.assertNull("GatewayDevice must be empty", mbusDevice.getChannel());
    }

    /**
     * Verify that the device exists in the database.
     */
    @Then("^the device exists")
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

        if (settings.containsKey(PlatformKeys.ALIAS)) {
            Assert.assertEquals(settings.get(PlatformKeys.ALIAS), device.getAlias());
        }
        if (settings.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
            Assert.assertEquals(settings.get(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION),
                    device.getOwner().getOrganisationIdentification());
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_POSTALCODE)) {
            Assert.assertEquals(settings.get(PlatformKeys.CONTAINER_POSTALCODE), device.getContainerAddress().getPostalCode());
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_CITY)) {
            Assert.assertEquals(settings.get(PlatformKeys.CONTAINER_CITY), device.getContainerAddress().getCity());
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_STREET)) {
            Assert.assertEquals(settings.get(PlatformKeys.CONTAINER_STREET), device.getContainerAddress().getStreet());
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_NUMBER)) {
            Assert.assertEquals(settings.get(PlatformKeys.CONTAINER_NUMBER), device.getContainerAddress().getNumber());
        }
        if (settings.containsKey(PlatformKeys.CONTAINER_MUNICIPALITY)) {
            Assert.assertEquals(settings.get(PlatformKeys.CONTAINER_MUNICIPALITY),
                    device.getContainerAddress().getMunicipality());
        }
        if (settings.containsKey(PlatformKeys.KEY_LATITUDE)) {
            Assert.assertTrue(Float.parseFloat(settings.get(PlatformKeys.KEY_LATITUDE)) == device.getGpsCoordinates()
                    .getLatitude());
        }
        if (settings.containsKey(PlatformKeys.KEY_LONGITUDE)) {
            Assert.assertTrue(Float.parseFloat(settings.get(PlatformKeys.KEY_LONGITUDE)) == device.getGpsCoordinates()
                    .getLongitude());
        }
        if (settings.containsKey(PlatformKeys.KEY_ACTIVATED)) {
            Assert.assertTrue(Boolean.parseBoolean(settings.get(PlatformKeys.KEY_ACTIVATED)) == device.isActivated());
        }
        if (settings.containsKey(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS)) {
            Assert.assertTrue(DeviceLifecycleStatus.valueOf(
                    settings.get(PlatformKeys.KEY_DEVICE_LIFECYCLE_STATUS)) == device.getDeviceLifecycleStatus());
        }
        if (settings.containsKey(PlatformKeys.KEY_HAS_SCHEDULE)
                || settings.containsKey(PlatformKeys.KEY_PUBLICKEYPRESENT)) {
            final Ssld ssld = this.ssldRepository
                    .findByDeviceIdentification(settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

            if (settings.containsKey(PlatformKeys.KEY_HAS_SCHEDULE)) {
                Assert.assertTrue(
                        Boolean.parseBoolean(settings.get(PlatformKeys.KEY_HAS_SCHEDULE)) == ssld.getHasSchedule());
            }
            if (settings.containsKey(PlatformKeys.KEY_PUBLICKEYPRESENT)) {
                Assert.assertTrue(Boolean.parseBoolean(settings.get(PlatformKeys.KEY_PUBLICKEYPRESENT)) == ssld
                        .isPublicKeyPresent());
            }
        }
        if (settings.containsKey(PlatformKeys.KEY_DEVICE_MODEL_MODELCODE)) {
            Assert.assertEquals(settings.get(PlatformKeys.KEY_DEVICE_MODEL_MODELCODE),
                    device.getDeviceModel().getModelCode());
        }
    }

    /**
     * Checks whether the device does not exist in the database.
     */
    @Then("^the device with id \"([^\"]*)\" should be removed$")
    public void theDeviceShouldBeRemoved(final String deviceIdentification) throws Throwable {
        Wait.until(() -> {
            final Device entity = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            Assert.assertNull("Device with identification [" + deviceIdentification + "] should be removed", entity);

            final List<DeviceAuthorization> devAuths = this.deviceAuthorizationRepository.findByDevice(entity);
            Assert.assertTrue("DeviceAuthorizations for device with identification [" + deviceIdentification
                    + "] should be removed", devAuths.isEmpty());
        });
    }

    /**
     * Checks whether the device does not exist in the database.
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

            Assert.assertTrue("Entity is inactive",
                    entity.getDeviceLifecycleStatus().equals(DeviceLifecycleStatus.IN_USE));
        });
    }

    @Then("^the device with device identification \"([^\"]*)\" should be inactive$")
    public void theDeviceWithDeviceIdentificationShouldBeInActive(final String deviceIdentification) throws Throwable {
        Wait.until(() -> {
            final Device entity = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            Assert.assertNotNull("Device with identification [" + deviceIdentification + "]", entity);

            Assert.assertFalse("Entity is active",
                    entity.getDeviceLifecycleStatus().equals(DeviceLifecycleStatus.IN_USE));
        });
    }

    /**
     * Checks whether the device exists in the database..
     */
    @Then("^the device with id \"([^\"]*)\" exists$")
    public void theDeviceWithIdExists(final String deviceIdentification) throws Throwable {
        Wait.until(() -> {
            final Device entity = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
            Assert.assertNotNull("Device with identification [" + deviceIdentification + "]", entity);

            final List<DeviceAuthorization> devAuths = this.deviceAuthorizationRepository.findByDevice(entity);

            Assert.assertNotNull("No entity found", entity);
            Assert.assertTrue("DeviceAuthorizations amount is not > 0", devAuths.size() > 0);
        });
    }

    @Then("^the G-meter \"([^\"]*)\" is DeCoupled from device \"([^\"]*)\"$")
    public void theGMeterIsDecoupledFromDevice(final String gMeter, final String eMeter) {
        Wait.until(() -> {
            final SmartMeter mbusDevice = this.smartMeterRepository.findByDeviceIdentification(gMeter);
            final Device gatewayDevice = this.deviceRepository.findByDeviceIdentification(eMeter);

            Assert.assertNotNull("No GatewayDevice found", gatewayDevice);
            Assert.assertNotNull("No MbusDevice found", mbusDevice);

            Assert.assertNull("GatewayDevice must be empty", mbusDevice.getGatewayDevice());
        });
    }

    @Then("^the M-Bus device \"([^\"]*)\" is coupled to device \"([^\"]*)\" on M-Bus channel \"([^\"]*)\" with PrimaryAddress \"([^\"]*)\"$")
    public void theMBusDeviceIsCoupledToDeviceOnMBusChannelWithPrimaryAddress(final String gMeter, final String eMeter,
            final Short channel, final Short primaryAddress) throws Throwable {
        Wait.until(() -> {
            final SmartMeter mbusDevice = this.smartMeterRepository.findByDeviceIdentification(gMeter);
            final Device gatewayDevice = this.deviceRepository.findByDeviceIdentification(eMeter);

            Assert.assertNotNull("No GatewayDevice found", gatewayDevice);
            Assert.assertNotNull("No MbusDevice found", mbusDevice);

            Assert.assertEquals("GatewayDevice does not match", gatewayDevice, mbusDevice.getGatewayDevice());
            Assert.assertEquals("Channel does not match", channel, mbusDevice.getChannel());
            Assert.assertEquals("PrimaryAddress does not match", primaryAddress, mbusDevice.getMbusPrimaryAddress());
        });
    }

    @Then("^the M-Bus device \"([^\"]*)\" is coupled to device \"([^\"]*)\" on M-Bus channel \"([^\"]*)\"$")
    public void theMBusDeviceIsCoupledToDeviceOnMBusChannel(final String gMeter, final String eMeter,
            final Short channel) throws Throwable {
        Wait.until(() -> {
            final SmartMeter mbusDevice = this.smartMeterRepository.findByDeviceIdentification(gMeter);
            final Device gatewayDevice = this.deviceRepository.findByDeviceIdentification(eMeter);

            Assert.assertNotNull("No GatewayDevice found", gatewayDevice);
            Assert.assertNotNull("No MbusDevice found", mbusDevice);

            Assert.assertEquals("GatewayDevice does not match", gatewayDevice, mbusDevice.getGatewayDevice());
            Assert.assertEquals("Channel does not match", channel, mbusDevice.getChannel());
        });
    }

    @Then("^the mbus device \"([^\"]*)\" is not coupled to the device \"([^\"]*)\"$")
    public void theMbusDeviceIsNotCoupledToTheDevice(final String gMeter, final String eMeter) {
        Wait.until(() -> {
            final SmartMeter mbusDevice = this.smartMeterRepository.findByDeviceIdentification(gMeter);
            final Device gatewayDevice = this.deviceRepository.findByDeviceIdentification(eMeter);

            Assert.assertNotNull("No GatewayDevice found", gatewayDevice);
            Assert.assertNotNull("No MbusDevice found", mbusDevice);

            Assert.assertNotEquals("MbusDevice should not be coupled to this GatewayDevice", gatewayDevice,
                    mbusDevice.getGatewayDevice());
        });
    }

    @Then("^the device contains$")
    public void theDeviceContains(final Map<String, String> expectedEntity) {
        Wait.until(() -> {
            final Device device = this.deviceRepository
                    .findByDeviceIdentification(getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

            Assert.assertEquals("IP address does not match", device.getIpAddress(),
                    getString(expectedEntity, PlatformKeys.IP_ADDRESS));
        });
    }

}
