/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support;

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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class RunXpathResult {
    private Pattern responsePattern;
    private Matcher responseMatcher;

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
        final Document doc = builder.parse(is);
        return doc;
    }

    public NodeList getNodeList(final String xml, final String path)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        final Document doc = this.getDocument(xml);
        final XPath xpath = this.getXpath();
        final XPathExpression expr = xpath.compile(path);
        return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    }

    public boolean assertXpath(final String xml, final String pathResultLogtime, final String xpathMatcherResultLogtime)
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        final XpathResult xpathResult = this.runXPathExpression(xml, pathResultLogtime);
        final XPathExpression expr = xpathResult.getXpathExpression();
        this.responsePattern = Pattern.compile(xpathMatcherResultLogtime);
        this.responseMatcher = this.responsePattern.matcher(expr.evaluate(xpathResult.getDocument()));

        return this.responseMatcher.find();
    }
}
