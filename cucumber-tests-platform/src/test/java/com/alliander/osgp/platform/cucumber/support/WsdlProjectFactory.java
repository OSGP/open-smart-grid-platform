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

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.support.SoapUIException;

@Deprecated
public class WsdlProjectFactory {

    private final WsdlProject project;

    public WsdlProjectFactory(final String soapProjectXml, final String certBasePath)
            throws XmlException, IOException, SoapUIException {
        this.project = new WsdlProject(soapProjectXml);
        this.project.setPropertyValue("CertBasePath", certBasePath);
    }

    public WsdlTestCase createWsdlTestCase(final String testSuiteXml, final String testCaseXml)
            throws XmlException, IOException, SoapUIException {
        return this.project.getTestSuiteByName(testSuiteXml).getTestCaseByName(testCaseXml);
    }
}
