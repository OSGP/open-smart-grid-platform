/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getFloat;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.AddDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.AddDeviceResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.DeviceModel;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.UpdateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.UpdateDeviceResponse;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.database.adapterprotocoloslp.OslpDeviceSteps;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreDeviceInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class CreateDeviceSteps extends GlueBase {

    @Autowired
    private CoreDeviceInstallationClient client;

    @When("^receiving an add device request$")
    public void receivingAnAddDeviceRequest(final Map<String, String> settings) throws Throwable {
        final AddDeviceRequest request = new AddDeviceRequest();
        final Device device = this.createDevice(settings);
        request.setDevice(device);

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.addDevice(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @When("^receiving an add device request with an unknown organization$")
    public void receivingAnAddDeviceRequestWithAnUnknownOrganization(final Map<String, String> settings)
            throws Throwable {
        ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");
        this.receivingAnAddDeviceRequest(settings);
    }

    /**
     * Verify the response of a add device request.
     *
     * @param settings
     * @throws Throwable
     */
    @Then("^the add device response is successful$")
    public void theAddDeviceResponseIsSuccessful() throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof AddDeviceResponse);
    }

    @When("^receiving an update device request")
    public void receivingAnUpdateDeviceRequest(final Map<String, String> settings) throws Throwable {
        final UpdateDeviceRequest request = new UpdateDeviceRequest();

        String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION,
                Defaults.DEFAULT_DEVICE_IDENTIFICATION);
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
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.updateDevice(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    private Device createDevice(final Map<String, String> settings) {

        final Device device = new Device();
        device.setAlias(getString(settings, Keys.KEY_ALIAS, Defaults.DEFAULT_ALIAS));
        device.setContainerCity(getString(settings, Keys.KEY_CITY, Defaults.DEFAULT_CONTAINER_CITY));
        device.setContainerMunicipality(
                getString(settings, Keys.KEY_MUNICIPALITY, Defaults.DEFAULT_CONTAINER_MUNICIPALITY));
        device.setContainerNumber(getString(settings, Keys.KEY_NUMBER, Defaults.DEFAULT_CONTAINER_NUMBER));
        device.setContainerPostalCode(getString(settings, Keys.KEY_POSTCODE, Defaults.DEFAULT_CONTAINER_POSTALCODE));
        device.setContainerStreet(getString(settings, Keys.KEY_STREET, Defaults.DEFAULT_CONTAINER_STREET));
        device.setDeviceIdentification(
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        final DeviceModel deviceModel = new DeviceModel();
        deviceModel.setDescription(
                getString(settings, Keys.KEY_DEVICE_MODEL_DESCRIPTION, Defaults.DEFAULT_DEVICE_MODEL_DESCRIPTION));
        deviceModel.setManufacturer(
                getString(settings, Keys.KEY_DEVICE_MODEL_MANUFACTURER, Defaults.DEFAULT_DEVICE_MODEL_MANUFACTURER));
        deviceModel
                .setMetered(getBoolean(settings, Keys.KEY_DEVICE_MODEL_METERED, Defaults.DEFAULT_DEVICE_MODEL_METERED));
        deviceModel.setModelCode(
                getString(settings, Keys.KEY_DEVICE_MODEL_MODELCODE, Defaults.DEFAULT_DEVICE_MODEL_MODEL_CODE));
        device.setDeviceModel(deviceModel);
        device.setDeviceUid(getString(settings, Keys.KEY_DEVICE_UID, OslpDeviceSteps.DEFAULT_DEVICE_UID));
        device.setGpsLatitude(getFloat(settings, Keys.KEY_LATITUDE, Defaults.DEFAULT_LATITUDE));
        device.setGpsLongitude(getFloat(settings, Keys.KEY_LONGITUDE, Defaults.DEFAULT_LONGITUDE));
        device.setHasSchedule(getBoolean(settings, Keys.KEY_HAS_SCHEDULE, Defaults.DEFAULT_HASSCHEDULE));
        device.setOwner(getString(settings, Keys.KEY_OWNER, Defaults.DEFAULT_OWNER));
        device.setPublicKeyPresent(getBoolean(settings, Keys.KEY_PUBLICKEYPRESENT, Defaults.DEFAULT_PUBLICKEYPRESENT));
        device.setActivated(getBoolean(settings, Keys.KEY_ACTIVATED, Defaults.DEFAULT_ACTIVATED));

        return device;
    }

    /**
     * Verify the response of an update device request.
     *
     * @param settings
     * @throws Throwable
     */
    @Then("^the update device response is successfull$")
    public void theUpdateDeviceResponseIsSuccessfull() throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof UpdateDeviceResponse);
    }

    /**
     * Verify that the create organization response contains the fault with the
     * given expectedResult parameters.
     *
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the add device response contains$")
    public void theAddDeviceResponseContains(final Map<String, String> expectedResult) throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof AddDeviceResponse);
    }

    @Then("^the add device response contains soap fault$")
    public void theAddDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }

    @Then("^the update device response contains$")
    public void theUpdateDeviceResponseContains(final Map<String, String> expectedResult) throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof UpdateDeviceResponse);
    }

    /**
     * Verify that the update device response contains the fault with the given
     * expectedResult parameters.
     *
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the update device response contains soap fault$")
    public void theUpdateDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
