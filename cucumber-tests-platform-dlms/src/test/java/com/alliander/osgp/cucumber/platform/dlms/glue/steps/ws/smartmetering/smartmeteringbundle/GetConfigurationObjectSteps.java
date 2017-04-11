/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Keys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class GetConfigurationObjectSteps extends BaseBundleSteps {

    @Given("^a get configuration object is part of a bundled request$")
    public void aGetConfigurationObjectIsPartOfBundledRequest() throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.Current()
                .get(Keys.SCENARIO_CONTEXT_BUNDLE_REQUEST);

        request.getActions().getActionList().add(new GetConfigurationObjectRequest());
        this.increaseCount(Keys.SCENARIO_CONTEXT_BUNDLE_ACTIONS);
    }

    @Then("^the bundle response contains a get configuration object response$")
    public void theBundleResponseContainsConfigurationObjectResponse(final Map<String, String> settings)
            throws Throwable {

        this.ensureBundleResponse(settings);

        final BundleResponse response = (BundleResponse) ScenarioContext.Current()
                .get(Keys.SCENARIO_CONTEXT_BUNDLE_RESPONSE);

        final Response actionResponse = response.getAllResponses().getResponseList()
                .get(this.getAndIncreaseCount(Keys.SCENARIO_CONTEXT_BUNDLE_RESPONSES));

        System.out.println(actionResponse.getClass());
        Assert.assertTrue("response should be a GetConfigurationResponse object",
                actionResponse instanceof GetConfigurationObjectResponse);
        final ConfigurationObject configurationObject = ((GetConfigurationObjectResponse) actionResponse)
                .getConfigurationObject();

        Assert.assertEquals("", settings.get("GprsOperationMode"),
                configurationObject.getGprsOperationMode().toString());
        configurationObject.getConfigurationFlags().getConfigurationFlag().forEach(f -> this.testConfigurationFlags(f, settings));
    }

    private void testConfigurationFlags(final ConfigurationFlag configFlag, final Map<String, String> settings) {
        final String key = configFlag.getConfigurationFlagType().name();
        final boolean value = getBoolean(settings, key);
        Assert.assertEquals("", value, configFlag.isEnabled());
    }
}
