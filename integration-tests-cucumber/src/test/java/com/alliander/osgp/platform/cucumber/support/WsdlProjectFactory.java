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

import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Component;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.support.SoapUIException;

@Component
public class WsdlProjectFactory {
    public WsdlTestCase createWsdlTestCase(final String soapProjectXml, final String testSuiteXml,
            final String testCaseXml) throws XmlException, IOException, SoapUIException {
        return new WsdlProject(soapProjectXml).getTestSuiteByName(testSuiteXml).getTestCaseByName(testCaseXml);
    }
}
