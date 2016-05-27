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

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReadAlarmRegister extends SmartMetering {
    private static final String PATH_RESULT_ALARMTYPES = "/Envelope/Body/ReadAlarmRegisterResponse/AlarmRegister/AlarmTypes/text()";

    private static final String XPATH_MATCHER_RESULT_ALARMTYPES = "\\w[A-Z]";

    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML = "192 Read alarm register";
    private static final String TEST_CASE_NAME_REQUEST = "ReadAlarmRegister - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetReadAlarmRegisterResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadAlarmRegister.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the get read alarm register request is received$")
    public void theGetActualMeterReadsRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION, this.organisationId.getOrganisationId());

        this.RequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the alarm register should be returned$")
    public void theActualMeterReadsResultShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID, this.correlationUid);

        this.ResponseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ALARMTYPES,
                XPATH_MATCHER_RESULT_ALARMTYPES));
    }
}
