/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec61850.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class IcdFileConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(IcdFileConverter.class);

  private IcdFileConverter() {
    // default constructor
  }

  public static InputStream convertReportsForTesting(final InputStream inputStream) {

    Document doc = null;
    try {
      final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
    } catch (final SAXException | IOException | ParserConfigurationException e) {
      LOGGER.error("Exception occurred while creating document", e);
    }

    final XPath xPath = XPathFactory.newInstance().newXPath();

    disableBufferedReports(xPath, doc);
    disablePeriodicReports(xPath, doc);
    enableReportOnQualityChange(xPath, doc);
    setIntegrityPeriodToZero(xPath, doc);

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Transformer transformer;
    try {
      final TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

      transformer = transformerFactory.newTransformer();
      final Result result = new StreamResult(byteArrayOutputStream);
      final Source source = new DOMSource(doc);
      transformer.transform(source, result);
    } catch (final TransformerException e) {
      LOGGER.error("Exception occurred while transforming", e);
    }

    return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
  }

  private static void setIntegrityPeriodToZero(final XPath xPath, final Document doc) {

    try {
      final NodeList nodeList =
          (NodeList)
              xPath.evaluate("//ReportControl[@intgPd='60000']", doc, XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); i++) {
        final Node value = nodeList.item(i).getAttributes().getNamedItem("intgPd");
        value.setNodeValue("0");
      }
    } catch (final XPathExpressionException e) {
      LOGGER.error("Exception occurred: Unable to set Integrity Period to zero", e);
    }
  }

  private static void enableReportOnQualityChange(final XPath xPath, final Document doc) {

    try {
      final NodeList nodeList =
          (NodeList) xPath.evaluate("//TrgOps[@*]", doc, XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); i++) {
        final Element element = ((Element) nodeList.item(i));
        element.setAttribute("qchg", "true");
      }
    } catch (final XPathExpressionException e) {
      LOGGER.error("Exception occurred: Unable to enable reporting on quality change", e);
    }
  }

  private static void disablePeriodicReports(final XPath xPath, final Document doc) {

    try {
      final NodeList nodeList =
          (NodeList) xPath.evaluate("//TrgOps[@period='true']", doc, XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); i++) {
        final Node value = nodeList.item(i).getAttributes().getNamedItem("period");
        value.setNodeValue("false");
      }
    } catch (final XPathExpressionException e) {
      LOGGER.error("Exception occurred: Unable to disable periodic reports", e);
    }
  }

  private static void disableBufferedReports(final XPath xPath, final Document doc) {

    try {
      final NodeList nodeList =
          (NodeList)
              xPath.evaluate("//ReportControl[@buffered='true']", doc, XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); i++) {
        final Node value = nodeList.item(i).getAttributes().getNamedItem("buffered");
        value.setNodeValue("false");
      }
    } catch (final XPathExpressionException e) {
      LOGGER.error("Exception occurred: Unable to disable buffered reports", e);
    }
  }
}
