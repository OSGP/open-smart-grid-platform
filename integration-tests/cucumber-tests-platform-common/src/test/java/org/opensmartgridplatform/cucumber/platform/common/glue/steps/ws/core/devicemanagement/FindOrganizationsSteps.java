/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Organisation;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.PlatformDomain;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class FindOrganizationsSteps {

    @Autowired
    private CoreDeviceManagementClient client;

    @When("^receiving a get organization request$")
    public void receivingGetOrganizationRequest(final Map<String, String> settings)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.current().put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, getString(settings,
                PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        final FindOrganisationRequest request = new FindOrganisationRequest();
        request.setOrganisationIdentification(getString(settings, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION_TO_FIND,
                PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION_TO_FIND));

        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.findOrganization(request));
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }
    }

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

    @Then("^the get organization response contains (\\d++) organizations?$")
    public void theGetOrganizationResponseContainsOrganization(final Integer expectedCount) {
        final FindOrganisationResponse response = (FindOrganisationResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);

        if (expectedCount == 0) {
            Assert.assertNull("Organisation object should be null.", response.getOrganisation());
        } else if (expectedCount == 1) {
            Assert.assertNotNull("Organisation object should not be null.", response.getOrganisation());
        } else {
            Assert.fail(
                    "This Then step should be used to test if an organisation object is null or if there's exactly 1 organisation object. The argument 'expectedCount' with value "
                            + expectedCount + " is not valid for this step.");
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

    @Then("^the get organization response contains$")
    public void theGetOrganizationResponseContains(final Map<String, String> expectedResult) {
        final FindOrganisationResponse response = (FindOrganisationResponse) ScenarioContext.current()
                .get(PlatformCommonKeys.RESPONSE);

        final Organisation organisation = response.getOrganisation();

        final Organisation expected = this.createOrganisation(expectedResult);

        Assert.assertTrue(
                "Expected organization \"" + expected.getOrganisationIdentification()
                        + "\" was not found as the organization in the response",
                this.organisationMatches(expected, organisation));
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
