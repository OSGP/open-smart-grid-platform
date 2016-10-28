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

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.common.ResponseSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.admin.AdminStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class CreateOrganizationSteps extends AdminStepsBase {
    
    protected CreateOrganizationSteps() throws Throwable {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final String TEST_SUITE = "DeviceManagement";
    private static final String TEST_CASE_NAME = "AT Create a new organization";
    private static final String TEST_CASE_NAME_REQUEST = "CreateOrganization";
    private static final String TEST_CASE_UNAUTHORIZED_NAME_REQUEST = "CreateOrganizationAsUnAuthorizedOrganization";

    /**
     * 
     * @param requestParameters
     */
    private void fillPropertiesMap(Map<String, String> requestParameters) {
        PROPERTIES_MAP.put("__ORGANIZATION_IDENTIFICATION__", requestParameters.get(Keys.KEY_ORGANIZATION_IDENTIFICATION));
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
        
        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    /**
     * 
     * @throws Throwable
     */
    @When("^receiving a create organization request as an unauthorized organization$")
    public void receiving_a_create_organization_request_as_an_unauthorized_organization(Map<String, String> requestParameters) throws Throwable {

        this.fillPropertiesMap(requestParameters);
    
        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_UNAUTHORIZED_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
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