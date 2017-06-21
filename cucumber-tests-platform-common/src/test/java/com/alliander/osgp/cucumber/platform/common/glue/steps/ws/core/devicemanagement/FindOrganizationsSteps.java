/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Organisation;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.PlatformDomain;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
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
     */
    @When("^receiving a get all organizations request$")
    public void receivingGetAllOrganizationsRequest(final Map<String, String> settings)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.current().put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, getString(settings,
                PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE,
                    this.client.findAllOrganizations(new FindAllOrganisationsRequest()));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }
    }

    /**
     *
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws WebServiceSecurityException
     */
    @When("^receiving an own unknown organization request$")
    public void receivingAnOwnUnknownOrganizationRequest(final Map<String, String> settings)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.current().put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, getString(settings,
                PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE,
                    this.client.findAllOrganizations(new FindAllOrganisationsRequest()));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }
    }

    @Then("^the get all organizations response contains (\\d++) organizations?$")
    public void theGetAllOrganizationsResponseContainsOrganization(final Integer expectedCount) {
        final FindAllOrganisationsResponse response = (FindAllOrganisationsResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);

        Assert.assertEquals((int) expectedCount, response.getOrganisations().size());
    }

    @Then("^the get own unknown organization response contains soap fault$")
    public void theGetOwnUnknownOrganizationResponseContainsSoapFault(final Map<String, String> expectedResult) {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }

    @Then("^the get all organizations response contains$")
    public void theGetAllOrganizationsResponseContains(final Map<String, String> expectedResult) {
        final FindAllOrganisationsResponse response = (FindAllOrganisationsResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);

        final List<Organisation> organisations = response.getOrganisations();

        final Organisation expected = this.createOrganisation(expectedResult);

        for (final Organisation organisation : organisations) {
            if (this.organisationMatches(expected, organisation)) {
                return;
            }
        }

        Assert.fail("Expected organization \"" + expected.getOrganisationIdentification()
                + "\" was not found as one of the " + organisations.size() + " organizations in the response");

    }

    private Organisation createOrganisation(final Map<String, String> expectedResult) {
        final Organisation expected = new Organisation();
        expected.setOrganisationIdentification(getString(expectedResult, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        expected.setName(getString(expectedResult, PlatformKeys.KEY_NAME, PlatformDefaults.DEFAULT_ORGANIZATION_NAME));
        expected.getDomains().addAll(this.createDomains(expectedResult));
        expected.setPrefix(getString(expectedResult, PlatformKeys.KEY_PREFIX, PlatformDefaults.DEFAULT_PREFIX));
        return expected;
    }

    private List<PlatformDomain> createDomains(final Map<String, String> expectedResult) {
        return Arrays.stream(
                getString(expectedResult, PlatformKeys.KEY_DOMAINS, PlatformDefaults.DEFAULT_DOMAINS).split(";"))
                .map(PlatformDomain::valueOf).collect(Collectors.toList());
    }

    private boolean organisationMatches(final Organisation expected, final Organisation actual) {
        return expected.getOrganisationIdentification().equals(actual.getOrganisationIdentification())
                && expected.getName().equals(actual.getName())
                && this.domainsMatch(expected.getDomains(), actual.getDomains())
                && expected.getPrefix().equals(actual.getPrefix());
    }

    private boolean domainsMatch(final List<PlatformDomain> expected, final List<PlatformDomain> actual) {
        return expected.size() == actual.size() && actual.containsAll(expected);
    }
}
