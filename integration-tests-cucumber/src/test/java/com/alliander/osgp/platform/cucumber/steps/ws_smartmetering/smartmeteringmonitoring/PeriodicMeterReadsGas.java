/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.smartmeteringmonitoring;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class PeriodicMeterReadsGas extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_PERIODTYPE = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodType/text()";
    private static final String PATH_RESULT_LOGTIME = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodicMeterReadsGas/LogTime/text()";
    private static final String PATH_RESULT_CONSUMPTION = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodicMeterReadsGas/Consumption/text()";
    private static final String PATH_RESULT_CAPTURETIME = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodicMeterReadsGas/CaptureTime/text()";

    private static final String XPATH_MATCHER_RESULT_PERIODTYPE = "\\w[A-Z]+";
    private static final String XPATH_MATCHER_RESULT_LOGTIME = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}Z";
    private static final String XPATH_MATCHER_RESULT = "\\d+\\.\\d+";

    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML_225 = "225 Retrieve periodic meter reads gas";
    private static final String TEST_CASE_XML_228 = "228 Retrieve interval values gas";
    private static final String TEST_CASE_NAME_REQUEST = "GetPeriodicMeterReadsGas - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetPeriodicMeterReadsGasResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsGas.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the get periodic meter reads gas request is received$")
    public void theGetPeriodicMeterReadsRequestIsReceived() throws Throwable {

        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_G_LABEL, this.deviceId.getDeviceIdG());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML_225, TEST_SUITE_XML);
    }

    @Then("^the periodic meter reads gas result should be returned$")
    public void thePeriodicMeterReadsResultShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_PERIODTYPE,
                XPATH_MATCHER_RESULT_PERIODTYPE));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_LOGTIME, XPATH_MATCHER_RESULT_LOGTIME));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CONSUMPTION, XPATH_MATCHER_RESULT));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CAPTURETIME, XPATH_MATCHER_RESULT));
    }

    @When("^the get interval meter reads gas request is received$")
    public void theGetIntervalMeterReadsRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_G_LABEL, this.deviceId.getDeviceIdG());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML_228, TEST_SUITE_XML);
    }

    @Then("^the interval meter reads gas result should be returned$")
    public void theIntervalMeterReadsResultShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_PERIODTYPE,
                XPATH_MATCHER_RESULT_PERIODTYPE));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_LOGTIME, XPATH_MATCHER_RESULT_LOGTIME));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CONSUMPTION, XPATH_MATCHER_RESULT));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CAPTURETIME, XPATH_MATCHER_RESULT));
    }
}
