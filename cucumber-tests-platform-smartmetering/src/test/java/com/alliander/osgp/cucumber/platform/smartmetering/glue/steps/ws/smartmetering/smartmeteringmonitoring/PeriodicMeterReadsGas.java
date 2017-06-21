/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class PeriodicMeterReadsGas extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_PERIODTYPE = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodType/text()";
    private static final String PATH_RESULT_LOGTIME = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodicMeterReadsGas/LogTime/text()";
    private static final String PATH_RESULT_CONSUMPTION = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodicMeterReadsGas/Consumption/text()";
    private static final String PATH_RESULT_CAPTURETIME = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodicMeterReadsGas/CaptureTime/text()";

    private static final String XPATH_MATCHER_RESULT_LOGTIME = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}Z";
    private static final String XPATH_MATCHER_RESULT = "\\d+\\.\\d+";

    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML = "Retrieve periodic meter reads gas";
    private static final String TEST_CASE_NAME_REQUEST = "GetPeriodicMeterReadsGas - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetPeriodicMeterReadsGasResponse - Request 1";

    @When("^the get \"([^\"]*)\" meter reads gas request is received$")
    public void theGetMeterReadsRequestIsReceived(final String periodtype, final Map<String, String> settings)
            throws Throwable {

        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, getString(settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, getString(settings,
                PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_PERIOD_TYPE,
                getString(settings, PlatformKeys.KEY_PERIOD_TYPE, PlatformDefaults.DEFAULT_PERIOD_TYPE));
        PROPERTIES_MAP.put(PlatformKeys.KEY_BEGIN_DATE,
                getString(settings, PlatformKeys.KEY_BEGIN_DATE, PlatformDefaults.DEFAULT_BEGIN_DATE));
        PROPERTIES_MAP.put(PlatformKeys.KEY_END_DATE,
                getString(settings, PlatformKeys.KEY_END_DATE, PlatformDefaults.DEFAULT_END_DATE));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the \"([^\"]*)\" meter reads gas result should be returned$")
    public void theMeterReadsResultShouldBeReturned(final String periodType, final Map<String, String> settings)
            throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, getString(settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_CORRELATION_UID,
                ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, TEST_CASE_XML, TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_PERIODTYPE, periodType));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_LOGTIME, XPATH_MATCHER_RESULT_LOGTIME));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CONSUMPTION, XPATH_MATCHER_RESULT));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CAPTURETIME, XPATH_MATCHER_RESULT));
    }

}
