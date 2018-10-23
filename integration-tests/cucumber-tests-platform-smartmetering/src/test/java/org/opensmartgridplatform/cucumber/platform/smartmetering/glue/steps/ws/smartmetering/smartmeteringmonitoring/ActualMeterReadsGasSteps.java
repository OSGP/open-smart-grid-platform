/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ActualMeterReadsGasRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ActualMeterReadsGasSteps {

    @Autowired
    private SmartMeteringMonitoringRequestClient<ActualMeterReadsGasAsyncResponse, ActualMeterReadsGasRequest> requestClient;

    @Autowired
    private SmartMeteringMonitoringResponseClient<ActualMeterReadsGasResponse, ActualMeterReadsGasAsyncRequest> responseClient;

    @When("^the get actual meter reads gas request is received$")
    public void theGetActualMeterReadsRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final ActualMeterReadsGasRequest request = ActualMeterReadsGasRequestFactory.fromParameterMap(settings);
        final ActualMeterReadsGasAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertNotNull("AsyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the actual meter reads gas result should be returned$")
    public void theActualMeterReadsResultShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final ActualMeterReadsGasAsyncRequest asyncRequest = ActualMeterReadsGasRequestFactory.fromScenarioContext();
        final ActualMeterReadsGasResponse response = this.responseClient.getResponse(asyncRequest);

        assertNotNull("ActualMeterReadsGasResponse should not be null", response);
        assertNotNull("Consumption should not be null", response.getConsumption());
        assertNotNull("CaptureTime should not be null", response.getCaptureTime());
        assertNotNull("LogTime should not be null", response.getLogTime());
    }
}
