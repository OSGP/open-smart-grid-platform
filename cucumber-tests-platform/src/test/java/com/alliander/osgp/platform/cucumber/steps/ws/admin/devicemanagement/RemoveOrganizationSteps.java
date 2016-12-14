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
 * Class with all the remove organization requests steps
 */
public class RemoveOrganizationSteps extends AdminStepsBase {
    
    private static final String TEST_SUITE = "DeviceManagement";
    private static final String TEST_CASE_NAME = "AT Remove an organization";
    private static final String TEST_CASE_NAME_REQUEST = "RemoveOrganization";

    /**
     * Send a remove organization request to the Platform.
     * @param requestParameter An list with request parameters for the request.
     * @throws Throwable
     */
    @When("^receiving a remove organization request$")
    public void receiving_a_remove_organization_request(Map<String, String> requestParameters) throws Throwable {

        // Required parameters
        PROPERTIES_MAP.put("__ORGANIZATION_IDENTIFICATION__", requestParameters.get(Keys.KEY_ORGANISATION_IDENTIFICATION));
    
        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }
    
    /**
     * Verify that the create organization response is successful.
     * @throws Throwable
     */
    @Then("^the remove organization response is successfull$")
    public void the_remove_organization_response_is_successfull() throws Throwable {
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, "/Envelope/Body/RemoveOrganisationResponse", ""));
    }

    /**
     * Verify the remove organization response 
     * @param arg1
     * @throws Throwable
     */
    @Then("^the remove organization response contains$")
    public void the_remove_organization_response_contains(final Map<String, String> expectedResult) throws Throwable {
        ResponseSteps.VerifyFaultResponse(this.runXpathResult, this.response, expectedResult);
    }
}