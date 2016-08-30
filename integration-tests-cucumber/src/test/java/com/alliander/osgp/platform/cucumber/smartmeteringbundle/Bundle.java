/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.platform.cucumber.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.NodeList;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.alliander.osgp.platform.cucumber.support.ServiceEndpoint;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Bundle extends SmartMetering {
    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "484 Handle a bundle of requests";
    private static final String TEST_CASE_NAME_REQUEST = "Bundle - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetBundleResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(Bundle.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();
    private static final List<String> REQUEST_ACTIONS = new ArrayList<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @When("^a bundled request message is received$")
    public void aBundledRequestMessageIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);

        final NodeList nodeList = this.runXpathResult.getNodeList(this.request, "//Actions/*");
        for (int nodeId = 0; nodeId < nodeList.getLength(); nodeId++) {
            REQUEST_ACTIONS.add(this.formatAction(nodeList.item(nodeId).getNodeName(), "Request"));
        }
    }

    /**
     * An action is in the node list in the form of <ns1:FindEventsRequest> or
     * <ns2:FindEventsResponseData> We want to loose the namespace and postfix
     */
    private String formatAction(final String input, final String postfix) {
        final String request = input.split(":")[1];
        if (request.indexOf(postfix) > 0) {
            return request.substring(0, request.indexOf(postfix));
        } else {
            return request;
        }

    }

    @And("^the operations in the bundled request message will be executed from top to bottom$")
    public void theRequestsInTheBundledRequestMessageWillBeExecutedFromTopToBottom() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        PROPERTIES_MAP.put(MAX_TIME, "180000");

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        LOGGER.debug("check if responses are in the correct order:");
        final NodeList nodeList = this.runXpathResult.getNodeList(this.response, "//AllResponses/*");
        for (int nodeId = 0; nodeId < nodeList.getLength(); nodeId++) {

            final String responseAction = this.formatAction(nodeList.item(nodeId).getNodeName(), "ResponseData");
            LOGGER.debug("requestAction:{}", REQUEST_ACTIONS.get(nodeId));
            LOGGER.debug("responseAction:{}", responseAction);

            // For several requests we get an ActionResponseData tag, instead of
            // a tag with the name of the action.
            // Other requests miss "Read" or "Get" if we compare them to the
            // input action. We will allow those situations.
            assertTrue("Action".equals(responseAction) || REQUEST_ACTIONS.get(nodeId).equals(responseAction)
                    || REQUEST_ACTIONS.get(nodeId).equals("Read" + responseAction)
                    || REQUEST_ACTIONS.get(nodeId).equals("Get" + responseAction));
        }
    }

    @Then("^a bundled response message will contain the response from all the operations$")
    public void aBundledResponseMessageWillContainTheResponseFromAllTheRequests() throws Throwable {
        LOGGER.debug("check if we get responses for all the requests");
        final NodeList nodeList = this.runXpathResult.getNodeList(this.response, "//AllResponses/*");

        for (int nodeId = 0; nodeId < nodeList.getLength(); nodeId++) {
            LOGGER.debug("requestAction:{}", REQUEST_ACTIONS.get(nodeId));
            final int childCount = nodeList.item(nodeId).getChildNodes().getLength();
            LOGGER.debug("responseValue has {} children", childCount);
            assertTrue(childCount > 0);
        }

    }
}
