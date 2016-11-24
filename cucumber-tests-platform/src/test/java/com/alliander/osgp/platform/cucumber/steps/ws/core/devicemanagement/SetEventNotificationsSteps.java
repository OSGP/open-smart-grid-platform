/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.core.devicemanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.oslp.Oslp.Event;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.core.CoreStepsBase;
import com.alliander.osgp.platform.cucumber.steps.ws.core.firmwaremanagement.GetFirmwareVersionSteps;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class SetEventNotificationsSteps extends CoreStepsBase {
    
	@Autowired
    private MockOslpServer oslpMockServer;
	
    private static final String TEST_SUITE = "DeviceManagement";
    private static final String TEST_CASE_NAME = "SetEventNotifications TestCase";
    private static final String TEST_CASE_NAME_REQUEST = "SetEventNotifications";
    
    private static final String TEST_RESPONSE_CASE_NAME = "GetSetEventNotificationsResponse TestCase";
    private static final String TEST_CASE_NAME_RESPONSE = "GetSetEventNotificationsResponse";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SetEventNotificationsSteps.class);
    
    /**
     * 
     * @param requestParameters
     */
    private void fillPropertiesMap(Map<String, String> requestParameters) {
        PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));
//        PROPERTIES_MAP.put("__EVENT__", requestParameters.get("Event"));
        PROPERTIES_MAP.put("__EVENT__", changeStringToAcceptanceString(requestParameters.get("Event")));
    }
    
    /**
     * 
     * @param event
     * 			A string with one or multiple events
     * @return property
     * 			The xml string which is needed to make multiple events in SoapUI
     */
    private String changeStringToAcceptanceString(String event)
    {
		String property = "";
		java.util.List<String> properties = new java.util.ArrayList<>();
		
		if (event.contains(",")) properties = Arrays.asList(event.split(", "));
		else properties.add(event);
		
		for (String prop : properties)
		{
			if (properties.size() > 1)
			{
				if (property.isEmpty()) property += prop;
				else property += "</ns1:EventNotifications><ns1:EventNotifications>" + prop;
			}
			else
			{
				property = prop;
				break;
			}
		}
		return property;
    }
    
    /**
     * Send an event notification request to the Platform
     * @param requestParameters An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a set event notification message request(?: on OSGP)?$")
    public void receiving_a_set_event_notification_message_request(final Map<String, String> requestParameters) throws Throwable
    {
    	// Required parameters
//    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));
    	fillPropertiesMap(requestParameters);
    	
//    	this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    	this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    /**
     * The check for the response from the Platform.
     * @param expectedResponseData The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the set event notification async response contains$")
    public void the_set_event_notification_async_response_contains(final Map<String, String> expectedResponseData)
            throws Throwable {
        this.runXpathResult.assertXpath(this.response, PATH_DEVICE_IDENTIFICATION,
                getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        this.runXpathResult.assertNotNull(this.response, PATH_CORRELATION_UID);

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }
    
    @Then("^the platform buffers a set event notification response message for device \"([^\"]*)\"")
    public void the_platform_buffers_a_set_event_notification_response_message_for_device(final String deviceIdentification) throws Throwable
    {
    	// Required parameters
        PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", deviceIdentification);
        PROPERTIES_MAP.put("__CORRELATION_UID__", (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        
    	this.waitForResponse(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE,
    			TEST_RESPONSE_CASE_NAME, TEST_SUITE);
    }
}