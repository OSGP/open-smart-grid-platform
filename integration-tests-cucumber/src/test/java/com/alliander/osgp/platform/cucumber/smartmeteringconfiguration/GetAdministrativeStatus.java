/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringconfiguration;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.smartmeteringmonitoring.ActualMeterReadsGas;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetAdministrativeStatus extends SmartMetering {
    private static final String PATH_RESULT_ENABLED = "/Envelope/Body/GetAdministrativeStatusResponse/Enabled/text()";

    private static final String XPATH_MATCHER_RESULT_ENABLED = "\\w[A-Z]";

    private static final String TEST_SUITE_XML = "SmartmeterConfiguration";
    private static final String TEST_CASE_XML = "190 Get administrative status";
    private static final String TEST_CASE_NAME_REQUEST = "GetAdministrativeStatus - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetGetAdministrativeStatusResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsGas.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the get administrative status request is received$")
    public void theRetrieveAdministrativeStatusRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());

        this.requestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the administrative status should be returned$")
    public void theAdministrativeStatusShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ENABLED, XPATH_MATCHER_RESULT_ENABLED));
    }

}
