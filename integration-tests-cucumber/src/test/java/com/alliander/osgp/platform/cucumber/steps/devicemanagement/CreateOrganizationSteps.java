/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.devicemanagement;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.platform.cucumber.SoapUiRunner;
import com.alliander.osgp.platform.cucumber.steps.common.ResponseSteps;

/**
 * Class with all the create organization requests steps
 */
@CucumberOptions(features = "src/test/resources/osg-adapter-ws-admin/OrganizationManagement.feature",
	glue = "com.alliander.osgp.platform.cucumber",
	format = {"pretty"})
public class CreateOrganizationSteps extends SoapUiRunner {
    
    private static final String TEST_SUITE_XML = "DeviceManagement";
    private static final String TEST_CASE_XML = "Create a new organization";
    private static final String TEST_CASE_NAME_REQUEST = "CreateOrganization";
    private static final String TEST_CASE_UNAUTHORIZED_NAME_REQUEST = "CreateOrganizationAsUnAuthorizedOrganization";

    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();
    
    /**
     * 
     * @param requestParameters
     */
    private void fillPropertiesMap(Map<String, String> requestParameters) {
		PROPERTIES_MAP.put("__ORGANIZATION_IDENTIFICATION__", requestParameters.get("OrganizationIdentification"));
		PROPERTIES_MAP.put("__NAME__", requestParameters.get("Name"));
		PROPERTIES_MAP.put("__PREFIX__", requestParameters.get("Prefix"));
		PROPERTIES_MAP.put("__FUNCTIONGROUP__", requestParameters.get("FunctionGroup"));
		PROPERTIES_MAP.put("__ENABLED__", requestParameters.get("Enabled").toLowerCase());
		PROPERTIES_MAP.put("__DOMAINS__", requestParameters.get("Domains"));
    }
    
    /**
     * 
     * @throws Throwable
     */
	@When("^receiving a create organization request$")
	public void receiving_a_create_organization_request(Map<String, String> requestParameters) throws Throwable {

		this.fillPropertiesMap(requestParameters);
		
		this.requestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
	}
	
	/**
     * 
     * @throws Throwable
     */
	@When("^receiving a create organization request as an unauthorized organization$")
	public void receiving_a_create_organization_request_as_an_unauthorized_organization(Map<String, String> requestParameters) throws Throwable {

		this.fillPropertiesMap(requestParameters);
	
		this.requestRunner(PROPERTIES_MAP, TEST_CASE_UNAUTHORIZED_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
	}
	
	/**
	 * Verify that the create organization response is successful.
	 * @throws Throwable
	 */
	@Then("^the create organization response is successfull$")
	public void the_create_organization_response_is_successfull() throws Throwable {
		Assert.assertTrue(this.runXpathResult.assertXpath(this.response, "/Envelope/Body/CreateOrganisationResponse", ""));
	}
	
	/**
	 * Verify that the create organization response contains the fault with the given expectedResult parameters.
	 * @param expectedResult
	 * @throws Throwable
	 */
	@Then("^the create organization response contains$")
	public void the_create_organization_response_contains(Map<String, String> expectedResult) throws Throwable {
		ResponseSteps.VerifyFaultResponse(this.runXpathResult, this.response, expectedResult);
	}
}