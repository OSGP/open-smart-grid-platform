/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.functional.exceptions;

import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetAdministrativeStatusRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.installation.AddDeviceRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FunctionalExceptionsSteps {

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @Autowired
    private SmartMeteringInstallationClient smartMeteringInstallationClient;

    @When("^the get administrative status request for an invalid organisation is received$")
    public void theRetrieveAdministrativeStatusRequestForAnInvalidOrganisationIsReceived(
            final Map<String, String> requestData) throws Throwable {
        final GetAdministrativeStatusRequest getAdministrativeStatusRequest = GetAdministrativeStatusRequestFactory
                .fromParameterMap(requestData);

        if (requestData.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
            final String organisation = getString(requestData, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION);
            ScenarioContext.current().put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, organisation);
        }

        try {
            this.smartMeteringConfigurationClient.getAdministrativeStatus(getAdministrativeStatusRequest);
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }

    }

    @When("^the get administrative status request for an invalid device is received$")
    public void theGetAdministrativeStatusRequestForAnInvalidDeviceIsReceived(final Map<String, String> requestData)
            throws Throwable {
        final GetAdministrativeStatusRequest getAdministrativeStatusRequest = GetAdministrativeStatusRequestFactory
                .fromParameterMap(requestData);
        try {
            this.smartMeteringConfigurationClient.getAdministrativeStatus(getAdministrativeStatusRequest);
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }
    }

    @Then("^the add device response for an existing device is received$")
    public void theAddDeviceResponseForAnExistingDeviceIsReceived(final Map<String, String> responseParameters)
            throws Throwable {
        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                PlatformKeys.KEY_CORRELATION_UID, correlationUid);

        final AddDeviceAsyncRequest addDeviceAsyncRequest = AddDeviceRequestFactory
                .fromParameterMapAsync(extendedParameters);
        try {
            this.smartMeteringInstallationClient.getAddDeviceResponse(addDeviceAsyncRequest);
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }
    }
}
