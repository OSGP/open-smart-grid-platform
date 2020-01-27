/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SynchronizeTimeRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SynchronizeTime {

    @Autowired
    private SmartMeteringAdHocRequestClient<SynchronizeTimeAsyncResponse, SynchronizeTimeRequest> requestClient;

    @Autowired
    private SmartMeteringAdHocResponseClient<SynchronizeTimeResponse, SynchronizeTimeAsyncRequest> responseClient;

    @When("^receiving a get synchronize time request$")
    public void receivingAGetSynchronizeTimeRequest(final Map<String, String> settings) throws Throwable {

        final SynchronizeTimeRequest request = SynchronizeTimeRequestFactory.fromParameterMap(settings);
        final SynchronizeTimeAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the date and time is synchronized on the device$")
    public void theDateAndTimeIsSynchronizedOnTheDevice(final Map<String, String> settings) throws Throwable {

        final SynchronizeTimeAsyncRequest asyncRequest = SynchronizeTimeRequestFactory.fromScenarioContext();
        final SynchronizeTimeResponse response = this.responseClient.getResponse(asyncRequest);

        assertThat(response.getResult()).as("Results was not as expected").isEqualTo(OsgpResultType.OK);
    }
}
