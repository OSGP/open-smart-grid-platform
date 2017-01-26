/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.ws.publiclighting.adhocmanagement;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getEnum;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;
import static com.alliander.osgp.automatictests.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.EventNotificationType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.LightType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.LinkType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.automatictests.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.support.ws.publiclighting.PublicLightingAdHocManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set light requests steps
 */
public class GetStatusSteps {

	@Autowired
	private CoreDeviceConfiguration configuration;
	
	@Autowired
	private PublicLightingAdHocManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetStatusSteps.class);

    /**
     * Sends a Get Status request to the platform for a given device identification.
     * @param requestParameters The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a get status request$")
    public void receivingAGetStatusRequest(final Map<String, String> requestParameters) throws Throwable {

    	GetStatusRequest request = new GetStatusRequest();
    	request.setDeviceIdentification(getString(requestParameters, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));
    	
    	try {
    		ScenarioContext.Current().put(Keys.RESPONSE, client.getStatus(request));
    	} catch(SoapFaultClientException ex) {
    		ScenarioContext.Current().put(Keys.RESPONSE, ex);
    	}
    }
    
    @When("^receiving a get status request by an unknown organization$")
    public void receivingAGetStatusRequestByAnUnknownOrganization(final Map<String, String> requestParameters) throws Throwable {
        // Force the request being send to the platform as a given organization.
    	ScenarioContext.Current().put(Keys.ORGANIZATION_IDENTIFICATION, "unknown-organization");
    	
    	receivingAGetStatusRequest(requestParameters);
    }
    
    /**
     * The check for the response from the Platform.
     * @param expectedResponseData The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the get status async response contains$")
    public void theGetStatusAsyncResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        
    	GetStatusAsyncResponse response = (GetStatusAsyncResponse)ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
    	Assert.assertEquals(getString(expectedResponseData,  Keys.DEVICE_IDENTIFICATION), response.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.ORGANIZATION_IDENTIFICATION, Defaults.ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.CORRELATION_UID) + "]");
    }

    @Then("^the get status response contains soap fault$")
    public void theGetStatusResponseContainsSoapFault(final Map<String, String> expectedResponseData) {
    	SoapFaultClientException response = (SoapFaultClientException)ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertEquals(expectedResponseData.get(Keys.MESSAGE), response.getMessage());
    }

    @Then("^the platform buffers a get status response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetStatusResponseMessageForDevice(final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
    	GetStatusAsyncRequest request = new GetStatusAsyncRequest();
    	AsyncRequest asyncRequest = new AsyncRequest();
    	asyncRequest.setDeviceId(deviceIdentification);
    	asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.CORRELATION_UID));
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
    			GetStatusResponse response = client.getGetStatusResponse(request);
    			
    			Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.RESULT)), response.getResult());
    			
    			DeviceStatus deviceStatus = response.getDeviceStatus();
    			
    			Assert.assertEquals(getEnum(expectedResult, Keys.PREFERRED_LINKTYPE, LinkType.class), deviceStatus.getPreferredLinkType());
    			Assert.assertEquals(getEnum(expectedResult, Keys.ACTUAL_LINKTYPE, LinkType.class), deviceStatus.getActualLinkType());
       			Assert.assertEquals(getEnum(expectedResult, Keys.LIGHTTYPE, LightType.class), deviceStatus.getLightType());
       			
       			if (expectedResult.containsKey(Keys.EVENTNOTIFICATIONTYPES) && !expectedResult.get(Keys.EVENTNOTIFICATIONTYPES).isEmpty()) {
           			Assert.assertEquals(getString(expectedResult,  Keys.EVENTNOTIFICATIONS, Defaults.EVENTNOTIFICATIONS).split(Keys.SEPARATOR).length, deviceStatus.getEventNotifications().size());
           			for (String eventNotification : getString(expectedResult,  Keys.EVENTNOTIFICATIONS, Defaults.EVENTNOTIFICATIONS).split(Keys.SEPARATOR)) {
               			Assert.assertTrue(deviceStatus.getEventNotifications().contains(Enum.valueOf(EventNotificationType.class, eventNotification)));
           			}
       			}
       			
       			if (expectedResult.containsKey(Keys.LIGHTVALUES) && !expectedResult.get(Keys.LIGHTVALUES).isEmpty()) {
               		Assert.assertEquals(getString(expectedResult,  Keys.LIGHTVALUES, Defaults.LIGHTVALUES).split(Keys.SEPARATOR).length, deviceStatus.getLightValues().size());
	           		for (String lightValues : getString(expectedResult, Keys.LIGHTVALUES, Defaults.LIGHTVALUES).split(Keys.SEPARATOR)) {
	           			
	       				String[] parts = lightValues.split(Keys.SEPARATOR_SEMICOLON);
	       				Integer index = Integer.parseInt(parts[0]);
	       				Boolean on = Boolean.parseBoolean(parts[1]);
	       				Integer dimValue = Integer.parseInt(parts[2]);
	       				
	           			boolean found = false;
	           			for (LightValue lightValue : deviceStatus.getLightValues()) {
	
	           				if (lightValue.getIndex() == index &&
	           						lightValue.isOn() == on &&
	           						lightValue.getDimValue() == dimValue)  {
	           					found = true;
	           					break;
	           				}
	           			}
	           			
	           			Assert.assertTrue(found);
	           		}
       			}
       		    			
    			success = true; 
    		}
    		catch(Exception ex) {
    			// Do nothing
    			LOGGER.info(ex.getMessage());
    		}
    	}
    }
}