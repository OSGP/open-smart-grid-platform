/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.admin.devicemanagement;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.common.ResponseSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.admin.AdminStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class EventNotificationSteps extends AdminStepsBase {
    
	@Autowired
    private MockOslpServer oslpMockServer;
	
    private static final String TEST_SUITE = "DeviceManagementPortSoap11";
    private static final String TEST_CASE_NAME = "SetEventNotifications";
    private static final String TEST_CASE_NAME_REQUEST = "Request 1";
    
    /**
     * Send an event notification request to the Platform
     * @param requestParameters An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving an event notification message request$")
    public void receiving_an_event_notification_message_request(final Map<String, String> requestParameters) throws Throwable
    {
    	// Required parameters
    	PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get(Keys.KEY_DEVICE_IDENTIFICATION));
    	
    	this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    @Then("^an event notification OSLP message is sent to device \"([^\"]*)\"")
    public void an_event_notification_oslp_message_is_sent_to_device(final String deviceIdentification) throws Throwable
    {
    	final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_LIGHT);
        Assert.assertNotNull(message);
        Assert.assertTrue(message.hasEventNotificationRequest());
    }
    
    @Then("^the platform buffers an event notification response message for device \"([^\"]*)\"")
    public void the_platform_buffers_an_event_notification_response_message_for_device(final String deviceIdentification, final Map<String, String> expectedMessage) throws Throwable
    {
    	// Required parameters
        PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", deviceIdentification);
        
        this.waitForResponse(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST,
        		TEST_CASE_NAME, TEST_SUITE);
    }
    
    // Temporary
    @Then("^the device returns an event notification response over OSLP$")
    public void Temp(final Map<String, String> temp)
    {
    	
    }
}