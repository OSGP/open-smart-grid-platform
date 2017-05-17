/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Bundle extends SmartMeteringStepsBase {
    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "484 Handle a bundle of requests";
    private static final String TEST_CASE_NAME_REQUEST = "Bundle - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetBundleResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(Bundle.class);
    private static final List<String> REQUEST_ACTIONS = new ArrayList<>();

    private static final Map<String, String> REQUEST_RESPONSE_PAIRS = new HashMap<>();
    static {
        REQUEST_RESPONSE_PAIRS.put("FindEventsRequest", "FindEventsResponse");
        REQUEST_RESPONSE_PAIRS.put("SetSpecialDaysRequest", "ActionResponse");
        REQUEST_RESPONSE_PAIRS.put("GetSpecificAttributeValueRequest", "ActionResponse");
        REQUEST_RESPONSE_PAIRS.put("ReadAlarmRegisterRequest", "ReadAlarmRegisterResponse");
        REQUEST_RESPONSE_PAIRS.put("SetAdministrativeStatusRequest", "ActionResponse");
        REQUEST_RESPONSE_PAIRS.put("GetActualMeterReadsRequest", "ActualMeterReadsResponse");
        REQUEST_RESPONSE_PAIRS.put("GetAdministrativeStatusRequest", "AdministrativeStatusResponse");
        REQUEST_RESPONSE_PAIRS.put("GetPeriodicMeterReadsRequest", "PeriodicMeterReadsResponse");
        REQUEST_RESPONSE_PAIRS.put("SetActivityCalendarRequest", "ActionResponse");
        REQUEST_RESPONSE_PAIRS.put("SetAlarmNotificationsRequest", "ActionResponse");
        REQUEST_RESPONSE_PAIRS.put("SetConfigurationObjectRequest", "ActionResponse");
        REQUEST_RESPONSE_PAIRS.put("SetPushSetupAlarmRequest", "ActionResponse");
        REQUEST_RESPONSE_PAIRS.put("SynchronizeTimeRequest", "ActionResponse");
    }

    @When("^a bundled request message is received$")
    public void aBundledRequestMessageIsReceived(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION, PlatformSmartmeteringDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformSmartmeteringKeys.KEY_ORGANIZATION_IDENTIFICATION, getString(settings,
                PlatformSmartmeteringKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformSmartmeteringDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);

        final NodeList nodeList = this.runXpathResult.getNodeList(this.request, "//Actions/*");
        for (int nodeId = 0; nodeId < nodeList.getLength(); nodeId++) {
            REQUEST_ACTIONS.add(this.removeNamespace(nodeList.item(nodeId).getNodeName()));
        }
    }

    @And("^the operations in the bundled request message will be executed from top to bottom$")
    public void theRequestsInTheBundledRequestMessageWillBeExecutedFromTopToBottom(final Map<String, String> settings)
            throws Throwable {
        PROPERTIES_MAP.put(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION, PlatformSmartmeteringDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                ScenarioContext.current().get(PlatformSmartmeteringKeys.KEY_CORRELATION_UID).toString());
        PROPERTIES_MAP.put(MAX_TIME, "180000");

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);

        LOGGER.debug("check if responses are in the correct order:");
        final NodeList nodeList = this.runXpathResult.getNodeList(this.response, "//AllResponses/*");
        for (int nodeId = 0; nodeId < nodeList.getLength(); nodeId++) {
            final String responseAction = this.removeNamespace(nodeList.item(nodeId).getNodeName());
            final String matchingResponse = REQUEST_RESPONSE_PAIRS.get(REQUEST_ACTIONS.get(nodeId));
            assertEquals("the matchingResponse should equals responseAction", matchingResponse, responseAction);
        }
    }

    @Then("^a bundled response message will contain the response from all the operations$")
    public void aBundledResponseMessageWillContainTheResponseFromAllTheRequests() throws Throwable {
        LOGGER.debug("check if we get responses for all the requests");
        final NodeList nodeList = this.runXpathResult.getNodeList(this.response, "//AllResponses/*");

        assertEquals("the number of request actions should equals the nr nodelists", REQUEST_ACTIONS.size(),
                nodeList.getLength());
    }

    private String removeNamespace(final String input) {
        return input.split(":")[1];
    }
}
