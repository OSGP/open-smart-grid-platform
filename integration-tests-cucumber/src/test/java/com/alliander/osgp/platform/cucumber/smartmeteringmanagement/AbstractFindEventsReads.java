/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringmanagement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventType;
import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;

public abstract class AbstractFindEventsReads extends SmartMetering {
    private static final String PATH_RESULT_EVENTS = "/Envelope/Body/FindEventsResponse/Events";

    private static final String XPATH_MATCHER_RESULT_LOGTIME = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}Z";
    private static final Pattern TIME_PATTERN = Pattern.compile(XPATH_MATCHER_RESULT_LOGTIME);

    private static final String TEST_SUITE_XML = "SmartmeterManagement";
    private static final String TEST_CASE_XML = "19 Retrieve events from the meter";
    private static final String TEST_CASE_NAME_REQUEST = "FindEvents - ";
    private static final String TEST_CASE_NAME_RESPONSE = "GetFindEventsResponse - ";

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractFindEventsReads.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    protected abstract String getEventLogCategory();

    public void theFindEventsRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());

        this.RequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST + this.getEventLogCategory(), TEST_CASE_XML,
                TEST_SUITE_XML);
    }

    public void eventsShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.ResponseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE + this.getEventLogCategory(), LOGGER);

        this.checkResponse();

    }

    private void checkResponse() throws XPathExpressionException, ParserConfigurationException, SAXException,
    IOException {
        final NodeList nodeList = this.runXpathResult.getNodeList(this.response, PATH_RESULT_EVENTS);
        if (nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element e = (Element) node;
                    if (e.getLocalName() == "Events") {
                        final String timestamp = e.getElementsByTagName("timestamp").item(0).getTextContent();
                        Assert.assertTrue(TIME_PATTERN.matcher(timestamp).matches());
                        final String type = e.getElementsByTagName("eventType").item(0).getTextContent();
                        EventType.fromValue(type);
                    }
                }
            }
        } else {
            // ok events can be empty
        }
    }
}
