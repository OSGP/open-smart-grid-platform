/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.smartmeteringmonitoring.ActualMeterReadsGas;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetFirmwareVersion extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_STATUS = "/Envelope/Body/GetFirmwareVersionResponse/Result/text()";
    private static final String PATH_RESULT_FIRMWAREVERSION_TYPE = "/Envelope/Body/GetFirmwareVersionResponse/FirmwareVersion/type";
    private static final String PATH_RESULT_FIRMWAREVERSION_VERSION = "/Envelope/Body/GetFirmwareVersionResponse/FirmwareVersion/version";

    private static final String XPATH_MATCHER_FIRMWAREVERSION_TYPE = "(MODULE_|COMMUNICATION_MODULE_)?ACTIVE_FIRMWARE";
    private static final String XPATH_MATCHER_FIRMWAREVERSION_VERSION = ".+";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "261 Retrieve firmware version";
    private static final String TEST_CASE_NAME_REQUEST = "GetFirmwareVersion - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetGetFirmwareVersionResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsGas.class);

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the get firmware version request is received$")
    public void theGetFirmwareVersionRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the firmware version result should be returned$")
    public void theFirmwareVersionResultShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, ScenarioContext.Current().get("CorrelationUid").toString());

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_STATUS, Defaults.EXPECTED_RESULT));

        this.runXpathResult.assertXpathList(this.response, PATH_RESULT_FIRMWAREVERSION_TYPE,
                XPATH_MATCHER_FIRMWAREVERSION_TYPE, 3);
        this.runXpathResult.assertXpathList(this.response, PATH_RESULT_FIRMWAREVERSION_VERSION,
                XPATH_MATCHER_FIRMWAREVERSION_VERSION, 3);
    }

}
