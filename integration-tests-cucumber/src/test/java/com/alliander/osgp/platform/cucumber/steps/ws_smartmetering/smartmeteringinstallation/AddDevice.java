/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.smartmeteringinstallation;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.hooks.AddDeviceHooks;
import com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.smartmeteringmonitoring.ActualMeterReadsGas;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AddDevice extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/AddDeviceResponse/Result/text()";
    private static final String PATH_DEVICE_IDENTIFICATION = "/Envelope/Body/AddDeviceAsyncResponse/DeviceIdentification/text()";
    private static final String PATH_CORRELATION_UID = "/Envelope/Body/AddDeviceAsyncResponse/CorrelationUid/text()";
    
    private static final String XPATH_MATCHER_RESULT = "OK";
    
    private static final String TEST_SUITE_XML = "SmartmeterInstallation";
    private static final String TEST_CASE_XML = "218 Retrieve AddDevice result";
    private static final String TEST_CASE_NAME_REQUEST = "AddDevice - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetAddDeviceResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsGas.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private AddDeviceHooks addDeviceHooks;

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
    	    getString(expectedResponseData, "OrganizationIdentification", "test-org"));
    }
	
    @Then("^receiving an get add device response request$")
    public void receiving_an_get_add_device_response_request(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().Data.get("CorrelationUid").toString());

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);
    }

    @Then("^the get add device request response should be ok$")
    public void the_get_add_device_request_response_should_be_ok() throws Throwable {
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
    }

    @And("^the device with id \"([^\"]*)\" should be added in the core database$")
    public void theDeviceShouldBeAddedInTheCoreDatabase(final String deviceId) throws Throwable {
        Assert.assertTrue(this.addDeviceHooks.testCoreDevice(deviceId));
    }

    @And("^the device with id \"([^\"]*)\" should be added in the dlms database$")
    public void theDeviceShouldBeAddedInTheDlmsDatabase(final String deviceId) throws Throwable {
        Assert.assertTrue(this.addDeviceHooks.testDlmsDevice(deviceId));
    }

}
