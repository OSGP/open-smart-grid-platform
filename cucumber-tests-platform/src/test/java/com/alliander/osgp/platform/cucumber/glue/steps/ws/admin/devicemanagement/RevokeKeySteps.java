/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.glue.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RevokeKeyResponse;
import com.alliander.osgp.platform.cucumber.Defaults;
import com.alliander.osgp.platform.cucumber.Keys;
import com.alliander.osgp.platform.cucumber.StepsBase;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class RevokeKeySteps extends StepsBase {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send a revoke key request to the Platform.
     *
     * @param requestParameter
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a revoke key request$")
    public void receiving_a_revoke_key_request(final Map<String, String> requestSettings) throws Throwable {

        // TODO: Change to Revoke Key
        final RevokeKeyRequest request = new RevokeKeyRequest();
        request.setDeviceIdentification(
                getString(requestSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.KEY_RESPONSE, this.client.getRevokeKeyResponse(request));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.Current().put(Keys.KEY_RESPONSE, e);
        }
    }

    /**
     * Verify that the revoke key response is successful.
     *
     * @throws Throwable
     */
    @Then("^the revoke key response contains$")
    public void the_revoke_key_response_contains(final Map<String, String> expectedResult) throws Throwable {
        // TODO: Check what the "Revoke Key Response" has to return

        Assert.assertTrue(ScenarioContext.Current().get(Keys.KEY_RESPONSE) instanceof RevokeKeyResponse);
        final RevokeKeyResponse response = (RevokeKeyResponse) ScenarioContext.Current().get(Keys.KEY_RESPONSE);
        Assert.assertNotNull(response);
    }

    /**
     * Verify that the revoke key response is successful.
     *
     * @throws Throwable
     */
    @Then("^the revoke key response contains soap fault$")
    public void the_revoke_key_response_contains_soap_fault(final Map<String, String> expectedResult) throws Throwable {
        // TODO: Check what the "Revoke Key Response" has to return

        Assert.assertTrue(ScenarioContext.Current().get(Keys.KEY_RESPONSE) instanceof SoapFaultClientException);
        final SoapFaultClientException response = (SoapFaultClientException) ScenarioContext.Current()
                .get(Keys.KEY_RESPONSE);
        Assert.assertEquals(getString(expectedResult, Keys.KEY_MESSAGE), response.getMessage());
    }
}