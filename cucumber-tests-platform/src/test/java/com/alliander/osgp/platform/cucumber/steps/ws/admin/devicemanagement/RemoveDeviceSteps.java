/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RemoveDeviceSteps {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send a remove device request to the Platform.
     *
     * @param requestParameters
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a remove device request$")
<<<<<<< HEAD
    public void receiving_a_remove_device_request(final Map<String, String> requestSettings) throws Throwable {
        final RemoveDeviceRequest request = new RemoveDeviceRequest();
        request.setDeviceIdentification(
                getString(requestSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

=======
    public void receivingARemoveDeviceRequest(final Map<String, String> requestSettings) throws Throwable
    {
    	RemoveDeviceRequest request = new RemoveDeviceRequest();
        request.setDeviceIdentification(getString(requestSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    	
>>>>>>> e9ae808eaaf0b2e324bd083ec79f70f573c72434
        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.removeDevice(request));
        } catch (final SoapFaultClientException ex) {
            LoggerFactory.getLogger(RemoveDeviceSteps.class).info("Response: " + ex);
            // ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @throws Throwable
     */
<<<<<<< HEAD
    @Then("^the remove device response is successfull$")
    public void the_remove_device_response_is_successfull() throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof RemoveDeviceResponse);
=======
    @Then("^the remove device response is successful$")
    public void theRemoveDeviceResponseIsSuccessful() throws Throwable
    {
    	Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof RemoveDeviceResponse);
>>>>>>> e9ae808eaaf0b2e324bd083ec79f70f573c72434
    }

}