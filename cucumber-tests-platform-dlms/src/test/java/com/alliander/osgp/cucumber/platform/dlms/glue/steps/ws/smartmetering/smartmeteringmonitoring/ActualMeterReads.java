/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ActualMeterReads extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_LOGTIME = "/Envelope/Body/ActualMeterReadsResponse/LogTime/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyImport/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyExport/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_ONE = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyImportTariffOne/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_TWO = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyImportTariffTwo/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_ONE = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyExportTariffOne/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_TWO = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyExportTariffTwo/text()";
    private static final String PATH_RESULT_NOT_ACTIVE = "/Envelope/Body/Fault/faultstring/text()";
    private static final String PATH_RESULT_NOT_ACTIVE_MESSAGE = "/Envelope/Body/Fault/detail/FunctionalFault/InnerMessage/text()";

    private static final String XPATH_MATCHER_RESULT_LOGTIME = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}Z";
    private static final String XPATH_MATCHER_RESULT_ACTIVE_ENERGY = "\\d+\\.\\d+";
    private static final String XPATH_PATH_RESULT_NOT_ACTIVE = "INACTIVE_DEVICE";

    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML = "392 Retrieve actual meter reads E";
    private static final String TEST_CASE_NAME_REQUEST = "GetActualMeterReads - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetActualMeterReadsResponse - Request 1";

    @When("^the get actual meter reads request is received$")
    public void theGetActualMeterReadsRequestIsReceived(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(Keys.KEY_ORGANIZATION_IDENTIFICATION,
                getString(settings, "OrganizationIdentification", Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the actual meter reads result should be returned$")
    public void theActualMeterReadsResultShouldBeReturned(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_CORRELATION_UID, ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_LOGTIME, XPATH_MATCHER_RESULT_LOGTIME));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_IMPORT,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_EXPORT,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_ONE,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_TWO,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_ONE,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_TWO,
                XPATH_MATCHER_RESULT_ACTIVE_ENERGY));
    }

    @When("^the get actual meter reads request on an inactive device is received$")
    public void theGetActualMeterReadsRequestOnAnInactiveDeviceIsReceived(final Map<String, String> settings)
            throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        getString(settings, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                                Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        this.requestRunner(TestStepStatus.FAILED, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the response \"([^\"]*)\" will be returned$")
    public void theResponseWillBeReturned(final String responseResult) throws Throwable {
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_NOT_ACTIVE, XPATH_PATH_RESULT_NOT_ACTIVE));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_NOT_ACTIVE_MESSAGE, responseResult));
    }
}
