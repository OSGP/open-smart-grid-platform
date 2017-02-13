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

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformDomain;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class ChangeOrganizationSteps extends GlueBase {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send a update organization request to the Platform
     *
     * @param requestParameters
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving an update organization request$")
    public void receivingAnUpdateOrganizationRequest(final Map<String, String> requestSettings) throws Throwable {
        final ChangeOrganisationRequest request = new ChangeOrganisationRequest();

        request.setOrganisationIdentification(getString(requestSettings, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        request.setNewOrganisationName(
                getString(requestSettings, Keys.KEY_NAME, Defaults.DEFAULT_NEW_ORGANIZATION_NAME));

        request.setNewOrganisationIdentification(getString(requestSettings, Keys.KEY_NEW_ORGANIZATION_IDENTIFICATION,
                Defaults.DEFAULT_NEW_ORGANIZATION_IDENTIFICATION));

        request.setNewOrganisationPlatformFunctionGroup(
                getEnum(requestSettings, Keys.KEY_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP, PlatformFunctionGroup.class,
                        Defaults.DEFAULT_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP));

        request.getNewOrganisationPlatformDomains().clear();
        for (final String platformDomain : getString(requestSettings, Keys.KEY_DOMAINS, Defaults.DEFAULT_DOMAINS)
                .split(";")) {
            request.getNewOrganisationPlatformDomains().add(Enum.valueOf(PlatformDomain.class, platformDomain));
        }

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.changeOrganization(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @Then("^the update organization response is successful$")
    public void theUpdateOrganizationResponseIsSuccessful() throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof ChangeOrganisationResponse);
    }

    /**
     * Verify that the create organization response contains the fault with the
     * given expectedResult parameters.
     *
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the update organization response contains$")
    public void theUpdateOrganizationResponseContains(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
