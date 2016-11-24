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
import org.xml.sax.SAXException;

import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.common.ResponseSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.admin.AdminStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the remove organization requests steps
 */
public class ChangeOrganizationSteps extends AdminStepsBase {
    
    private static final String TEST_SUITE = "DeviceManagement";
    private static final String TEST_CASE_NAME = "ChangeOrganisation TestCase";
    private static final String TEST_CASE_NAME_REQUEST = "ChangeOrganisation";

    /**
     * 
     * @param requestParameters
     */
    private void fillPropertiesMap(Map<String, String> requestParameters) {
        PROPERTIES_MAP.put("__ORGANIZATION_IDENTIFICATION__", requestParameters.get(Keys.KEY_ORGANIZATION_IDENTIFICATION));
        PROPERTIES_MAP.put("__NEW_ORGANIZATION_IDENTIFICATION__", requestParameters.get(Keys.KEY_ORGANIZATION_IDENTIFICATION));
        PROPERTIES_MAP.put("__NAME__", requestParameters.get("Name"));
        PROPERTIES_MAP.put("__FUNCTION_GROUP__", requestParameters.get("FunctionGroup"));
        PROPERTIES_MAP.put("__DOMAINS__", requestParameters.get("Domains"));
    }
    
    /**
     * Send a update organization request to the Platform
     * @param requestParameters An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving an update organization request$")
    public void receiving_an_update_organization_request(Map<String, String> requestParameters) throws Throwable
    {
    	// Required parameters
    	//PROPERTIES_MAP.put("__ORGANIZATION_IDENTIFICATION__", requestParameters.get(Keys.KEY_ORGANIZATION_IDENTIFICATION));
    	
    	fillPropertiesMap(requestParameters);
    	
    	this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    @Then("^the update organization response is successfull$")
    public void the_update_organization_response_is_successfull() throws Throwable
    {
    	Assert.assertTrue(this.runXpathResult.assertXpath(this.response, "/Envelope/Body/UpdateOrganisationResponse", ""));
    }
    
    @Then("^the update organization response contains$")
    public void the_update_organization_response_contains(final Map<String, String> expectedResult) throws Throwable
    {
    	ResponseSteps.VerifyFaultResponse(this.runXpathResult, this.response, expectedResult);
    }
}