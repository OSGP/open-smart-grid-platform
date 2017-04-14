/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.Actions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.cucumber.platform.core.Helpers;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Defaults;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.bundle.SmartMeteringBundleClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import ma.glasnost.orika.MapperFacade;

public class BundleWithoutSoapUi {

    private static final String SCENARIO_CONTEXT_BUNDLE_RESPONSE = "BundleResponse";
    private static final String SCENARIO_CONTEXT_BUNDLE_REQUEST = "BundleRequest";
    private static final String SCENARIO_CONTEXT_BUNDLE_ACTIONS = "BundleActions";
    private static final String SCENARIO_CONTEXT_BUNDLE_RESPONSES = "BundleResponses";

    @Autowired
    private SmartMeteringBundleClient client;

    @Autowired
    private MapperFacade defaultMapper;

    @Given("^a bundle request$")
    public void aBundleRequest(final Map<String, String> settings) throws Throwable {

        final BundleRequest request = new BundleRequest();
        request.setDeviceIdentification(
                getString(settings, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));

        final Actions actions = new Actions();
        request.setActions(actions);

        ScenarioContext.Current().put(SCENARIO_CONTEXT_BUNDLE_REQUEST, request);
    }

    @When("^the bundle request is received$")
    public void theBundleRequestIsReceived() throws Throwable {
        final BundleRequest request = (BundleRequest) ScenarioContext.Current().get(SCENARIO_CONTEXT_BUNDLE_REQUEST);

        final BundleAsyncResponse asyncResponse = this.client.sendBundleRequest(request);

        assertNotNull(asyncResponse);
        Helpers.saveAsyncResponse(asyncResponse);
    }

    private void ensureBundleResponse(final Map<String, String> settings)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        if (ScenarioContext.Current().get(SCENARIO_CONTEXT_BUNDLE_RESPONSE) == null) {
            final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
            final BundleAsyncRequest asyncRequest = new BundleAsyncRequest();
            asyncRequest.setCorrelationUid(correlationUid);
            asyncRequest.setDeviceIdentification((String) ScenarioContext.Current().get(Keys.DEVICE_IDENTIFICATION));

            final BundleResponse response = this.client.retrieveBundleResponse(asyncRequest);
            ScenarioContext.Current().put(SCENARIO_CONTEXT_BUNDLE_RESPONSE, response);

            assertEquals(ScenarioContext.Current().get(SCENARIO_CONTEXT_BUNDLE_ACTIONS),
                    response.getAllResponses().getResponseList().size());
        }
    }

    private void increaseCount(final String key) {
        if (ScenarioContext.Current().get(key) == null) {
            ScenarioContext.Current().put(key, 1);
        } else {
            ScenarioContext.Current().put(key, (Integer) ScenarioContext.Current().get(key) + 1);
        }
    }

    private int getAndIncreaseCount(final String key) {
        if (ScenarioContext.Current().get(key) == null) {
            ScenarioContext.Current().put(key, 1);
            return 0;
        }
        final Integer value = (Integer) ScenarioContext.Current().get(key);
        ScenarioContext.Current().put(key, value + 1);

        return value;
    }
}
