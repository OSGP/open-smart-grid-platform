/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support;

import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;

@Deprecated
public class XpathResult {
    private Document doc;
    private XPathExpression expr;

    public XpathResult(final XPathExpression expr, final Document doc) {
        this.doc = doc;
        this.expr = expr;
    }

    public Document getDocument() {
        return this.doc;
    }

    public XPathExpression getXpathExpression() {
        return this.expr;
    }
}
