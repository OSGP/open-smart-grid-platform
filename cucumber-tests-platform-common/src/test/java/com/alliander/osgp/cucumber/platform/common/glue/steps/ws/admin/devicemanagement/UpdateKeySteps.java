/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getLong;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.UpdateKeyResponse;
import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonDefaults;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class UpdateKeySteps extends GlueBase {

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
        request.setDeviceIdentification(getString(requestSettings, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setPublicKey(getString(requestSettings, PlatformCommonKeys.KEY_PUBLIC_KEY,
                PlatformCommonDefaults.DEFAULT_PUBLIC_KEY));
        request.setProtocolInfoId(getLong(requestSettings, PlatformCommonKeys.KEY_PROTOCOL_INFO_ID,
                PlatformCommonDefaults.DEFAULT_PROTOCOL_INFO_ID));

        try {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.getUpdateKeyResponse(request));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, e);
        }
    }

    /**
     * Verify that the update key response is successful.
     *
     * @throws Throwable
     */
    @Then("^the update key response contains$")
    public void the_update_key_response_contains(final Map<String, String> expectedResult) throws Throwable {
        // TODO: Check what the "Update Key Response" has to return, for now
        // there is no information to check.
        final UpdateKeyResponse response = (UpdateKeyResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);
        Assert.assertNotNull(response);
    }

    /**
     * Verify that the update key response is failed.
     *
     * @throws Throwable
     */
    @Then("^the update key response contains soap fault$")
    public void the_update_key_response_contains_soap_fault(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}