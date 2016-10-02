/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.smartmeteringinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AddDevice extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/AddDeviceResponse/Result/text()";
    
    private static final String TEST_SUITE_XML = "SmartmeterInstallation";
    private static final String TEST_CASE_XML = "218 Retrieve AddDevice result";
    private static final String TEST_CASE_NAME_REQUEST = "AddDevice - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetAddDeviceResponse - Request 1";

    @When("^receiving an add device request$")
    public void receiving_an_add_device_request(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, requestData.get("DeviceIdentification"));
        PROPERTIES_MAP.put(DEVICE_TYPE_LABEL, requestData.get("DeviceType"));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }
    
    @Then("^the add device response contains$")
    public void the_add_device_request_contains(final Map<String, String> expectedResponseData) throws Throwable {
    	this.runXpathResult.assertXpath(this.response, PATH_DEVICE_IDENTIFICATION, expectedResponseData.get("DeviceIdentification"));
    	this.runXpathResult.assertNotNull(this.response, PATH_CORRELATION_UID);

    	// Save the returned CorrelationUid in the Scenario related context for further use.
    	saveCorrelationUidInScenarioContext(
    	    this.runXpathResult.getValue(response, PATH_CORRELATION_UID),
    	    getString(expectedResponseData, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }
	
    @Then("^receiving an get add device response request$")
    public void receiving_an_get_add_device_response_request(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().get("CorrelationUid").toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the get add device request response should be ok$")
    public void the_get_add_device_request_response_should_be_ok() throws Throwable {
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, Defaults.EXPECTED_RESULT));
    }
}
