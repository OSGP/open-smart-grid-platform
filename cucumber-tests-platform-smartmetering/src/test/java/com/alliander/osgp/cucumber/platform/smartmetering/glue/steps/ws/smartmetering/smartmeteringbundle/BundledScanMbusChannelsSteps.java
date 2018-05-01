/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ScanMbusChannelsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ScanMbusChannelsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledScanMbusChannelsSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a scan mbus channels action$")
    public void theBundleRequestContainsAScanMbusChannelsAction() throws Throwable {

        final ScanMbusChannelsRequest action = new ScanMbusChannelsRequest();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a scan mbus channels response$")
    public void theBundleResponseShouldContainAScanMbusChannelsResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);

    }

    @Then("^the bundle response should contain a scan mbus channels response with values$")
    public void theBundleResponseShouldContainAScanMbusChannelsResponse(final Map<String, String> values)
            throws Throwable {

        final ScanMbusChannelsResponse response = (ScanMbusChannelsResponse) this.getNextBundleResponse();

        final String EXPECTED_CHANNEL1 = values.get("Channel1MbusIdentificationNumber");
        final String EMPTY_CHANNEL = "0";

        assertTrue("Not a valid response", response instanceof ScanMbusChannelsResponse);

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
