/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.ws.admin.devicemanagement;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getEnum;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.CreateOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Organisation;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformDomain;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.automatictests.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.automatictests.platform.support.ws.admin.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class CreateOrganizationSteps {

    @Autowired
    private AdminDeviceManagementClient client;

    /**
     *
     * @throws Throwable
     */
    @When("^receiving a create organization request$")
    public void receivingACreateOrganizationRequest(final Map<String, String> requestSettings) throws Throwable {

        final CreateOrganisationRequest request = new CreateOrganisationRequest();
        final Organisation organization = new Organisation();

        // Required fields
        organization.setName(getString(requestSettings, Keys.NAME, Defaults.ORGANIZATION_NAME));
        organization.setOrganisationIdentification(getString(requestSettings, Keys.ORGANIZATION_IDENTIFICATION,
                Defaults.ORGANIZATION_IDENTIFICATION));
        organization.setPrefix(getString(requestSettings, Keys.PREFIX, Defaults.ORGANIZATION_PREFIX));

        final PlatformFunctionGroup platformFunctionGroup = getEnum(requestSettings, Keys.PLATFORM_FUNCTION_GROUP,
                PlatformFunctionGroup.class, Defaults.NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP);
        organization.setFunctionGroup(platformFunctionGroup);

        for (final String domain : getString(requestSettings, Keys.DOMAINS, Defaults.DOMAINS).split(";")) {
            organization.getDomains().add(Enum.valueOf(PlatformDomain.class, domain));
        }

        // Optional fields
        if (requestSettings.containsKey(Keys.ENABLED) && !requestSettings.get(Keys.ENABLED).isEmpty()) {
            organization.setEnabled(getBoolean(requestSettings, Keys.ENABLED));
        }

        request.setOrganisation(organization);

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.createOrganization(request));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.Current().put(Keys.RESPONSE, e);
        }
    }

    /**
     *
     * @throws Throwable
     */
    @When("^receiving a create organization request as an unauthorized organization$")
    public void receivingACreateOrganizationRequestAsAnUnauthorizedOrganization(
            final Map<String, String> requestSettings) throws Throwable {

        // Force WSTF to use a different organization to send the requests with.
        // (Cerificate is used from the certificates directory).
        ScenarioContext.Current().put(Keys.ORGANIZATION_IDENTIFICATION, "unknown-organization");

        this.receivingACreateOrganizationRequest(requestSettings);
    }

    /**
     * Verify that the create organization response is successful.
     *
     * @throws Throwable
     */
    @Then("^the create organization response is successful$")
    public void theCreateOrganizationResponseIsSuccessful() throws Throwable {
        Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof CreateOrganisationResponse);
    }

    /**
     * Verify that the create organization response contains the fault with the
     * given expectedResult parameters.
     *
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the create organization response contains soap fault$")
    public void theCreateOrganizationResponseContainsSoapFault(final Map<String, String> expectedResult)
            throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
