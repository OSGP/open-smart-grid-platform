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

import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationResponse;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformDomain;
import com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.PlatformFunctionGroup;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.admin.devicemanagement.AdminDeviceManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class ChangeOrganizationSteps {

	@Autowired
	private AdminDeviceManagementClient client;

	/**
	 * Send a update organization request to the Platform
	 * 
	 * @param requestParameters
	 *            An list with request parameters for the request.
	 * @throws Throwable
	 */
	@When("^receiving an update organization request$")
	public void receiving_an_update_organization_request(Map<String, String> requestSettings) throws Throwable {
		ChangeOrganisationRequest request = new ChangeOrganisationRequest();
		request.setOrganisationIdentification(getString(requestSettings, Keys.KEY_ORGANIZATION_IDENTIFICATION,
				Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
		request.setNewOrganisationName(
				getString(requestSettings, Keys.KEY_NAME, Defaults.DEFAULT_NEW_ORGANIZATION_NAME));
		request.setNewOrganisationIdentification(getString(requestSettings, Keys.KEY_NEW_ORGANIZATION_IDENTIFICATION,
				Defaults.DEFAULT_NEW_ORGANIZATION_IDENTIFICATION));
		request.setNewOrganisationPlatformFunctionGroup(
				getEnum(requestSettings, Keys.KEY_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP, PlatformFunctionGroup.class,
						Defaults.DEFAULT_NEW_ORGANIZATION_PLATFORMFUNCTIONGROUP));
		
		request.getNewOrganisationPlatformDomains().clear();
		for (String platformDomain : getString(requestSettings, Keys.KEY_DOMAINS, Defaults.DEFAULT_DOMAINS).split(";")) {
			request.getNewOrganisationPlatformDomains().add(Enum.valueOf(PlatformDomain.class, platformDomain));
		}

		try {
			ScenarioContext.Current().put(Keys.RESPONSE, client.changeOrganization(request));
		} catch (SoapFaultClientException ex) {
			ScenarioContext.Current().put(Keys.RESPONSE, ex);
		}
	}

	@Then("^the update organization response is successfull$")
	public void the_update_organization_response_is_successfull() throws Throwable {
		Assert.assertTrue(ScenarioContext.Current().get(Keys.RESPONSE) instanceof ChangeOrganisationResponse);
	}
}