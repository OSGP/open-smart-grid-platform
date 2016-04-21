package com.alliander.osgp.platform.cucumber;

import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;

public class MyXpathResult {
    private Document doc;
    private XPathExpression expr;

    public MyXpathResult(final XPathExpression expr, final Document doc) {
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
