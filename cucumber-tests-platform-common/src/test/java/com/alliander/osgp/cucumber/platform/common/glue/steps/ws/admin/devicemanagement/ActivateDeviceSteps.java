/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceResponse;
import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonDefaults;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the activate device steps.
 */
public class ActivateDeviceSteps extends GlueBase {

    @Autowired
    private AdminDeviceManagementClient client;

    @When("^receiving an activate device request$")
    public void receivingAnActivateDeviceRequest(final Map<String, String> requestSettings) throws Throwable {

        final ActivateDeviceRequest request = new ActivateDeviceRequest();
        request.setDeviceIdentification(getString(requestSettings, PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.activateDevice(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, ex);
        }
    }

    @When("^receiving an activate device request as an unauthorized organization$")
    public void receivingAnActivateDeviceRequestAsAnUnauthorizedOrganization(final Map<String, String> requestSettings)
            throws Throwable {

        // Force WSTF to use a different organization to send the requests with.
        // (Certificate is used from the certificates directory).
        ScenarioContext.current().put(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

        this.receivingAnActivateDeviceRequest(requestSettings);
    }

    /**
     * Verify that the activate device response is successful.
     *
     * @throws Throwable
     */
    @Then("^the activate device response contains$")
    public void theActivateDeviceResponseContains(final Map<String, String> expectedResponse) throws Throwable {
        final ActivateDeviceResponse response = (ActivateDeviceResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);

        Assert.assertEquals(response.getResult(),
                getEnum(expectedResponse, PlatformCommonKeys.KEY_RESULT, OsgpResultType.class, OsgpResultType.OK));

    }

    @Then("^the activate device response contains soap fault$")
    public void theActivateDeviceResponseContainsASoapFault(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
