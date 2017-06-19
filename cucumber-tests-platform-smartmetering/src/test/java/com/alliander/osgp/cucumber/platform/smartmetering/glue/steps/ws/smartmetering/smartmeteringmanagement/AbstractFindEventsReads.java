/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static com.alliander.osgp.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventType;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

public abstract class AbstractFindEventsReads extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_EVENTS = "/Envelope/Body/FindEventsResponse/Events";

    private static final String TEST_SUITE_XML = "SmartmeterManagement";
    private static final String TEST_CASE_XML = "19 Retrieve events from the meter";
    private static final String TEST_CASE_NAME_REQUEST = "FindEvents - ";
    private static final String TEST_CASE_NAME_RESPONSE = "GetFindEventsResponse - ";

    private static final String DEFAULT_BEGIN_DATE_EVENT_LOG = "2015-09-01T00:00:00.000Z";
    private static final String DEFAULT_END_DATE_EVENT_LOG = DateTime.now(DateTimeZone.UTC).toString();
    private static final String EXPECTED_NUMBER_OF_EVENTS = "ExpectedNumberOfEvents";

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractFindEventsReads.class);

    protected abstract String getEventLogCategory();

    public void receivingAFindEventsRequest(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, getString(requestData,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_SMART_METER_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_BEGIN_DATE,
                getString(requestData, PlatformKeys.KEY_BEGIN_DATE, DEFAULT_BEGIN_DATE_EVENT_LOG));
        PROPERTIES_MAP.put(PlatformKeys.KEY_END_DATE,
                getString(requestData, PlatformKeys.KEY_END_DATE, DEFAULT_END_DATE_EVENT_LOG));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST + this.getEventLogCategory(),
                TEST_CASE_XML, TEST_SUITE_XML);

        saveCorrelationUidInScenarioContext(this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
                getString(PROPERTIES_MAP, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }

    public void eventsShouldBeReturned(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, getString(settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_SMART_METER_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put(PlatformKeys.KEY_CORRELATION_UID,
                ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE + this.getEventLogCategory(),
                TEST_CASE_XML, TEST_SUITE_XML);
        this.checkResponse(settings, this.getAllowedEventTypes());
    }

    public void eventsForAllTypesShouldBeReturned(final Map<String, String> settings) throws Throwable {
        this.eventsShouldBeReturned(
                SettingsHelper.addDefault(settings, PlatformKeys.KEY_EVENTS_NODELIST_EXPECTED, "true"));
    }

    public void eventsShouldBeReturned(final int numberOfEvents, final Map<String, String> settings) throws Throwable {
        this.eventsShouldBeReturned(
                SettingsHelper.addDefault(settings, EXPECTED_NUMBER_OF_EVENTS, String.valueOf(numberOfEvents)));
    }

    /**
     * Return the types of events allowed in a response, an assert will be done.
     *
     * @return
     */
    protected abstract List<EventType> getAllowedEventTypes();

    /**
     * To be called from subclasses, checks whether event types returned belong
     * to the log category requested.
     *
     * @param allowed
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private final void checkResponse(final Map<String, String> settings, final List<EventType> allowed)
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {

        final NodeList nodeList = this.runXpathResult.getNodeList(this.response, PATH_RESULT_EVENTS);
        final int actualNumberOfEvents = nodeList.getLength();
        final boolean nodeListExpected = getBoolean(settings, PlatformKeys.KEY_EVENTS_NODELIST_EXPECTED,
                PlatformDefaults.EVENTS_NODELIST_EXPECTED);
        if (nodeListExpected) {
            Assert.assertEquals("Number of events", allowed.size(), actualNumberOfEvents);
            for (final EventType eventtype : allowed) {
                Assert.assertTrue("eventype " + eventtype + " should be in response",
                        this.response.indexOf(eventtype.toString()) > 0);
            }
        }
        if (settings.containsKey(EXPECTED_NUMBER_OF_EVENTS)) {
            final int expectedNumberOfEvents = getInteger(settings, EXPECTED_NUMBER_OF_EVENTS);
            Assert.assertEquals("Number of events", expectedNumberOfEvents, actualNumberOfEvents);
        }
    }
}
