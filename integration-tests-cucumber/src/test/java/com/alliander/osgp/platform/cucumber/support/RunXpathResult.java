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
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class RunXpathResult {
    private Pattern responsePattern;
    private Matcher responseMatcher;

    public XpathResult runXPathExpression(final String response, final String path)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(response));
        final Document doc = builder.parse(is);
        final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath xpath = xPathfactory.newXPath();

        return new XpathResult(xpath.compile(path), doc);
    }

    public boolean assertXpath(final String response, final String PATH_RESULT_LOGTIME,
            final String XPATH_MATCHER_RESULT_LOGTIME) throws XPathExpressionException, ParserConfigurationException,
            SAXException, IOException {
        final XpathResult xpathResult = this.runXPathExpression(response, PATH_RESULT_LOGTIME);
        final XPathExpression expr = xpathResult.getXpathExpression();
        this.responsePattern = Pattern.compile(XPATH_MATCHER_RESULT_LOGTIME);
        this.responseMatcher = this.responsePattern.matcher(expr.evaluate(xpathResult.getDocument()));

        return this.responseMatcher.find();
    }
}