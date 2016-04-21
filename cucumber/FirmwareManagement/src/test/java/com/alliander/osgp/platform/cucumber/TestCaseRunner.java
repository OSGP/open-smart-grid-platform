package com.alliander.osgp.platform.cucumber;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Component;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.support.SoapUIException;

@Component
public class TestCaseRunner {
    public MyTestCaseResult runWsdlTestCase(final TestCase testCase, final String deviceId,
            final String organisationId, final String correlationUid, final String TEST_CASE_NAME_REQUEST)
            throws XmlException, IOException, SoapUIException {

        final WsdlTestCase wsdlTestCase = (WsdlTestCase) testCase;
        wsdlTestCase.setPropertyValue("DeviceIdentificationE", deviceId);
        wsdlTestCase.setPropertyValue("OrganisationIdentification", organisationId);
        wsdlTestCase.setPropertyValue("CorrelationUid", correlationUid);
        final WsdlTestCaseRunner wsdlTestCaseRunner = new WsdlTestCaseRunner(wsdlTestCase, new PropertiesMap());

        try {
            Thread.sleep(15000);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        // connect to osgp_adapter_ws_smartmetering - meter_response_data and
        // check correlationUid

        return new MyTestCaseResult(wsdlTestCaseRunner.runTestStepByName(TEST_CASE_NAME_REQUEST), wsdlTestCaseRunner);
    }
}