/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.PlatformDomain;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;

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
     * @throws Throwable
     */
    @When("^receiving an update organization request$")
    public void receivingAnUpdateOrganizationRequest(final Map<String, String> requestSettings) throws Throwable {
        final ChangeOrganisationRequest request = new ChangeOrganisationRequest();

        request.setOrganisationIdentification(
                getString(requestSettings, PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        request.setNewOrganisationName(getString(requestSettings, PlatformCommonKeys.KEY_NAME,
                PlatformCommonDefaults.DEFAULT_NEW_ORGANIZATION_NAME));

        request.setNewOrganisationPlatformFunctionGroup(getEnum(requestSettings,
                PlatformCommonKeys.KEY_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP, PlatformFunctionGroup.class,
                PlatformCommonDefaults.DEFAULT_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP));

        request.getNewOrganisationPlatformDomains().clear();
        for (final String platformDomain : getString(requestSettings, PlatformCommonKeys.KEY_DOMAINS,
                PlatformCommonDefaults.DEFAULT_DOMAINS).split(";")) {
            request.getNewOrganisationPlatformDomains().add(Enum.valueOf(PlatformDomain.class, platformDomain));
        }

        try {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.changeOrganization(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, ex);
        }
    }

    @Then("^the update organization response is successful$")
    public void theUpdateOrganizationResponseIsSuccessful() throws Throwable {
        Assert.assertTrue(
                ScenarioContext.current().get(PlatformCommonKeys.RESPONSE) instanceof ChangeOrganisationResponse);
    }

    /**
     * Verify that the create organization response contains the fault with the
     * given expectedResult parameters.
     *
     * @throws Throwable
     */
    @Then("^the update organization response contains$")
    public void theUpdateOrganizationResponseContains(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
