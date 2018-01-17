/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ActualMeterReadsRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ActualMeterReadsSteps {

    @Autowired
    private SmartMeteringMonitoringRequestClient<ActualMeterReadsAsyncResponse, ActualMeterReadsRequest> requestClient;

    @Autowired
    private SmartMeteringMonitoringResponseClient<ActualMeterReadsResponse, ActualMeterReadsAsyncRequest> responseClient;

    @When("^the get actual meter reads request is received$")
    public void theGetActualMeterReadsRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final ActualMeterReadsRequest request = ActualMeterReadsRequestFactory.fromParameterMap(settings);
        final ActualMeterReadsAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertNotNull("AsyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the actual meter reads result should be returned$")
    public void theActualMeterReadsResultShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final ActualMeterReadsAsyncRequest asyncRequest = ActualMeterReadsRequestFactory.fromScenarioContext();
        final ActualMeterReadsResponse response = this.responseClient.getResponse(asyncRequest);

        assertNotNull("ActualMeterReadsResponse should not be null", response);
        assertNotNull("ActiveEnergyExport should not be null", response.getActiveEnergyExport());
        assertNotNull("ActiveEnergyExportTariffOne should not be null", response.getActiveEnergyExportTariffOne());
        assertNotNull("ActiveEnergyExportTariffTwo should not be null", response.getActiveEnergyExportTariffTwo());
        assertNotNull("ActiveEnergyImport should not be null", response.getActiveEnergyImport());
        assertNotNull("ActiveEnergyImportTariffOne should not be null", response.getActiveEnergyImportTariffOne());
        assertNotNull("ActiveEnergyimportTariffTwo should not be null", response.getActiveEnergyImportTariffTwo());
        assertNotNull("LogTime should not be null", response.getLogTime());
    }
}
