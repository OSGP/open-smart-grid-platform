/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RemoveDeviceSteps extends GlueBase {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send a remove device request to the Platform.
     *
     * @param requestParameters
     *            An list with request parameters for the request.
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws WebServiceSecurityException
     */
    @When("^receiving a remove device request$")
    public void receivingARemoveDeviceRequest(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final RemoveDeviceRequest request = new RemoveDeviceRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.removeDevice(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, ex);
            GenericResponseSteps.verifySoapFault(requestParameters);
        }
    }

    /**
     * Send a remove device request to the Platform.
     *
     * @param requestParameters
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a remove device request with unknown device identification$")
    public void receivingARemoveDeviceRequestWithUnknownDeviceIdentification(
            final Map<String, String> requestParameters) throws Throwable {
        ScenarioContext.current().put(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

        this.receivingARemoveDeviceRequest(requestParameters);
    }

    /**
     * Send a remove device request to the Platform.
     *
     * @param requestParameters
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a remove device request with empty device identification$")
    public void receivingARemoveDeviceRequestWithEmptyDeviceIdentification(final Map<String, String> requestParameters)
            throws Throwable {
        this.receivingARemoveDeviceRequest(requestParameters);
    }

    /**
     * The check for the response from the Platform.
     */
    @Then("^the remove device response is successful$")
    public void theRemoveDeviceResponseIsSuccessful() throws Throwable {
        Assert.assertTrue(ScenarioContext.current().get(PlatformCommonKeys.RESPONSE) instanceof RemoveDeviceResponse);
    }

    /**
     * The check for the response from the Platform.
     */
    @Then("^the remove device response contains soap fault$")
    public void theRemoveDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}