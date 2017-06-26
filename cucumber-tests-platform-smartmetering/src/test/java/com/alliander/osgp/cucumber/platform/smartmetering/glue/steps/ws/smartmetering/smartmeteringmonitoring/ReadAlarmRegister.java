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

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ReadAlarmRegisterRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReadAlarmRegister extends SmartMeteringStepsBase {

    @Autowired
    private SmartMeteringMonitoringRequestClient<ReadAlarmRegisterAsyncResponse, ReadAlarmRegisterRequest> requestClient;

    @Autowired
    private SmartMeteringMonitoringResponseClient<ReadAlarmRegisterResponse, ReadAlarmRegisterAsyncRequest> responseClient;

    @When("^the get read alarm register request is received$")
    public void theGetReadAlarmRegisterRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final ReadAlarmRegisterRequest request = ReadAlarmRegisterRequestFactory.fromParameterMap(settings);
        final ReadAlarmRegisterAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertNotNull("asyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the alarm register should be returned$")
    public void theAlarmRegisterShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final ReadAlarmRegisterAsyncRequest asyncRequest = ReadAlarmRegisterRequestFactory.fromScenarioContext();
        final ReadAlarmRegisterResponse response = this.responseClient.getResponse(asyncRequest);

        assertNotNull("AlarmTypes should not be null", response.getAlarmTypes());
        assertNotNull("AlarmType should not be null", response.getAlarmTypes().get(0));

    }
}
