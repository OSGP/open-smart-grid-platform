/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ConfigurationSteps extends SmartMeteringStepsBase {

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML_501 = "501 Retrieve specific attribute value bundle";
    private static final String TEST_CASE_XML_526 = "526 Retrieve association objectlist bundle";

    private static final String TEST_CASE_NAME_REQUEST = "Bundle - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetBundleResponse - Request 1";

    @When("^the get associationLnObjects request is received as part of a bundled request$")
    public void theGetAssociationLnObjectsRequestIsReceivedAsPartOfABundledRequest(final Map<String, String> settings)
            throws Throwable {
        this.setDeviceAndOrganisationProperties(settings);

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML_526,
                TEST_SUITE_XML);
    }

    private void setDeviceAndOrganisationProperties(final Map<String, String> settings) {
        PROPERTIES_MAP.put(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION, PlatformSmartmeteringDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformSmartmeteringKeys.KEY_ORGANIZATION_IDENTIFICATION, getString(settings,
                PlatformSmartmeteringKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformSmartmeteringDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }

    @Then("^the response should contain$")
    public void theResponseShouldContain(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION, PlatformSmartmeteringDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                ScenarioContext.current().get(PlatformSmartmeteringKeys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML_501,
                TEST_SUITE_XML);

        assertTrue(this.response.contains(settings.get("ResponsePart")));
    }

}
