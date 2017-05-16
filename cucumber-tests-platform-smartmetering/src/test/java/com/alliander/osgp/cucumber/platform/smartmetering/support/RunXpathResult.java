/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Deprecated
@Component
public class RunXpathResult {

    public XpathResult runXPathExpression(final String xml, final String path)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        final Document doc = this.getDocument(xml);
        final XPath xpath = this.getXpath();

        return new XpathResult(xpath.compile(path), doc);
    }

    private XPath getXpath() {
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath xpath = xPathfactory.newXPath();
        return xpath;
    }

    public Document getDocument(final String xml) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        return builder.parse(is);
    }

    public NodeList getNodeList(final String xml, final String path)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        final Document doc = this.getDocument(xml);
        final XPath xpath = this.getXpath();
        final XPathExpression expr = xpath.compile(path);
        return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    }

    public boolean assertXpath(final String xml, final String nodeXPath, final String nodeRegex)
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        final XpathResult xpathResult = this.runXPathExpression(xml, nodeXPath);
        final XPathExpression expr = xpathResult.getXpathExpression();
        final Pattern responsePattern = Pattern.compile(nodeRegex);
        final Matcher responseMatcher = responsePattern.matcher(expr.evaluate(xpathResult.getDocument()));
        return responseMatcher.find();
    }

    public void assertXpathList(final String xml, final String nodeXPath, final String nodeRegex,
            final int expectedNodes)
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        final XpathResult xpathResult = this.runXPathExpression(xml, nodeXPath);
        final XPathExpression expr = xpathResult.getXpathExpression();
        final Pattern responsePattern = Pattern.compile(nodeRegex);

        final NodeList list = (NodeList) expr.evaluate(xpathResult.getDocument(), XPathConstants.NODESET);

        assertEquals("Expected number of nodes does not match.", expectedNodes, list.getLength());

        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            /**
             * Originally, node.getNodeValue() was used (and apparantly worked)
             * but than it apeared that this value may null, whereas
             * getFirstChild().getNodeValue() contains the correct value, hence
             * this if-else below.
             */
            final String nodeValue = node.getNodeValue() == null ? node.getFirstChild().getNodeValue()
                    : node.getNodeValue();
            final Matcher responseMatcher = responsePattern.matcher(nodeValue);
            assertTrue(responseMatcher.find());
        }
    }

    /**
     * Verifies that the XPath is not null or empty
     *
     * @param xml
     * @param nodeXPath
     * @throws Throwable
     */
    public void assertNotNull(final String xml, final String nodeXPath) throws Throwable {
        final XpathResult xpathResult = this.runXPathExpression(xml, nodeXPath);
        final XPathExpression expr = xpathResult.getXpathExpression();

        assertTrue(expr.evaluate(xpathResult.getDocument()) != null);
    }

    public String getValue(final String xml, final String nodeXPath) throws Throwable {
        final XpathResult xpathResult = this.runXPathExpression(xml, nodeXPath);
        final XPathExpression expr = xpathResult.getXpathExpression();
        return expr.evaluate(xpathResult.getDocument());
    }
}
