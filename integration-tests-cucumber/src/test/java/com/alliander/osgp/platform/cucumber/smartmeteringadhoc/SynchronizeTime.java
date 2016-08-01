package com.alliander.osgp.platform.cucumber.smartmeteringadhoc;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SynchronizeTime extends SmartMetering {
    private static final String PATH_RESULT = "/Envelope/Body/SynchronizeTimeResponse/Result/text()";

    private static final String XPATH_MATCHER_RESULT = "OK";

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML = "213 Retrieve SynchronizeTime result";
    private static final String TEST_CASE_NAME_REQUEST = "SynchronizeTime - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetSynchronizeTimeResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeTime.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    private static final String DEVIATION_LABEL = "Deviation";
    private static final String DST_LABEL = "DST";

    private static final DateTimeZone DTZ_EUROPE_AMSTERDAM = DateTimeZone.forID("Europe/Amsterdam");

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the get synchronize time request is received$")
    public void theGetSynchronizeTimeRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());

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

        this.requestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the date and time is synchronized on the device$")
    public void theDateAndTimeIsSynchronizedOnTheDevice() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
    }
}
