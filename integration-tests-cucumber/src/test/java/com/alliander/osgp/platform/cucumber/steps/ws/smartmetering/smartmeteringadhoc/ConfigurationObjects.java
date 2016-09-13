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
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ConfigurationObjects extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/RetrieveConfigurationObjectsResponse/Result/text()";
    private static final String PATH_RESULT_OUTPUT = "/Envelope/Body/RetrieveConfigurationObjectsResponse/Output/text()";

    private static final String XPATH_MATCHER_RESULT_OUTPUT = "DataObject: Choice=\\w+, ResultData \\w+, value=\\S+ logical name: \\S+";

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML = "193 Retrieve available objects of a meter";
    private static final String TEST_CASE_NAME_REQUEST = "RetrieveConfigurationObjects - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE_REQUEST = "GetRetrieveConfigurationObjectsResponse - Request 1";

    @When("^receiving a retrieve configuration request$")
    public void receivingARetrieveConfigurationRequest(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, requestData.get("DeviceIdentification"));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^all the configuration items should be returned$")
    public void allTheConfigurationItemsShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().get("CorrelationUid").toString());
        PROPERTIES_MAP.put(MAX_TIME, "1800000");

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, Defaults.EXPECTED_RESULT));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_OUTPUT, XPATH_MATCHER_RESULT_OUTPUT));
    }
}
