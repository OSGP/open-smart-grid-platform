/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeactivateDeviceResponse;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DeactivateDeviceSteps extends GlueBase {

    @Autowired
    private AdminDeviceManagementClient client;

    @When("^receiving a deactivate device request$")
    public void receivingADeactivateDeviceRequest(final Map<String, String> requestSettings) throws Throwable {

        final DeactivateDeviceRequest request = new DeactivateDeviceRequest();
        request.setDeviceIdentification(
                getString(requestSettings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.deactivateDevice(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    /**
     * Verify that the deactivate device response is successful.
     *
     * @throws Throwable
     */
    @Then("^the deactivate device response contains$")
    public void theDeactivateDeviceResponseContains(final Map<String, String> expectedResponse) throws Throwable {
        final DeactivateDeviceResponse response = (DeactivateDeviceResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        Assert.assertEquals(getEnum(expectedResponse, Keys.KEY_RESULT, OsgpResultType.class), response.getResult());
    }

    /**
     * Verifies the soap fault.
     *
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the deactivate device response contains soap fault$")
    public void theDeactivateDeviceResponseContainsSoapFault(final Map<String, String> expectedResult)
            throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
