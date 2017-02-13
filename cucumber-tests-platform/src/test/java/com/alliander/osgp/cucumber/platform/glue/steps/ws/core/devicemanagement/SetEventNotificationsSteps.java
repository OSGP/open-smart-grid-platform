/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventNotificationType;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.SetEventNotificationsResponse;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class SetEventNotificationsSteps extends GlueBase {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(SetEventNotificationsSteps.class);
    
	@Autowired
	private CoreDeviceConfiguration configuration;

	@Autowired
    private CoreDeviceManagementClient client;
    
    /**
     * Send an event notification request to the Platform
     * @param requestParameters An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a set event notification message request(?: on OSGP)?$")
    public void receivingASetEventNotificationMessageRequest(final Map<String, String> requestParameters) throws Throwable
    {
    	SetEventNotificationsRequest request = new SetEventNotificationsRequest();
    	request.setDeviceIdentification(getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    	for (String event : getString(requestParameters, Keys.KEY_EVENT).split(",")){
        	request.getEventNotifications().add(Enum.valueOf(EventNotificationType.class, event.trim()));
    	}
    	
    	try {
    		ScenarioContext.Current().put(Keys.RESPONSE, client.setEventNotifications(request));
    	} catch(SoapFaultClientException ex) {
    		ScenarioContext.Current().put(Keys.RESPONSE, ex);
    	}
    }
    
    /**
     * The check for the response from the Platform.
     * @param expectedResponseData The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the set event notification async response contains$")
    public void theSetEventNotificationAsyncResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {
    	SetEventNotificationsAsyncResponse response = (SetEventNotificationsAsyncResponse)ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
    	Assert.assertEquals(getString(expectedResponseData,  Keys.KEY_DEVICE_IDENTIFICATION), response.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }
    
    @Then("^the platform buffers a set event notification response message for device \"([^\"]*)\"")
    public void thePlatformBuffersASetEventNotificationResponseMessageForDevice(final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable
    {
    	SetEventNotificationsAsyncRequest request = new SetEventNotificationsAsyncRequest();
    	AsyncRequest asyncRequest = new AsyncRequest();
    	asyncRequest.setDeviceId(deviceIdentification);
    	asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
    	request.setAsyncRequest(asyncRequest);
    	
    	boolean success = false;
    	int count = 0;
    	while (!success) {
    		if (count > configuration.getTimeout()) {
    			Assert.fail("Timeout");
    		}
    		
    		count++;
            Thread.sleep(1000);

    		try {
    			SetEventNotificationsResponse response = client.getSetEventNotificationsResponse(request);
    			    			
    			Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)), response.getResult());
    			
    			success = true; 
    		}
    		catch(Exception ex) {
    			LOGGER.debug(ex.getMessage());
    		}
    	}
    }
}