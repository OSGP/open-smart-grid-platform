/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.smartmetering.smartmeteringadhoc;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SpecificConfigurationObject extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_CONFIGURATION_DATA = "/Envelope/Body/SpecificConfigurationObjectResponse/ConfigurationData/text()";

    private static final String XPATH_MATCHER_RESULT_CONFIGURATION_DATA = "DataObject: Choice=\\w[A-Z]+, ResultData \\w+, value=";

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML = "534 Retrieve specific configuration object";
    private static final String TEST_CASE_NAME_REQUEST = "GetSpecificConfigurationObject - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetSpecificConfigurationObjectResponse - Request 1";

    @When("^receiving a retrieve specific configuration request$")
    public void receivingARetrieveSpecificConfigurationRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, requestData.get("DeviceIdentification"));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the specific configuration item should be returned$")
    public void theSpecificConfigurationItemShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().get("CorrelationUid").toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CONFIGURATION_DATA,
                XPATH_MATCHER_RESULT_CONFIGURATION_DATA));
    }
}
