/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Map;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ActualMeterReadsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ActualMeterReadsSteps {

    @Autowired
    private SmartMeteringMonitoringRequestClient<ActualMeterReadsAsyncResponse, ActualMeterReadsRequest> requestClient;

    @Autowired
    private SmartMeteringMonitoringResponseClient<ActualMeterReadsResponse, ActualMeterReadsAsyncRequest> responseClient;

    @When("^the get actual meter reads request is received$")
    public void theGetActualMeterReadsRequestIsReceived(final Map<String, String> settings) throws Throwable {
        final ActualMeterReadsRequest request = ActualMeterReadsRequestFactory.fromParameterMap(settings);

        final ActualMeterReadsAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @When("^the get actual meter reads request generating an error is received$")
    public void theGetActualMeterReadsRequestGeneratingAnErrorIsReceived(final Map<String, String> settings)
            throws Throwable {
        final ActualMeterReadsRequest request = ActualMeterReadsRequestFactory.fromParameterMap(settings);
        final ActualMeterReadsAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());

        final ActualMeterReadsAsyncRequest actualMeterReadsAsyncRequest = ActualMeterReadsRequestFactory
                .fromScenarioContext();
        assertThat(actualMeterReadsAsyncRequest).as("ActualMeterReadsAsyncRequest should not be null").isNotNull();

        try {
            final ActualMeterReadsResponse response = this.responseClient.getResponse(actualMeterReadsAsyncRequest);
            fail("Expected exception, but got a response: %s", response.toString());
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }
    }

    @Then("^the actual meter reads result should be returned$")
    public void theActualMeterReadsResultShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final ActualMeterReadsAsyncRequest asyncRequest = ActualMeterReadsRequestFactory.fromScenarioContext();
        final ActualMeterReadsResponse response = this.responseClient.getResponse(asyncRequest);

        assertThat(response).as("ActualMeterReadsResponse should not be null").isNotNull();
        assertThat(response.getActiveEnergyExport()).as("ActiveEnergyExport should not be null").isNotNull();
        assertThat(response.getActiveEnergyExportTariffOne()).as("ActiveEnergyExportTariffOne should not be null")
                .isNotNull();
        assertThat(response.getActiveEnergyExportTariffTwo()).as("ActiveEnergyExportTariffTwo should not be null")
                .isNotNull();
        assertThat(response.getActiveEnergyImport()).as("ActiveEnergyImport should not be null").isNotNull();
        assertThat(response.getActiveEnergyImportTariffOne()).as("ActiveEnergyImportTariffOne should not be null")
                .isNotNull();
        assertThat(response.getActiveEnergyImportTariffTwo()).as("ActiveEnergyimportTariffTwo should not be null")
                .isNotNull();
        assertThat(response.getLogTime()).as("LogTime should not be null").isNotNull();
    }
}
