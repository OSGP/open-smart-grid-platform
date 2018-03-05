/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.functional.exceptions;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration.GetAdministrativeStatus;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SpecificAttributeValueRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetAdministrativeStatusRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.installation.AddDeviceRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ActualMeterReadsGasRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FunctionalExceptionsSteps {

    /**
     * Some tests, for instance with some connection exceptions, require a
     * relatively long wait time before the actual response is available.
     *
     * For steps that are used in scenarios requiring a longer wait time, as
     * well as in scenarios that don't, the same step may be used, looking at
     * {@link #useLongWaitTime} to see which wait time to apply.
     */
    private static final int LONG_WAIT_TIME = 900000;

    @Autowired
    private GetAdministrativeStatus getAdministrativeStatus;

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @Autowired
    private SmartMeteringInstallationClient smartMeteringInstallationClient;

    @Autowired
    private SmartMeteringMonitoringRequestClient<ActualMeterReadsGasAsyncResponse, ActualMeterReadsGasRequest> actualMeterReadsGasRequestClient;

    @Autowired
    private SmartMeteringMonitoringResponseClient<ActualMeterReadsGasResponse, ActualMeterReadsGasAsyncRequest> actualMeterReadsGasResponseClient;

    @Autowired
    private SmartMeteringAdHocRequestClient<GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest> getSpecificAttributeValueRequestClient;

    @Autowired
    private SmartMeteringAdHocResponseClient<GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest> getSpecificAttributeValueResponseClient;

    private boolean useLongWaitTime;

    @Before
    public void before(final Scenario scenario) {
        this.useLongWaitTime = scenario.getSourceTagNames().contains("@NightlyBuildOnly");
    }

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

    @When("^receiving a smartmetering add device request with an invalid DSMR version$")
    public void receivingASmartmeteringAddDeviceRequestWithAnInvalidDsmrVersion(final Map<String, String> settings)
            throws Throwable {

        final AddDeviceRequest request = AddDeviceRequestFactory.fromParameterMap(settings);
        try {
            final AddDeviceAsyncResponse asyncResponse = this.smartMeteringInstallationClient.addDevice(request);
            final AddDeviceAsyncRequest addDeviceAsyncRequest = new AddDeviceAsyncRequest();
            addDeviceAsyncRequest.setCorrelationUid(asyncResponse.getCorrelationUid());
            addDeviceAsyncRequest.setDeviceIdentification(asyncResponse.getDeviceIdentification());
            this.smartMeteringInstallationClient.getAddDeviceResponse(addDeviceAsyncRequest);
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }
    }

    @When("^the get administrative status request generating an error is received$")
    public void theGetAdministrativeStatusRequestGeneratingAnErrorIsReceived(final Map<String, String> settings)
            throws Throwable {

        this.getAdministrativeStatus.theRetrieveAdministrativeStatusRequestIsReceived(settings);

        final GetAdministrativeStatusAsyncRequest getAdministrativeStatusAsyncRequest = GetAdministrativeStatusRequestFactory
                .fromScenarioContext();

        if (this.useLongWaitTime) {
            this.smartMeteringConfigurationClient.setWaitFailMillis(LONG_WAIT_TIME);
        }

        try {
            this.smartMeteringConfigurationClient
                    .retrieveGetAdministrativeStatusResponse(getAdministrativeStatusAsyncRequest);
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }
    }

    @When("^the get actual meter reads gas request generating an error is received$")
    public void theGetActualMeterReadsGasRequestGeneratingAnErrorIsReceived(final Map<String, String> requestData)
            throws Throwable {
        final ActualMeterReadsGasRequest request = ActualMeterReadsGasRequestFactory.fromParameterMap(requestData);
        final ActualMeterReadsGasAsyncResponse asyncResponse = this.actualMeterReadsGasRequestClient
                .doRequest(request);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());

        final ActualMeterReadsGasAsyncRequest asyncRequest = ActualMeterReadsGasRequestFactory.fromScenarioContext();

        try {
            this.actualMeterReadsGasResponseClient.getResponse(asyncRequest);
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }
    }

    @When("^the get specific attribute value request generating an error is received$")
    public void whenTheGetSpecificAttributeValueRequestGeneratingAnErrorIsReceived(final Map<String, String> settings)
            throws Throwable {
        final GetSpecificAttributeValueRequest request = SpecificAttributeValueRequestFactory
                .fromParameterMap(settings);
        final GetSpecificAttributeValueAsyncResponse asyncResponse = this.getSpecificAttributeValueRequestClient
                .doRequest(request);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());

        final GetSpecificAttributeValueAsyncRequest asyncRequest = SpecificAttributeValueRequestFactory
                .fromScenarioContext();

        try {
            this.getSpecificAttributeValueResponseClient.getResponse(asyncRequest);
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }
    }
}
