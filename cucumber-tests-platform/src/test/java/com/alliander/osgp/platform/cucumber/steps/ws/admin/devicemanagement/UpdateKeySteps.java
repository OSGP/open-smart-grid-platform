/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getLong;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class UpdateKeySteps {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send an update key request to the Platform.
     *
     * @param requestParameter
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving an update key request$")
    public void receiving_an_update_key_request(final Map<String, String> requestSettings) throws Throwable {

        // TODO: Change to Update Key
        final UpdateKeyRequest request = new UpdateKeyRequest();
        request.setDeviceIdentification(
                getString(requestSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setPublicKey(getString(requestSettings, Keys.KEY_PUBLIC_KEY, Defaults.DEFAULT_PUBLIC_KEY));
        request.setProtocolInfoId(
                getLong(requestSettings, Keys.KEY_PROTOCOL_INFO_ID, Defaults.DEFAULT_PROTOCOL_INFO_ID));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.getUpdateKeyResponse(request));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.Current().put(Keys.RESPONSE, e);
        }
    }

    /**
     * Verify that the update key response is successful.
     *
     * @throws Throwable
     */
    @Then("^the update key response contains$")
    public void the_update_key_response_contains(final Map<String, String> expectedResult) throws Throwable {
        final Object obj = ScenarioContext.Current().get(Keys.RESPONSE);
        Assert.assertTrue(obj instanceof UpdateKeyResponse);
        Assert.assertNotNull(obj);
    }

    /**
     * Verify that the update key response is failed.
     *
     * @throws Throwable
     */
    @Then("^the update key response contains soap fault$")
    public void the_update_key_response_contains_soap_fault(final Map<String, String> expectedResult) throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof SoapFaultClientException);
        final SoapFaultClientException response = (SoapFaultClientException) ScenarioContext.Current()
                .get(Keys.RESPONSE);
        Assert.assertEquals(getString(expectedResult, Keys.KEY_MESSAGE), response.getMessage());
    }
}