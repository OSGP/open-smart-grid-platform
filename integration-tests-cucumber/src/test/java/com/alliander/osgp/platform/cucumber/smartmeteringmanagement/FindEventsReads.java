/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringmanagement;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventLogCategory;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventType;
import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindEventsReads extends SmartMetering {
    private static final String PATH_RESULT_LOGTIME = "/Envelope/Body/FindEventsResponse/Events[1]/timestamp/text()";
    private static final String PATH_RESULT_EVENTYPE = "/Envelope/Body/FindEventsResponse/Events[1]/eventType/text()";

    private static final String XPATH_MATCHER_RESULT_LOGTIME = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}Z";

    private static final String TEST_SUITE_XML = "SmartmeterManagement";
    private static final String TEST_CASE_XML = "19 Retrieve events from the meter";
    private static final String TEST_CASE_NAME_REQUEST = "FindEvents - ";
    private static final String TEST_CASE_NAME_RESPONSE = "GetFindEventsResponse - ";

    private static final Logger LOGGER = LoggerFactory.getLogger(FindEventsReads.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the find events request is received$")
    public void theFindEventsRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());

        final String category = EventLogCategory.STANDARD_EVENT_LOG.name();

        this.RequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST + category.substring(0, category.lastIndexOf('_')),
                TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^events should be returned$")
    public void eventsShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        final String category = EventLogCategory.STANDARD_EVENT_LOG.name();

        this.ResponseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE + category.substring(0, category.lastIndexOf('_')),
                LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_LOGTIME, XPATH_MATCHER_RESULT_LOGTIME));

        final String type = this.runXpathResult.runXPathExpression(this.response, PATH_RESULT_EVENTYPE).toString();

        EventType.fromValue(type);
    }
}
