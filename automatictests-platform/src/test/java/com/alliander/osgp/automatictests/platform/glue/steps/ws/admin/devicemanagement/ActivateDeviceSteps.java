/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getEnum;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateDeviceResponse;
import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.StepsBase;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.automatictests.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.automatictests.platform.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the activate device steps.
 */
public class ActivateDeviceSteps extends StepsBase {

    @Autowired
    private AdminDeviceManagementClient client;

    @When("^receiving an activate device request$")
    public void receivingAnActivateDeviceRequest(final Map<String, String> requestSettings) throws Throwable {

        ActivateDeviceRequest request = new ActivateDeviceRequest();
        request.setDeviceIdentification(
                getString(requestSettings, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, client.activateDevice(request));
        } catch (SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }
    
    @When("^receiving an activate device request as an unauthorized organization$")
    public void receivingAnActivateDeviceRequestAsAnUnauthorizedOrganization(final Map<String, String> requestSettings) throws Throwable {
        
        // Force WSTF to use a different organization to send the requests with.
        // (Cerificate is used from the certificates directory).
        ScenarioContext.Current().put(Keys.ORGANIZATION_IDENTIFICATION, "unknown-organization");

        this.receivingAnActivateDeviceRequest(requestSettings);
    }

    /**
     * Verify that the activate device response is successful.
     * 
     * @throws Throwable
     */
    @Then("^the activate device response contains$")
    public void theActivateDeviceResponseContains(final Map<String, String> expectedResponse) throws Throwable {
        ActivateDeviceResponse response = (ActivateDeviceResponse) ScenarioContext.Current().get(Keys.RESPONSE);

        Assert.assertEquals(response.getResult(),
                getEnum(expectedResponse, Keys.RESULT, OsgpResultType.class, OsgpResultType.OK));
    }

    @Then("^the activate device response return a soap fault$")
    public void theActivateDeviceResponseReturnsASoapFault(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
