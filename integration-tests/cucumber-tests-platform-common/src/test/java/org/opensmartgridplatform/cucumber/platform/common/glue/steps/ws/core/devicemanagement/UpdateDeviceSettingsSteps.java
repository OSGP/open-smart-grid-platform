/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.junit.Assert;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceModel;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Manufacturer;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceResponse;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class UpdateDeviceSettingsSteps extends GlueBase {

    @Autowired
    private CoreDeviceManagementClient client;

    @When("^receiving a device management update device request")
    public void receivingAnUpdateDeviceRequest(final Map<String, String> settings) {
        final UpdateDeviceRequest request = new UpdateDeviceRequest();

        String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION);
        // Note: The regular expression below matches at spaces between two
        // quotation marks("), this check is used for a test with a
        // DeviceIdentification with only spaces
        if (deviceIdentification.matches("(?!\")\\s*(?=\")")) {
            deviceIdentification = deviceIdentification.replaceAll("\"", " ");
        }
        request.setDeviceIdentification(deviceIdentification);
        final Device device = this.createDevice(settings);
        request.setUpdatedDevice(device);

        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.updateDevice(request));
        } catch (final SoapFaultClientException | WebServiceSecurityException | GeneralSecurityException
                | IOException ex) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
        }
    }

    private Device createDevice(final Map<String, String> settings) {

        final Device device = new Device();
        device.setAlias(getString(settings, PlatformKeys.KEY_ALIAS, PlatformCommonDefaults.DEFAULT_ALIAS));
        device.setContainerCity(
                getString(settings, PlatformKeys.KEY_CITY, PlatformCommonDefaults.DEFAULT_CONTAINER_CITY));
        device.setContainerMunicipality(getString(settings, PlatformKeys.KEY_MUNICIPALITY,
                PlatformCommonDefaults.DEFAULT_CONTAINER_MUNICIPALITY));
        device.setContainerNumber(
                getString(settings, PlatformKeys.KEY_NUMBER, PlatformCommonDefaults.DEFAULT_CONTAINER_NUMBER));
        device.setContainerPostalCode(
                getString(settings, PlatformKeys.KEY_POSTCODE, PlatformCommonDefaults.DEFAULT_CONTAINER_POSTALCODE));
        device.setContainerStreet(
                getString(settings, PlatformKeys.KEY_STREET, PlatformCommonDefaults.DEFAULT_CONTAINER_STREET));
        device.setDeviceIdentification(getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        final DeviceModel deviceModel = new DeviceModel();
        deviceModel.setDescription(getString(settings, PlatformKeys.KEY_DEVICE_MODEL_DESCRIPTION,
                PlatformCommonDefaults.DEFAULT_DEVICE_MODEL_DESCRIPTION));

        final Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(
                getString(settings, PlatformKeys.MANUFACTURER_NAME, PlatformDefaults.DEFAULT_MANUFACTURER_NAME));
        manufacturer.setManufacturerId(getString(settings, PlatformKeys.KEY_DEVICE_MODEL_MANUFACTURER,
                PlatformCommonDefaults.DEFAULT_DEVICE_MODEL_MANUFACTURER));
        manufacturer.setUsePrefix(
                getBoolean(settings, PlatformKeys.USE_PREFIX, PlatformDefaults.DEFAULT_MANUFACTURER_USE_PREFIX));
        deviceModel.setManufacturer(manufacturer);

        deviceModel.setMetered(getBoolean(settings, PlatformKeys.KEY_DEVICE_MODEL_METERED,
                PlatformCommonDefaults.DEFAULT_DEVICE_MODEL_METERED));
        deviceModel.setModelCode(getString(settings, PlatformKeys.KEY_DEVICE_MODEL_MODELCODE,
                PlatformCommonDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));
        device.setDeviceModel(deviceModel);
        device.setDeviceUid(getString(settings, PlatformKeys.KEY_DEVICE_UID, PlatformCommonDefaults.DEVICE_UID));
        device.setGpsLatitude(
                getString(settings, PlatformKeys.KEY_LATITUDE, PlatformCommonDefaults.DEFAULT_LATITUDE_STRING));
        device.setGpsLongitude(
                getString(settings, PlatformKeys.KEY_LONGITUDE, PlatformCommonDefaults.DEFAULT_LONGITUDE_STRING));
        device.setHasSchedule(
                getBoolean(settings, PlatformKeys.KEY_HAS_SCHEDULE, PlatformCommonDefaults.DEFAULT_HASSCHEDULE));
        device.setOwner(getString(settings, PlatformKeys.KEY_OWNER, PlatformCommonDefaults.DEFAULT_OWNER));
        device.setPublicKeyPresent(getBoolean(settings, PlatformKeys.KEY_PUBLICKEYPRESENT,
                PlatformCommonDefaults.DEFAULT_PUBLICKEYPRESENT));
        device.setActivated(getBoolean(settings, PlatformKeys.KEY_ACTIVATED, PlatformCommonDefaults.DEFAULT_ACTIVATED));

        return device;
    }

    @Then("^the device management update device response is successful$")
    public void theUpdateDeviceResponseIsSuccessfull() {
        Assert.assertTrue(ScenarioContext.current().get(PlatformKeys.RESPONSE) instanceof UpdateDeviceResponse);
    }

    @Then("^the device management update device response contains soap fault$")
    public void theUpdateDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
