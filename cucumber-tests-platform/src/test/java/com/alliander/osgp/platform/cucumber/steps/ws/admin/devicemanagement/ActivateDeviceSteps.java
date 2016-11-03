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
import com.alliander.osgp.platform.cucumber.steps.ws.admin.AdminStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ActivateDeviceSteps extends AdminStepsBase {

    private static final String TEST_SUITE = "DeviceManagement";
    private static final String TEST_CASE_NAME = "540 Activate device";
    private static final String TEST_CASE_NAME_REQUEST = "ActivateDevice - Request 1";

    @When("^receiving a activate device request$")
    public void receivingAActivateDeviceRequest(final Map<String, String> requestSettings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION, requestSettings.get(Keys.KEY_DEVICE_IDENTIFICATION));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_NAME, TEST_SUITE);
    }

    /**
     * Verify that the activate device response is successful.
     * @throws Throwable
     */
    @Then("^the activate device response contains$")
    public void theActivateDeviceResponseContains(final Map<String, String> expectedResponse) throws Throwable {
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, "/Envelope/Body/ActivateDeviceResponse/Result/text()", expectedResponse.get("Result")));
    }
}
