/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ReadAlarmRegisterRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReadAlarmRegisterSteps {

    @Autowired
    private SmartMeteringMonitoringRequestClient<ReadAlarmRegisterAsyncResponse, ReadAlarmRegisterRequest> requestClient;

    @Autowired
    private SmartMeteringMonitoringResponseClient<ReadAlarmRegisterResponse, ReadAlarmRegisterAsyncRequest> responseClient;

    @When("^the get read alarm register request is received$")
    public void theGetReadAlarmRegisterRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final ReadAlarmRegisterRequest request = ReadAlarmRegisterRequestFactory.fromParameterMap(settings);
        final ReadAlarmRegisterAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the alarm register should be returned$")
    public void theAlarmRegisterShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final ReadAlarmRegisterAsyncRequest asyncRequest = ReadAlarmRegisterRequestFactory.fromScenarioContext();
        final ReadAlarmRegisterResponse response = this.responseClient.getResponse(asyncRequest);

        assertThat(response.getAlarmTypes()).as("AlarmTypes should not be null").isNotNull();
        assertThat(response.getAlarmTypes().get(0)).as("AlarmType should not be null").isNotNull();
    }
}
