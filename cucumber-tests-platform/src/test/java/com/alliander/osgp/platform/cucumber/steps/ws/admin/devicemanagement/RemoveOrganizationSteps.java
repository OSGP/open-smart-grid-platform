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

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.GenericResponseSteps;
import com.alliander.osgp.platform.cucumber.support.ws.admin.devicemanagement.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class RemoveOrganizationSteps {
    
	@Autowired
	private AdminDeviceManagementClient client;

    /**
     * Send a remove organization request to the Platform.
     * @param requestParameter An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a remove organization request$")
    public void receiving_a_remove_organization_request(Map<String, String> requestSettings) throws Throwable {

    	RemoveOrganisationRequest request = new RemoveOrganisationRequest();
        request.setOrganisationIdentification(getString(requestSettings, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    	
        try {
            ScenarioContext.Current().put(Keys.RESPONSE, client.removeOrganization(request));
        } catch (SoapFaultClientException e){
            ScenarioContext.Current().put(Keys.RESPONSE, e);        	
        }
    }
    
    /**
     * Verify that the create organization response is successful.
     * @throws Throwable
     */
    @Then("^the remove organization response is successfull$")
    public void the_remove_organization_response_is_successfull() throws Throwable {
    	Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof RemoveOrganisationResponse);
    }

    /**
     * Verify the remove organization response 
     * @param arg1
     * @throws Throwable
     */
    @Then("^the remove organization response contains$")
    public void the_remove_organization_response_contains(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.VerifySoapFault(expectedResult);
    }
}