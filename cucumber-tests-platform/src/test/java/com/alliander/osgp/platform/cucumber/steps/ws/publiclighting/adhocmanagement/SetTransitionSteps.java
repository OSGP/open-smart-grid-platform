/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.publiclighting.adhocmanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.platform.cucumber.config.CoreDeviceConfiguration;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.publiclighting.PublicLightingAdHocManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set light requests steps
 */
public class SetTransitionSteps {
	@Autowired
	private CoreDeviceConfiguration configuration;
	
	@Autowired
	private PublicLightingAdHocManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(SetTransitionSteps.class);

    /**
     * Sends a Get Status request to the platform for a given device identification.
     * @param requestParameters The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set transition request$")
    public void receivingASetTransitionRequest(final Map<String, String> requestParameters) throws Throwable {

    	SetTransitionRequest request = new SetTransitionRequest();
    	request.setDeviceIdentification(getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

    	if (requestParameters.containsKey(Keys.KEY_TRANSITION_TYPE) && !requestParameters.get(Keys.KEY_TRANSITION_TYPE).isEmpty()) {
        	request.setTransitionType(getEnum(requestParameters, Keys.KEY_TRANSITION_TYPE, TransitionType.class, Defaults.DEFAULT_TRANSITION_TYPE));
    	}
    	
    	if (requestParameters.containsKey(Keys.KEY_TIME) && !requestParameters.get(Keys.KEY_TIME).isEmpty()) {
        	GregorianCalendar gcal = new GregorianCalendar();
        	gcal.add(Calendar.HOUR, Integer.parseInt(requestParameters.get(Keys.KEY_TIME).substring(0, 2)));
        	gcal.add(Calendar.MINUTE, Integer.parseInt(requestParameters.get(Keys.KEY_TIME).substring(2, 4)));
        	gcal.add(Calendar.SECOND, Integer.parseInt(requestParameters.get(Keys.KEY_TIME).substring(4, 6)));
            XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
    		request.setTime(xgcal);
    	}
    	
    	try {
    		ScenarioContext.Current().put(Keys.RESPONSE, client.setTransition(request));
    	} catch(SoapFaultClientException ex) {
    		ScenarioContext.Current().put(Keys.RESPONSE, ex);
    	} 
    }
    
    @When("^receiving a set transition request by an unknown organization$")
    public void receivingASetTransitionRequestByAnUnknownOrganization(final Map<String, String> requestParameters) throws Throwable {
        // Force the request being send to the platform as a given organization.
    	ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");
    	
    	receivingASetTransitionRequest(requestParameters);
    }
    
    /**
     * The check for the response from the Platform.
     * @param expectedResponseData The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the set transition async response contains$")
    public void theSetTransitionAsyncResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
    	SetTransitionAsyncResponse response = (SetTransitionAsyncResponse)ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
    	Assert.assertEquals(getString(expectedResponseData,  Keys.KEY_DEVICE_IDENTIFICATION), response.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

     	LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the platform buffers a set transition response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersASetTransitionResponseMessage(final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
    	SetTransitionAsyncRequest request = new SetTransitionAsyncRequest();
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
    			SetTransitionResponse response = client.getSetTransitionResponse(request);
    			
    			Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)), response.getResult());
    			
    			success = true; 
    		}
    		catch(Exception ex) {
    			// Do nothing
    		}
    	}
    }
    
    @Then("^the set transition async response contains a soap fault$")
    public void theSetTransitionAsyncResponseContainsASoapFault(final Map<String, String> expectedResult) {
    	SoapFaultClientException response = (SoapFaultClientException)ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertEquals(expectedResult.get(Keys.KEY_MESSAGE), response.getMessage());
    }
}