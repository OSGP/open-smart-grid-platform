/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.ScanMbusChannelsRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ScanMbusChannels {

    @Autowired
    private SmartMeteringAdHocRequestClient<ScanMbusChannelsAsyncResponse, ScanMbusChannelsRequest> requestClient;

    @Autowired
    private SmartMeteringAdHocResponseClient<ScanMbusChannelsResponse, ScanMbusChannelsAsyncRequest> responseClient;

    @When("^the scan M-Bus channels request is received$")
    public void theScanMBusChannelsRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final ScanMbusChannelsRequest request = ScanMbusChannelsRequestFactory.fromParameterMap(settings);
        final ScanMbusChannelsAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertNotNull("AsyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the found M-bus devices are in the response$")
    public void theFoundMBusDevicesAreInTheResponse(final Map<String, String> settings) throws Throwable {
        final ScanMbusChannelsAsyncRequest asyncRequest = ScanMbusChannelsRequestFactory.fromScenarioContext();
        final ScanMbusChannelsResponse response = this.responseClient.getResponse(asyncRequest);

        final String EXPECTED_CHANNEL1 = settings.get("Channel1MbusIdentificationNumber");
        final String EMPTY_CHANNEL = "0";

        assertEquals("Result is not as expected.", settings.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());

        assertEquals(
                "Mbus Identification Number Channel 1 has value: " + response.getMbusIdentificationNumber1()
                        + " instead of: " + EXPECTED_CHANNEL1,
                EXPECTED_CHANNEL1, response.getMbusIdentificationNumber1());
        assertEquals("Mbus Identification Number Channel 2 has value: " + response.getMbusIdentificationNumber2()
                + " instead of: " + EMPTY_CHANNEL, EMPTY_CHANNEL, response.getMbusIdentificationNumber2());
        assertEquals("Mbus Identification Number Channel 3 has value: " + response.getMbusIdentificationNumber3()
                + " instead of: " + EMPTY_CHANNEL, EMPTY_CHANNEL, response.getMbusIdentificationNumber3());
        assertEquals("Mbus Identification Number Channel 4 has value: " + response.getMbusIdentificationNumber4()
                + " instead of: " + EMPTY_CHANNEL, EMPTY_CHANNEL, response.getMbusIdentificationNumber4());
    }

}
