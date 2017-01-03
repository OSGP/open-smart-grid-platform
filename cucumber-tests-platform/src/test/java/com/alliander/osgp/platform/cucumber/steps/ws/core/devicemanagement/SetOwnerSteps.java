/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.core.devicemanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.core.CoreDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetOwnerSteps {

    @Autowired
    private CoreDeviceManagementClient client;
    
	@When("^receiving a set owner request(?: over OSGP)?$")
    public void receiving_a_find_devices_request(final Map<String, String> requestParameters) throws Throwable
    {
    	SetOwnerRequest request = new SetOwnerRequest();
    	
    	request.setDeviceIdentification(getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    	request.setOrganisationIdentification(getString(requestParameters, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    	
    	ScenarioContext context = ScenarioContext.Current();
    	context.put(Keys.RESPONSE, client.setOwner(request));
    	ScenarioContext.Current().put(Keys.RESPONSE, client.setOwner(request));
    }
    
    @Then("^the set owner async response contains$")
    public void the_find_devices_response_contains_devices(final Map<String, String> expectedDevice) throws Throwable
    {
    	SetOwnerResponse response = (SetOwnerResponse) ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	Assert.assertEquals(getString(expectedDevice, Keys.KEY_ORGANIZATION_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION), response);
    }

    @Then("^the owner of device \"([^\"]*)\" has been changed$")
    public void the_find_devices_response_contains_at_index(final String deviceIdentification) throws Throwable
    {
    	SetOwnerResponse response = (SetOwnerResponse) ScenarioContext.Current().get(Keys.RESPONSE);
    	
    	System.out.println(response);
    }
}