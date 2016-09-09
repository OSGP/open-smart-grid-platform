/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws_admin.firmwaremanagement;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.platform.cucumber.SoapUiRunner;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class with all the firmware requests steps
 */
public class GetFirmwareVersionSteps extends SoapUiRunner {
    private final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareVersionSteps.class);

    private static final String TEST_SUITE_XML = "FirmwareManagement";
    private static final String TEST_CASE_XML = "AT Retrieve GetFirmwareVersion result";
    private static final String TEST_CASE_NAME_REQUEST = "GetFirmwareVersion - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetFirmwareVersionResponse - Request 1";

    private static final String PATH_DEVICE_IDENTIFICATION = "/Envelope/Body/GetFirmwareVersionAsyncResponse/AsyncResponse/DeviceId/text()";
    // private static final String PATH_CORRELATION_UID =
    // "/Envelope/Body/GetFirmwareVersionAsyncResponse/AsyncResponse/CorrelationUid/text()";
    private static final String PATH_CORRELATION_UID = "//*[local-name()='CorrelationUid']/text()";

    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    /**
     *
     * @throws Throwable
     */
    @Given("^receiving a get firmware version request$")
    public void givenReceivingAGetFirmwareVersionRequest(final Map<String, String> requestParameters) throws Throwable {

        // Required parameters
        PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get("DeviceIdentification"));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    /**
     *
     * @param arg1
     * @throws Throwable
     */
    @Then("^the get firmware version response contains$")
    public void thenTheGetFirmwareVersionResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {
        this.runXpathResult.assertXpath(this.response, PATH_DEVICE_IDENTIFICATION,
                expectedResponseData.get("DeviceIdentification"));
        this.runXpathResult.assertNotNull(this.response, PATH_CORRELATION_UID);

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        /*
         * saveCorrelationUidInScenarioContext(this.runXpathResult.getValue(this
         * .response, PATH_CORRELATION_UID), getString(expectedResponseData,
         * "OrganizationIdentification",
         * Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
         */
    }
}