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

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.platform.cucumber.config.CorePersistenceConfig;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.publiclighting.PublicLightingAdHocManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set light requests steps
 */
public class ResumeScheduleSteps {
	@Autowired
	private CorePersistenceConfig configuration;
	
	@Autowired
	private PublicLightingAdHocManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeScheduleSteps.class);
    
    /**
     * Sends a Resume Schedule request to the platform for a given device identification.
     * @param requestParameters The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a resume schedule request$")
    public void whenReceivingAResumeScheduleRequest(final Map<String, String> requestParameters) throws Throwable {

    	ResumeScheduleRequest request = new ResumeScheduleRequest();
    	request.setDeviceIdentification(getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    	request.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
    	request.setIsImmediate(getBoolean(requestParameters, Keys.KEY_ISIMMEDIATE, Defaults.DEFAULT_ISIMMEDIATE));
    	
    	try {
    		ScenarioContext.Current().put(Keys.RESPONSE, client.resumeScheduleStatus(request));
    	} catch(SoapFaultClientException ex) {
    		ScenarioContext.Current().put(Keys.RESPONSE, ex);
    	}
    }
    
    @When("^receiving a set resume schedule by an unknown organization$")
    public void receivingASetResumeScheduleByAnUnknownOrganization(final Map<String, String> requestParameters) throws Throwable {
        // Force the request being send to the platform as a given organization.
    	ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

    	whenReceivingAResumeScheduleRequest(requestParameters);
    }
    
    /**
     * The check for the response from the Platform.
     * @param expectedResponseData The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the resume schedule async response contains$")
    public void thenTheResumeScheduleAsyncResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
    	ResumeScheduleAsyncResponse response = (ResumeScheduleAsyncResponse)ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
    	Assert.assertEquals(getString(expectedResponseData,  Keys.KEY_DEVICE_IDENTIFICATION), response.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

     	LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the platform buffers a resume schedule response message for device \"([^\"]*)\"$")
    public void thenThePlatformBuffersAResumeScheduleResponseMessage(final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
    	ResumeScheduleAsyncRequest request = new ResumeScheduleAsyncRequest();
    	AsyncRequest asyncRequest = new AsyncRequest();
    	asyncRequest.setDeviceId(deviceIdentification);
    	asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
    	request.setAsyncRequest(asyncRequest);
    	
    	boolean success = false;
    	int count = 0;
    	while (!success) {
    		if (count > configuration.getDefaultTimeout()) {
    			Assert.fail("Timeout");
    		}
    		
    		count++;
    		
    		try {
    			ResumeScheduleResponse response = client.getResumeScheduleResponse(request);
    			
    			Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)), response.getResult());
    			
    			success = true; 
    		}
    		catch(Exception ex) {
    			// Do nothing
    		}
    	}
    }
    
    @Then("^the resume schedule async response contains a soap fault$")
    public void theResumeScheduleAsyncResponseContainsASoapFault(final Map<String, String> expectedResult) {
    	SoapFaultClientException response = (SoapFaultClientException)ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertEquals(expectedResult.get(Keys.KEY_MESSAGE), response.getMessage());
    }
}