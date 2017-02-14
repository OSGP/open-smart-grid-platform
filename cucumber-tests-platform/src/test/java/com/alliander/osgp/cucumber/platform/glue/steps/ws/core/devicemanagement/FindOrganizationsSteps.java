/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Organisation;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreDeviceManagementClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class FindOrganizationsSteps {

    @Autowired
    private CoreDeviceManagementClient client;

    /**
     *
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws WebServiceSecurityException
     * @throws Throwable
     */
    @When("^receiving a get all organizations request$")
    public void receivingGetAllOrganizationsRequest(final Map<String, String> settings)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, getString(settings,
                Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        try {
            ScenarioContext.Current().put(Keys.RESPONSE,
                    this.client.findAllOrganizations(new FindAllOrganisationsRequest()));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.Current().put(Keys.RESPONSE, e);
        }
    }

    /**
     *
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws WebServiceSecurityException
     * @throws Throwable
     */
    @When("^receiving an own unknown organization request$")
    public void receivingAnOwnUnknownOrganizationRequest(final Map<String, String> settings)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, getString(settings,
                Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        try {
            ScenarioContext.Current().put(Keys.RESPONSE,
                    this.client.findAllOrganizations(new FindAllOrganisationsRequest()));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.Current().put(Keys.RESPONSE, e);
        }
    }

    /**
     *
     * @throws Throwable
     */
    @Then("^the get all organizations response contains \"([^\"]*)\" organizations?$")
    public void theGetAllOrganizationsResponseContainsOrganization(final Integer expectedCount) {
        final FindAllOrganisationsResponse response = ((FindAllOrganisationsResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE));
        Assert.assertEquals((int) expectedCount, response.getOrganisations().size());
    }

    /**
     *
     * @throws Throwable
     */
    @Then("^the get own unknown organization response contains soap fault$")
    public void theGetOwnUnknownOrganizationResponseContainsSoapFault(final Map<String, String> expectedResult) {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }

    /**
     *
     * @throws Throwable
     */
    @Then("^the get all organizations response contains at index \"([^\"]*)\"$")
    public void theGetAllOrganizationsResponseContainsAtIndex(final Integer expectedIndex,
            final Map<String, String> expectedResult) {
        final FindAllOrganisationsResponse response = ((FindAllOrganisationsResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE));

        final Organisation organisation = response.getOrganisations().get(expectedIndex - 1);
        Assert.assertEquals(getString(expectedResult, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION), organisation.getOrganisationIdentification());
        Assert.assertEquals(getString(expectedResult, Keys.KEY_NAME, Defaults.DEFAULT_ORGANIZATION_NAME),
                organisation.getName());
        final String domains = getString(expectedResult, Keys.KEY_DOMAINS, Defaults.DEFAULT_DOMAINS);
        Assert.assertEquals("[" + domains.replaceAll(";", ", ") + "]", organisation.getDomains().toString());
        Assert.assertEquals(getString(expectedResult, Keys.KEY_PREFIX, Defaults.DEFAULT_PREFIX),
                organisation.getPrefix());
    }
}
