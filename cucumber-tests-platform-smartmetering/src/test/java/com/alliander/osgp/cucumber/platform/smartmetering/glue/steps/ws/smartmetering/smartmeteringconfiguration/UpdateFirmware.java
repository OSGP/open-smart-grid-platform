/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.UpdateFirmwareRequestFactory;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.FirmwareFile;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@Transactional(value = "txMgrCore")
public class UpdateFirmware extends SmartMeteringStepsBase {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SmartMeteringConfigurationClient client;

    @When("^the request for a firmware upgrade is received$")
    public void theRequestForAFirmwareUpgradeIsReceived(final Map<String, String> settings) throws Throwable {

        final UpdateFirmwareRequest request = UpdateFirmwareRequestFactory.fromParameterMap(settings);
        final UpdateFirmwareAsyncResponse asyncResponse = this.client.updateFirmware(request);

        assertNotNull("asyncResponse should not be null", asyncResponse);
        Helpers.saveAsyncResponse(asyncResponse);
    }

    @Then("^retrieving the update firmware response results in an exception$")
    public void retrievingTheUpdateFirmwareResponseResultsInAnException() throws Throwable {

        final UpdateFirmwareAsyncRequest asyncRequest = UpdateFirmwareRequestFactory.fromScenarioContext();

        try {
            this.client.getUpdateFirmwareResponse(asyncRequest);
            fail("A SoapFaultClientException should be thrown");
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }
    }

    @Then("^the update firmware result should be returned$")
    public void theUpdateFirmwareResultShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final UpdateFirmwareAsyncRequest asyncRequest = UpdateFirmwareRequestFactory.fromParameterMapAsync(settings);
        final UpdateFirmwareResponse response = this.client.getUpdateFirmwareResponse(asyncRequest);

        assertEquals("result", OsgpResultType.OK, response.getResult());

        final List<FirmwareVersion> expectedFirmareVersions = UpdateFirmwareRequestFactory
                .firmwareVersionsFromParameters(settings);

        final List<FirmwareVersion> actualFirmwareVersions = response.getFirmwareVersion();

        assertEquals("number of firmware versions", expectedFirmareVersions.size(), actualFirmwareVersions.size());

        for (final FirmwareVersion expected : expectedFirmareVersions) {
            assertTrue(
                    "Firmware version not returned: " + expected.getFirmwareModuleType() + " => "
                            + expected.getVersion(),
                    this.firmwareVersionListContains(actualFirmwareVersions, expected));
        }
    }

    private boolean firmwareVersionListContains(final List<FirmwareVersion> firmwareVersions,
            final FirmwareVersion expectedFirmwareVersion) {
        for (final FirmwareVersion firmwareVersion : firmwareVersions) {
            if (this.firmwareVersionEquals(firmwareVersion, expectedFirmwareVersion)) {
                return true;
            }
        }
        return false;
    }

    private boolean firmwareVersionEquals(final FirmwareVersion a, final FirmwareVersion b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.getFirmwareModuleType().equals(b.getFirmwareModuleType()) && a.getVersion().equals(b.getVersion());
    }

    @Then("^the database should be updated with the new device firmware$")
    public void theDatabaseShouldBeUpdatedWithTheNewDeviceFirmware(final Map<String, String> settings)
            throws Throwable {

        final String deviceIdentification = Helpers.getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_SMART_METER_DEVICE_IDENTIFICATION);
        final Device device = this.deviceRepository.findByDeviceIdentificationWithFirmware(deviceIdentification);
        assertNotNull("Device " + deviceIdentification + " not found.", device);

        final FirmwareFile activeFirmware = device.getActiveFirmwareFile();
        assertNotNull("No active firmware found for device " + deviceIdentification + ".", activeFirmware);

        final String moduleVersionComm = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM);
        final String moduleVersionMa = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_MA);
        final String moduleVersionFunc = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC);
        assertEquals(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM, moduleVersionComm,
                activeFirmware.getModuleVersionComm());
        assertEquals(PlatformKeys.FIRMWARE_MODULE_VERSION_MA, moduleVersionMa, activeFirmware.getModuleVersionMa());
        assertEquals(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC, moduleVersionFunc,
                activeFirmware.getModuleVersionFunc());
    }

    @Then("^the database should not be updated with the new device firmware$")
    public void theDatabaseShouldNotBeUpdatedWithTheNewDeviceFirmware(final Map<String, String> settings)
            throws Throwable {

        final String deviceIdentification = Helpers.getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_SMART_METER_DEVICE_IDENTIFICATION);
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        assertNotNull("Device " + deviceIdentification + " not found.", device);

        final FirmwareFile activeFirmwareFile = device.getActiveFirmwareFile();
        if (activeFirmwareFile == null) {
            /*
             * The device has no active firmware in the database, so the
             * firmware from the settings has not been linked to the device.
             */
            return;
        }

        final String moduleVersionComm = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_COMM);
        final String moduleVersionMa = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_MA);
        final String moduleVersionFunc = settings.get(PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC);

        assertFalse(
                "Device " + deviceIdentification
                        + " should not have firmware versions from the scenario after an unsuccessful update.",
                Objects.equals(moduleVersionComm, activeFirmwareFile.getModuleVersionComm())
                        && Objects.equals(moduleVersionMa, activeFirmwareFile.getModuleVersionMa())
                        && Objects.equals(moduleVersionFunc, activeFirmwareFile.getModuleVersionFunc()));
    }
}
