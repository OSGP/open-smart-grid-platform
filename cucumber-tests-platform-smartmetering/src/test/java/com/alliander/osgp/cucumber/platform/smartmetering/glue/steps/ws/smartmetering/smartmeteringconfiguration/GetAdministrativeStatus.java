/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetAdministrativeStatus extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_ENABLED = "/Envelope/Body/GetAdministrativeStatusResponse/Enabled/text()";

    private static final String XPATH_MATCHER_RESULT_ENABLED = "\\w[A-Z]";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "190 Get administrative status";
    private static final String TEST_CASE_NAME_REQUEST = "GetAdministrativeStatus - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetGetAdministrativeStatusResponse - Request 1";

    @When("^the get administrative status request is received$")
    public void theRetrieveAdministrativeStatusRequestIsReceived(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, getString(settings,
                PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the administrative status should be returned$")
    public void theAdministrativeStatusShouldBeReturned(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_CORRELATION_UID,
                ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ENABLED, XPATH_MATCHER_RESULT_ENABLED));
    }

}
