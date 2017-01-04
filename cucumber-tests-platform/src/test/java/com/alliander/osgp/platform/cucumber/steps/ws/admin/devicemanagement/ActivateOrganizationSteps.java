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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the activate organization steps.
 */
public class ActivateOrganizationSteps {

    @Autowired
    private AdminDeviceManagementClient client;

    @When("^receiving an activate organization request$")
    public void receivingAnActivateOrganizationRequest(final Map<String, String> requestSettings) throws Throwable {

        ActivateOrganisationRequest request = new ActivateOrganisationRequest();
        request.setOrganisationIdentification(
                getString(requestSettings, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, client.activateOrganization(request));
        } catch (SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    /**
     * Verify that the activate organization response is successful.
     * 
     * @throws Throwable
     */
    @Then("^the activate organization response is successful$")
    public void theActivateOrganizationResponseIsSuccessful() throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof ActivateOrganisationResponse);
    }
}
