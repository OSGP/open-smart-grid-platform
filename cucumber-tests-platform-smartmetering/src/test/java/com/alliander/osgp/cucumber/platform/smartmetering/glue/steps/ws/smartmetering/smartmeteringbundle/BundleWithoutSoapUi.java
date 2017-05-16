/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.Actions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SmartMeteringBundleClient;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

public class BundleWithoutSoapUi {
    @Autowired
    private SmartMeteringBundleClient client;

    @Given("^a bundle request$")
    public void aBundleRequest(final Map<String, String> settings) throws Throwable {

        final BundleRequest request = new BundleRequest();
        request.setDeviceIdentification(
                getString(settings, PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION, PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

        final Actions actions = new Actions();
        request.setActions(actions);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.BUNDLE_REQUEST, request);
    }

    @When("^the bundle request is received$")
    public void theBundleRequestIsReceived() throws Throwable {
        final BundleRequest request = (BundleRequest) ScenarioContext.current().get(PlatformSmartmeteringKeys.BUNDLE_REQUEST);

        final BundleAsyncResponse asyncResponse = this.client.sendBundleRequest(request);

        assertNotNull(asyncResponse);
        Helpers.saveAsyncResponse(asyncResponse);
    }

}
