/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.joda.time.DateTimeZone;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SynchronizeTime extends SmartMeteringStepsBase {
    private static final String PATH_RESULT = "/Envelope/Body/SynchronizeTimeResponse/Result/text()";

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML = "213 Retrieve SynchronizeTime result";
    private static final String TEST_CASE_NAME_REQUEST = "SynchronizeTime - Request 1";
    private static final String TEST_CASE_NAME_GETRESPONSE_REQUEST = "GetSynchronizeTimeResponse - Request 1";

    private static final String DEVIATION_LABEL = "Deviation";
    private static final String DST_LABEL = "DST";

    private static final DateTimeZone DTZ_EUROPE_AMSTERDAM = DateTimeZone.forID("Europe/Amsterdam");

    @When("^receiving a get synchronize time request$")
    public void receivingAGetSynchronizeTimeRequest(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        /*
         * Setup of deviation and DST information, that will make
         * SynchronizeTime configure a meter for time zone Europe/Amsterdam.
         * 
         * This assumes the server time that will be synchronized is about the
         * same as the system time where this test code is executed and
         * configures deviation and DST according to the proper values for
         * Europe/Amsterdam at the time of execution.
         */
        final String deviation;
        final String dst;
        if (DTZ_EUROPE_AMSTERDAM.isStandardOffset(System.currentTimeMillis())) {
            // normal time / winter time, GMT+1
            deviation = "-60";
            dst = "false";
        } else {
            // summer time (DST), daylight savings active, GMT+2
            deviation = "-120";
            dst = "true";
        }

        PROPERTIES_MAP.put(DEVIATION_LABEL, deviation);
        PROPERTIES_MAP.put(DST_LABEL, dst);

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the date and time is synchronized on the device$")
    public void theDateAndTimeIsSynchronizedOnTheDevice(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_CORRELATION_UID, ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_GETRESPONSE_REQUEST, TEST_CASE_XML,
                TEST_SUITE_XML);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, Defaults.EXPECTED_RESULT_OK));
    }
}
