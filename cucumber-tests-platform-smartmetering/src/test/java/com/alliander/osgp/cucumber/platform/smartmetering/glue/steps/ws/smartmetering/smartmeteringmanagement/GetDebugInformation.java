/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.builders.logging.DeviceLogItemBuilder;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetDebugInformation extends SmartMeteringStepsBase {

    private static final String TEST_SUITE_XML = "SmartmeterManagement";
    private static final String TEST_CASE_XML = "628 Get debug information";
    private static final String TEST_CASE_NAME_REQUEST = "FindMessageLogs";
    private static final String TEST_CASE_NAME_RESPONSE = "GetFindMessageLogsResponse";
    private static final String PATH_RESULT_DECODED_MESSAGE = "/Envelope/Body/FindMessageLogsResponse/MessageLogPage/MessageLogs/DecodedMessage/text()";

    private static final String XPATH_MATCHER_RESULT_DECODED_MESSAGE = ".*";

    @Autowired
    private DeviceLogItemRepository logItemRepository;

    @Autowired
    private DeviceLogItemBuilder deviceLogItemBuilder;

    @Given("^there is debug information logged for the device$")
    public void thereIsDebugInformationLoggedForTheDevice() throws Throwable {
        final DeviceLogItem item = this.deviceLogItemBuilder
                .withDeviceIdentification(ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION).toString())
                .build();

        this.logItemRepository.save(item);
    }

    @When("^the get debug information request is received$")
    public void theGetDebugInformationRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                getString(requestData, PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_PAGE, getString(requestData, PlatformKeys.KEY_PAGE, PlatformDefaults.DEFAULT_PAGE.toString()));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the device debug information should be in the response message$")
    public void theDeviceDebugInformationShouldBeInTheResponseMessage() throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, TEST_CASE_XML, TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_DECODED_MESSAGE,
                XPATH_MATCHER_RESULT_DECODED_MESSAGE));
    }

}
