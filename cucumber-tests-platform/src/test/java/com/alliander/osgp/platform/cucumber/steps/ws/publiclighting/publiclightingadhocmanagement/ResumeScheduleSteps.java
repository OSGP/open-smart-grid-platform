/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.publiclighting.publiclightingadhocmanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.common.ResponseSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.publiclighting.PublicLightingStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

/**
 * Class with all the set light requests steps
 */
public class ResumeScheduleSteps extends PublicLightingStepsBase {
    private static final String TEST_SUITE_XML = "PublicLightingAdHocManagement";
    private static final String TEST_CASE_ASYNC_REQ_XML = "ResumeSchedule TestCase";
    private static final String TEST_CASE_ASYNC_NAME_REQUEST = "ResumeSchedule";
    private static final String TEST_CASE_RESULT_REQ_XML = "GetResumeScheduleResponse TestCase";
    private static final String TEST_CASE_RESULT_NAME_REQUEST = "GetResumeScheduleResponse";

    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeScheduleSteps.class);
    
    /**
     * Sends a Resume Schedule request to the platform for a given device identification.
     * @param requestParameters The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a resume schedule request$")
    public void whenReceivingAResumeScheduleRequest(final Map<String, String> requestParameters) throws Throwable {

        // Required parameters
    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    	PROPERTIES_MAP.put("__INDEX__", requestParameters.get("Index"));
    	PROPERTIES_MAP.put("__IS_IMMEDIATE__", requestParameters.get("IsImmediate"));
    	
        // Now run the request.
//        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_ASYNC_NAME_REQUEST, TEST_CASE_ASYNC_REQ_XML, TEST_SUITE_XML);
        this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_ASYNC_NAME_REQUEST, TEST_CASE_ASYNC_REQ_XML, TEST_SUITE_XML);
    }
    
    /**
     * Sends a Resume Schedule response to the platform for a given device identification.
     * @param requestParameters The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a resume schedule response$")
    public void whenReceivingAResumeScheduleResponse(final Map<String, String> requestParameters) throws Throwable {

        // Required parameters
    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    	PROPERTIES_MAP.put("__INDEX__", requestParameters.get("Index"));
    	PROPERTIES_MAP.put("__IS_IMMEDIATE__", requestParameters.get("IsImmediate"));
    	
        // Now run the request.
//        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_ASYNC_NAME_REQUEST, TEST_CASE_ASYNC_REQ_XML, TEST_SUITE_XML);
        this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_ASYNC_NAME_REQUEST, TEST_CASE_ASYNC_REQ_XML, TEST_SUITE_XML);
    }
    
    /**
     * The check for the response from the Platform.
     * @param expectedResponseData The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the resume schedule async response contains$")
    public void thenTheResumeScheduleAsyncResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        this.runXpathResult.assertXpath(this.response, PATH_DEVICE_IDENTIFICATION,
                getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        this.runXpathResult.assertNotNull(this.response, PATH_CORRELATION_UID);

//        LOGGER.info("Correlation-UID: " + this.response + " | Path: " + PATH_CORRELATION_UID);
//        LOGGER.info("Correlation-UID: " + this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID));
        
//        LOGGER.info("Response: " + this.response);
        
        if (expectedResponseData.containsKey("FaultCode"))
        {
        	ResponseSteps.VerifyFaultResponse(this.runXpathResult, this.response, expectedResponseData);
        }
        else
        {
        	// Save the returned CorrelationUid in the Scenario related context for further use.
            saveCorrelationUidInScenarioContext(this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
                    getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                            Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
            
            LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
        }
    }

    @Then("^the platform buffers a resume schedule response message for device \"([^\"]*)\"$")
    public void thenThePlatformBuffersAResumeScheduleResponseMessage(final String deviceIdentification) throws Throwable {
        // Required parameters
        PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", deviceIdentification);
        PROPERTIES_MAP.put("__CORRELATION_UID__", (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));

//        this.waitForResponse(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_RESULT_NAME_REQUEST,
//                    TEST_CASE_RESULT_REQ_XML, TEST_SUITE_XML);
        this.waitForResponse(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_RESULT_NAME_REQUEST,
                TEST_CASE_RESULT_REQ_XML, TEST_SUITE_XML);
    }
}