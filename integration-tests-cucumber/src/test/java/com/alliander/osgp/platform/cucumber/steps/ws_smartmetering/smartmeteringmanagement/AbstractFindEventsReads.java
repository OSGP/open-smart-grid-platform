/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.smartmeteringmanagement;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.alliander.osgp.platform.cucumber.steps.ws_smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

public abstract class AbstractFindEventsReads extends SmartMeteringStepsBase {
    private static final String PATH_RESULT_EVENTS = "/Envelope/Body/FindEventsResponse/Events";

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

    public void receivingAFindStandardEventsRequest(final Map<String, String> requestData) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_LABEL, requestData.get("DeviceIdentification"));

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST + this.getEventLogCategory(),
                TEST_CASE_XML, TEST_SUITE_XML);
    }

    public void eventsShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE + this.getEventLogCategory(), LOGGER);

        this.checkResponse(this.getAllowedEventTypes());
    }

    /**
     * Return the types of events allowed in a reponse, an assert will be done.
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
    private final void checkResponse(final List<EventType> allowed) throws XPathExpressionException,
            ParserConfigurationException, SAXException, IOException {
        final NodeList nodeList = this.runXpathResult.getNodeList(this.response, PATH_RESULT_EVENTS);
        if (nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element e = (Element) node;
                    String name = e.getNodeName();
                    String prefix = "";
                    if (name.indexOf(':') != -1) {
                        prefix = name.substring(0, name.indexOf(':') + 1);
                        name = name.substring(name.indexOf(':') + 1);
                    }
                    if ("Events".equals(name)) {
                        final String type = e.getElementsByTagName(prefix + "eventType").item(0).getTextContent();
                        Assert.assertTrue("Type not allowed " + type, allowed.contains(EventType.fromValue(type)));
                    }
                }
            }
        } else {
            // ok events can be empty
        }
    }
}
