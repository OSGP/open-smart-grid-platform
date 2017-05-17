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

public class PeriodicMeterReads extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_PERIODTYPE = "/Envelope/Body/PeriodicMeterReadsResponse/PeriodType/text()";
    private static final String PATH_RESULT_LOGTIME = "/Envelope/Body/PeriodicMeterReadsResponse/PeriodicMeterReads/LogTime/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_ONE = "/Envelope/Body/PeriodicMeterReadsResponse/PeriodicMeterReads/ActiveEnergyImportTariffOne/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_TWO = "/Envelope/Body/PeriodicMeterReadsResponse/PeriodicMeterReads/ActiveEnergyImportTariffTwo/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_ONE = "/Envelope/Body/PeriodicMeterReadsResponse/PeriodicMeterReads/ActiveEnergyExportTariffOne/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_TWO = "/Envelope/Body/PeriodicMeterReadsResponse/PeriodicMeterReads/ActiveEnergyExportTariffTwo/text()";

    private static final String XPATH_MATCHER_RESULT_PERIODTYPE = "\\w[A-Z]+";
    private static final String XPATH_MATCHER_RESULT_LOGTIME = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}Z";
    private static final String XPATH_MATCHER_RESULT_ACTIVE_ENERGY = "\\d+\\.\\d+";

    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML = "400 Retrieve periodic meter reads E";
    private static final String TEST_CASE_NAME_REQUEST = "GetPeriodicMeterReads - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetPeriodicMeterReadsResponse - Request 1";

    @When("^the get periodic meter reads request is received$")
    public void theGetPeriodicMeterReadsRequestIsReceived(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                getString(settings, "OrganizationIdentification", PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the periodic meter reads result should be returned$")
    public void thePeriodicMeterReadsResultShouldBeReturned(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_CORRELATION_UID,
                ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_PERIODTYPE,
                XPATH_MATCHER_RESULT_PERIODTYPE));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_LOGTIME, XPATH_MATCHER_RESULT_LOGTIME));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_ONE,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_TWO,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_ONE,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_TWO,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
    }

}
