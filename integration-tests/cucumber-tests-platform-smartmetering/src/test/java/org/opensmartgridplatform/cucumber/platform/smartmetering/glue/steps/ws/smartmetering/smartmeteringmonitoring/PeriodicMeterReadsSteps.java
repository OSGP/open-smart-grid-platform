/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.*;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.PeriodicMeterReadsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PeriodicMeterReadsSteps {

    @Autowired
    private SmartMeteringMonitoringRequestClient<PeriodicMeterReadsAsyncResponse, PeriodicMeterReadsRequest> requestClient;

    @Autowired
    private SmartMeteringMonitoringResponseClient<PeriodicMeterReadsResponse, PeriodicMeterReadsAsyncRequest> responseClient;

    @When("^the get \"([^\"]*)\" meter reads request is received$")
    public void theGetMeterReadsRequestIsReceived(final String periodType, final Map<String, String> settings)
            throws Throwable {

        final PeriodicMeterReadsRequest request = PeriodicMeterReadsRequestFactory.fromParameterMap(settings);

        final PeriodicMeterReadsAsyncResponse asyncResponse = this.requestClient.doRequest(request);
        assertNotNull(asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the \"([^\"]*)\" meter reads result should be returned$")
    public void theMeterReadsResultShouldBeReturned(final String periodType, final Map<String, String> settings)
            throws Throwable {

        final PeriodicMeterReadsAsyncRequest asyncRequest = PeriodicMeterReadsRequestFactory.fromScenarioContext();
        final PeriodicMeterReadsResponse response = this.responseClient.getResponse(asyncRequest);

        assertNotNull("PeriodicMeterReadsGasResponse should not be null", response);
        assertEquals("PeriodType should match", PeriodType.fromValue(periodType), response.getPeriodType());
        assertNotNull("Expected periodic meter reads gas", response.getPeriodicMeterReads());
    }
}
