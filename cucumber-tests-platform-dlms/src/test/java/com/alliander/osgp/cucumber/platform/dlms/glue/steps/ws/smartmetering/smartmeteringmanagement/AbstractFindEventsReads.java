/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventType;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

public abstract class AbstractFindEventsReads extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_EVENTS = "/Envelope/Body/FindEventsResponse/Events";

    private static final String TEST_SUITE_XML = "SmartmeterManagement";
    private static final String TEST_CASE_XML = "19 Retrieve events from the meter";
    private static final String TEST_CASE_NAME_REQUEST = "FindEvents - ";
    private static final String TEST_CASE_NAME_RESPONSE = "GetFindEventsResponse - ";

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractFindEventsReads.class);

    protected abstract String getEventLogCategory();

    public void receivingAFindStandardEventsRequest(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(requestData, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST + this.getEventLogCategory(),
                TEST_CASE_XML, TEST_SUITE_XML);
    }

    public void eventsShouldBeReturned(final Map<String, String> settings) throws Throwable {
        PROPERTIES_MAP.put(Keys.KEY_DEVICE_IDENTIFICATION,
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
        PROPERTIES_MAP
                .put(Keys.KEY_CORRELATION_UID, ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID).toString());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE + this.getEventLogCategory(),
                TEST_CASE_XML, TEST_SUITE_XML);
        this.checkResponse(settings, this.getAllowedEventTypes());
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
        final boolean nodeListExpected = getBoolean(settings, Keys.KEY_EVENTS_NODELIST_EXPECTED,
                Defaults.EVENTS_NODELIST_EXPECTED);
        if (nodeListExpected) {
            Assert.assertEquals("Size of response nodelist should be equals to the allowed size", allowed.size(),
                    nodeList.getLength());
            for (final EventType eventtype : allowed) {
                Assert.assertTrue("eventype " + eventtype + " should be in response",
                        this.response.indexOf(eventtype.toString()) > 0);
            }
        }
    }
}
