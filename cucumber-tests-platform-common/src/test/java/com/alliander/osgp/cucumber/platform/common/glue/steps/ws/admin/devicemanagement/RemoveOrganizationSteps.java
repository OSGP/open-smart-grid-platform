/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationResponse;
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
public class RemoveOrganizationSteps extends GlueBase {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     * Send a remove organization request to the Platform.
     *
     * @param requestParameter
     *            An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a remove organization request$")
    public void receivingARemoveOrganizationRequest(final Map<String, String> requestSettings) throws Throwable {

        final RemoveOrganisationRequest request = new RemoveOrganisationRequest();
        request.setOrganisationIdentification(
                getString(requestSettings, PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        try {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.removeOrganization(request));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, e);
        }
    }

    /**
     * Verify that the create organization response is successful.
     *
     * @throws Throwable
     */
    @Then("^the remove organization response is successful$")
    public void theRemoveOrganizationResponseIsSuccessful() throws Throwable {
        Assert.assertTrue(
                ScenarioContext.current().get(PlatformCommonKeys.RESPONSE) instanceof RemoveOrganisationResponse);
    }

    /**
     * Verify the remove organization response
     *
     * @param arg1
     * @throws Throwable
     */
    @Then("^the remove organization response contains$")
    public void theRemoveOrganizationResponseContains(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
