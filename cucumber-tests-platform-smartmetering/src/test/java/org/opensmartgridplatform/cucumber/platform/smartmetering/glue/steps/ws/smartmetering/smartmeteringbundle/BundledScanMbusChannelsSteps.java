/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ScanMbusChannelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ScanMbusChannelsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc.ScanMbusChannelsSteps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledScanMbusChannelsSteps extends BaseBundleSteps {

    @Autowired
    private ScanMbusChannelsSteps scanMbusChannelsSteps;

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
        this.scanMbusChannelsSteps.assertChannelShortIds(values, response.getChannelShortIds());
    }

}
