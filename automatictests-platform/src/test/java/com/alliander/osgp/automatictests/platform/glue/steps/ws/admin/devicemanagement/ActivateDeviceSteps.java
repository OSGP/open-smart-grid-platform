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
<<<<<<< HEAD:automatictests-platform/src/test/java/com/alliander/osgp/automatictests/platform/glue/steps/ws/admin/devicemanagement/ActivateDeviceSteps.java
import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.StepsBase;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.automatictests.platform.support.ws.admin.AdminDeviceManagementClient;
=======
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.common.ResponseSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.GenericResponseSteps;
import com.alliander.osgp.platform.cucumber.support.ws.admin.AdminDeviceManagementClient;
>>>>>>> 3ccf56a85cff1219f2d93ef91f86a3dd8e3e9de3:cucumber-tests-platform/src/test/java/com/alliander/osgp/platform/cucumber/steps/ws/admin/devicemanagement/ActivateDeviceSteps.java

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
