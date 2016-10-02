/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
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

    @When("^a bundled request message is received$")
    public void aBundledRequestMessageIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, ScenarioContext.Current().get("DeviceIdentification", Defaults.DEFAULT_DEVICE_IDENTIFICATION).toString());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, ScenarioContext.Current().get("OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION).toString());

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
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().get("CorrelationUid").toString());
        PROPERTIES_MAP.put(MAX_TIME, "180000");

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);

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
