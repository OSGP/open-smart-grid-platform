/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class EnableAndDisableDebugging extends SmartMeteringStepsBase {

    private static final String TEST_SUITE_XML = "SmartmeterManagement";
    private static final String TEST_CASE_XML = "625 Enable disable debugging";
    private static final String TEST_CASE_NAME_ENABLE_REQUEST = "EnableDebugging";
    private static final String TEST_CASE_NAME_ENABLE_RESPONSE = "GetEnableDebuggingResponse";
    private static final String PATH_RESULT_ENABLE = "/Envelope/Body/EnableDebuggingResponse/Result/text()";

    private static final String TEST_CASE_NAME_DISABLE_REQUEST = "DisableDebugging";
    private static final String TEST_CASE_NAME_DISABLE_RESPONSE = "GetDisableDebuggingResponse";
    private static final String PATH_RESULT_DISABLE = "/Envelope/Body/DisableDebuggingResponse/Result/text()";

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @When("^the enable Debug request is received$")
    public void theEnableDebugRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(requestData, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_ENABLE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);
    }

    @Then("^the device debug information should be enabled$")
    public void theDeviceDebugInformationShouldBeEnabled() throws Throwable {
        final DlmsDevice device = this.dlmsDeviceRepository.findByDeviceIdentification(ScenarioContext.Current()
                .get(Keys.KEY_DEVICE_IDENTIFICATION).toString());

        assertTrue("Debug mode", device.isInDebugMode());
    }

    @Then("^the enable debug response should be \"([^\"]*)\"$")
    public void theEnableDebugResponseShouldBe(final String result) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION, ScenarioContext.Current()
                .get(Keys.KEY_DEVICE_IDENTIFICATION).toString());
        PROPERTIES_MAP
        .put(Keys.KEY_CORRELATION_UID, ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_ENABLE_RESPONSE, TEST_CASE_XML,
                TEST_SUITE_XML);

        assertTrue("Response value", this.runXpathResult.assertXpath(this.response, PATH_RESULT_ENABLE, result));
    }

    @When("^the disable Debug request is received$")
    public void theDisableDebugRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(requestData, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_DISABLE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);
    }

    @Then("^the device debug information should be disabled$")
    public void theDeviceDebugInformationShouldBeDisabled() throws Throwable {
        final DlmsDevice device = this.dlmsDeviceRepository.findByDeviceIdentification(ScenarioContext.Current()
                .get(Keys.KEY_DEVICE_IDENTIFICATION).toString());

        assertFalse("Debug mode", device.isInDebugMode());
    }

    @Then("^the disable debug response should be \"([^\"]*)\"$")
    public void theDisableDebugResponseShouldBe(final String result) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION, ScenarioContext.Current()
                .get(Keys.KEY_DEVICE_IDENTIFICATION).toString());
        PROPERTIES_MAP
        .put(Keys.KEY_CORRELATION_UID, ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_DISABLE_RESPONSE, TEST_CASE_XML,
                TEST_SUITE_XML);

        assertTrue("Response value", this.runXpathResult.assertXpath(this.response, PATH_RESULT_DISABLE, result));
    }
}
