package com.alliander.osgp.platform.cucumber;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Component;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.support.SoapUIException;

@Component
public class WsdlProjectFactory {
    public WsdlTestCase createWsdlTestCase(final String SOAP_PROJECT_XML, final String TEST_SUITE_XML,
            final String TEST_CASE_XML) throws XmlException, IOException, SoapUIException {
        return new WsdlProject(SOAP_PROJECT_XML).getTestSuiteByName(TEST_SUITE_XML).getTestCaseByName(TEST_CASE_XML);
    }
}
