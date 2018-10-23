/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RevokeKeyResponse;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class RevokeKeySteps extends GlueBase {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send a revoke key request to the Platform.
     *
     * @param requestSettings
     *            A map of request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a revoke key request$")
    public void receiving_a_revoke_key_request(final Map<String, String> requestSettings) throws Throwable {

        // TODO: Change to Revoke Key
        final RevokeKeyRequest request = new RevokeKeyRequest();
        request.setDeviceIdentification(getString(requestSettings, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.getRevokeKeyResponse(request));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, e);
        }
    }

    /**
     * Verify that the revoke key response is successful.
     *
     * @throws Throwable
     */
    @Then("^the revoke key response contains$")
    public void the_revoke_key_response_contains(final Map<String, String> expectedResult) throws Throwable {
        // TODO: Check what the "Revoke Key Response" has to return, for now
        // there is no information to check.
        final RevokeKeyResponse response = (RevokeKeyResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);
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

        Assert.assertTrue(
                ScenarioContext.current().get(PlatformCommonKeys.RESPONSE) instanceof SoapFaultClientException);
        final SoapFaultClientException response = (SoapFaultClientException) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);
        Assert.assertEquals(getString(expectedResult, PlatformCommonKeys.KEY_MESSAGE), response.getMessage());
    }
}