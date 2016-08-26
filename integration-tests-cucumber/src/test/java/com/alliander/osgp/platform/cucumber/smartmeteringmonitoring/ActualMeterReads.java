/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringmonitoring;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.alliander.osgp.platform.cucumber.support.ServiceEndpoint;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ActualMeterReads extends SmartMetering {
    private static final String PATH_RESULT_LOGTIME = "/Envelope/Body/ActualMeterReadsResponse/LogTime/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyImport/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyExport/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_ONE = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyImportTariffOne/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_TWO = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyImportTariffTwo/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_ONE = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyExportTariffOne/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_TWO = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyExportTariffTwo/text()";
    private static final String PATH_RESULT_NOT_AUTHORIZED = "/Envelope/Body/Fault/faultstring/text()";

    private static final String XPATH_MATCHER_RESULT_LOGTIME = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}Z";
    private static final String XPATH_MATCHER_RESULT_ACTIVE_ENERGY = "\\d+\\.\\d+";

    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML = "392 Retrieve actual meter reads E";
    private static final String TEST_CASE_NAME_REQUEST = "GetActualMeterReads - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetActualMeterReadsResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReads.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @When("^the get actual meter reads request is received$")
    public void theGetActualMeterReadsRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the actual meter reads result should be returned$")
    public void theActualMeterReadsResultShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

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

    @When("^the not authorized get actual meter reads request is received$")
    public void theNotAuthorizedGetActualMeterReadsRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());
        this.notOkRequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML, LOGGER);
    }

    @Then("^the response \"([^\"]*)\" be returned$")
    public void theResponseBeReturned(final String response_string) throws Throwable {
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_NOT_AUTHORIZED, response_string));
    }
}
