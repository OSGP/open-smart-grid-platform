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

public class ActualMeterReadsInactiveDevice extends SmartMetering {
    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML = "392 Retrieve actual meter reads E";
    private static final String TEST_CASE_NAME_REQUEST = "GetActualMeterReads - Request 1";
    private static final String PATH_REQUEST_RESPONSE = "/Envelope/Body/Fault/detail/FunctionalFault/InnerMessage/text()";

    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsInactiveDevice.class);

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the get actual meter reads request on an inactive device is received$")
    public void theGetActualMeterReadsRequestOnAnInactiveIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        this.notOkRequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML, LOGGER);
    }


    @Then("^the inactive response \"([^\"]*)\" should be given$")
    public void theResponseDeviceIsInactiveShouldBeGiven(final String arg1) throws Throwable {
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_REQUEST_RESPONSE, arg1));
    }

}
