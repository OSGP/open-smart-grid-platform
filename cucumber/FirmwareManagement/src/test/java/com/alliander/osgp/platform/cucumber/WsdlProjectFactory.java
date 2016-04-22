package com.alliander.osgp.platform.cucumber;

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
