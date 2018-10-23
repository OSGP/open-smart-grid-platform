/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetFirmwareVersionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration.GetFirmwareVersion;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetFirmwareVersionSteps extends BaseBundleSteps {

    @Autowired
    private GetFirmwareVersion getFirmwareVersionSteps;

    @Given("^the bundle request contains a get firmware version action$")
    public void theBundleRequestContainsAGetFirmwareVersionAction() throws Throwable {

        final GetFirmwareVersionRequest action = new GetFirmwareVersionRequest();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a get firmware version response$")
    public void theBundleResponseShouldContainAGetFirmwareVersionResponse(final Map<String, String> settings)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        Assert.assertTrue("response should be a GetFirmwareVersionResponse object",
                response instanceof GetFirmwareVersionResponse);

        final GetFirmwareVersionResponse getFirmwareVersionResponse = (GetFirmwareVersionResponse) response;

        final List<FirmwareVersion> firmwareVersions = getFirmwareVersionResponse.getFirmwareVersions();

        this.getFirmwareVersionSteps.checkFirmwareVersionResult(settings, firmwareVersions);
    }

}
