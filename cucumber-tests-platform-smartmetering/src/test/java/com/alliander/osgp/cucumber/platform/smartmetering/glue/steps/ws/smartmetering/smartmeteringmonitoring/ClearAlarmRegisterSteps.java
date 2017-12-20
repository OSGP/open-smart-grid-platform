/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ClearAlarmRegisterRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ClearAlarmRegisterSteps {

    @Autowired
    private SmartMeteringMonitoringRequestClient<ClearAlarmRegisterAsyncResponse, ClearAlarmRegisterRequest> smMonitoringRequestClientClearAlarmRegister;

    @Autowired
    private SmartMeteringMonitoringResponseClient<ClearAlarmRegisterResponse, ClearAlarmRegisterAsyncRequest> smMonitoringResponseClientClearAlarmRegister;

    @When("^the Clear Alarm Code request is received$")
    public void theClearAlarmCodeRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final ClearAlarmRegisterRequest clearAlarmRegisterRequest = ClearAlarmRegisterRequestFactory
                .fromParameterMap(settings);

        final ClearAlarmRegisterAsyncResponse clearAlarmRegisterAsyncResponse = this.smMonitoringRequestClientClearAlarmRegister
                .doRequest(clearAlarmRegisterRequest);

        assertNotNull("ClearAlarmRegisterAsyncResponse should not be null", clearAlarmRegisterAsyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID,
                clearAlarmRegisterAsyncResponse.getCorrelationUid());
    }

    @Then("^the Clear Alarm Code response should be returned$")
    public void theClearAlarmCodeResponseShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final ClearAlarmRegisterAsyncRequest clearAlarmRegisterAsyncRequest = ClearAlarmRegisterRequestFactory
                .fromScenarioContext();

        final ClearAlarmRegisterResponse clearAlarmRegisterResponse = this.smMonitoringResponseClientClearAlarmRegister
                .getResponse(clearAlarmRegisterAsyncRequest);

        assertNotNull("ClearAlarmRegisterResponse should not be null", clearAlarmRegisterResponse);
        assertNotNull("Expected OsgpResultType should not be null", clearAlarmRegisterResponse.getResult());

        assertEquals("Result is not 'OK' as expected.", settings.get(PlatformSmartmeteringKeys.RESULT),
                clearAlarmRegisterResponse.getResult().name());
    }

}
