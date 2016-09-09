/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws_admin.devicemanagement;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.platform.cucumber.steps.ws_admin.AdminStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DeactivateDeviceSteps extends AdminStepsBase {

    private static final String TEST_SUITE_XML = "DeviceManagement";
    private static final String TEST_CASE_XML = "281 Deactivate device";
    private static final String TEST_CASE_NAME_REQUEST = "DeactivateDevice - Request 1";

    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @When("^receiving a deactivate device request$")
    public void receivingADeactivateDeviceRequest(final Map<String, String> requestSettings) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, requestSettings.get("DeviceIdentification"));
        
        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    /**
	 * Verify that the deactivate device response is successful.
	 * @throws Throwable
	 */
	@Then("^the deactivate device response contains$")
	public void the_deactivate_device_response_contains(Map<String, String> expectedResponse) throws Throwable {
		Assert.assertTrue(this.runXpathResult.assertXpath(this.response, "/Envelope/Body/DeactivateDeviceResponse/Result/text()", expectedResponse.get("Result")));
	}
}
