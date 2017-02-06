/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.support.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SoapFaultHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoapFaultHelper.class);

    private SoapFaultHelper() {
        // Utility class.
    }

    public static Object getFaultDetailValuesByElement(final SoapFaultClientException soapFaultClientException) {

        if (soapFaultClientException == null || soapFaultClientException.getSoapFault() == null) {
            return Collections.emptyMap();
        }

        final Node nodeForFirstFaultDetailEntry = getNodeForFirstFaultDetailEntry(
                soapFaultClientException.getSoapFault().getFaultDetail());

        if (nodeForFirstFaultDetailEntry == null) {
            return Collections.emptyMap();
        }

        LOGGER.debug("Checking child nodes of SOAP fault detail entry: {{}}{}",
                nodeForFirstFaultDetailEntry.getNamespaceURI(), nodeForFirstFaultDetailEntry.getLocalName());

        if (nodeForFirstFaultDetailEntry.getLocalName().equals("ValidationError")) {
            return getFaultDetailValuesByElement(nodeForFirstFaultDetailEntry);
        } else {
            return getFaultDetailValuesByElement(nodeForFirstFaultDetailEntry.getChildNodes());
        }
    }

    private static Node getNodeForFirstFaultDetailEntry(final SoapFaultDetail faultDetail) {

        if ((faultDetail == null) || (faultDetail.getDetailEntries() == null)
                || !faultDetail.getDetailEntries().hasNext()) {
            return null;
        }

        final Source source = faultDetail.getDetailEntries().next().getSource();
        if (source instanceof DOMSource) {
            return ((DOMSource) source).getNode();
        }

        final DOMResult domResult = new DOMResult();
        try {
            TransformerFactory.newInstance().newTransformer().transform(source, domResult);
        } catch (TransformerException | TransformerFactoryConfigurationError e) {
            LOGGER.warn("Unable to transform first SOAP-Fault detail entry to DOMResult", e);
            return null;
        }
        return domResult.getNode();
    }

    private static Map<FaultDetailElement, String> getFaultDetailValuesByElement(final NodeList nodeList) {

        final Map<FaultDetailElement, String> faultDetailValuesByElement = new EnumMap<>(FaultDetailElement.class);

        final int numberOfChildNodes = nodeList.getLength();
        for (int i = 0; i < numberOfChildNodes; i++) {
            addFaultDetailElement(faultDetailValuesByElement, nodeList.item(i));
        }

        return faultDetailValuesByElement;
    }

    private static void addFaultDetailElement(final Map<FaultDetailElement, String> faultDetailValuesByElement,
            final Node node) {

        final FaultDetailElement faultDetailElement = FaultDetailElement.forLocalName(node.getLocalName());

        if (faultDetailElement == null) {
            LOGGER.info("Ignoring unexpected child node: {{}}{}", node.getNamespaceURI(), node.getLocalName());
            return;
        }

        faultDetailValuesByElement.put(faultDetailElement, getNodeValueAsText(node));
    }

    private static String getNodeValueAsText(final Node node) {
        if (StringUtils.isBlank(node.getTextContent())) {
            return null;
        }
        return node.getTextContent().trim();
    }

    private static List<String> getFaultDetailValuesByElement(Node node) {

        final List<String> validationErrors = new ArrayList<>();

        while (node != null) {
            addFaultDetailElement(validationErrors, node);
            node = node.getNextSibling();
        }

        return validationErrors;
    }

    private static void addFaultDetailElement(final List<String> validationErrors, final Node node) {

        final FaultDetailElement faultDetailElement = FaultDetailElement.forLocalName(node.getLocalName());

        if (checkIfFaultDetailElementWithLocalNameOfNodeIsNotNull(faultDetailElement, node)) {
            validationErrors.add(getNodeValueAsText(node));
        }
    }

    private static boolean checkIfFaultDetailElementWithLocalNameOfNodeIsNotNull(
            final FaultDetailElement faultDetailElement, final Node node) {
        if (faultDetailElement == null) {
            LOGGER.info("Ignoring unexpected child node: {{}}{}", node.getNamespaceURI(), node.getLocalName());
            return false;
        }
        return true;
    }
}
