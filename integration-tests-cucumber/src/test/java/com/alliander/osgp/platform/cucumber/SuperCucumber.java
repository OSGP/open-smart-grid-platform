package com.alliander.osgp.platform.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.alliander.osgp.platform.cucumber.support.RunXpathResult;
import com.alliander.osgp.platform.cucumber.support.TestCaseResult;
import com.alliander.osgp.platform.cucumber.support.TestCaseRunner;
import com.alliander.osgp.platform.cucumber.support.WsdlProjectFactory;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

public class SuperCucumber {
    protected static final String SOAP_PROJECT_XML = "../cucumber/soap-ui-project/FLEX-OVL-V3---SmartMetering-soapui-project.xml";
    protected static final String XPATH_MATCHER_CORRELATIONUID = "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}";
    protected static final String DEVICE_IDENTIFICATION_E = "DeviceIdentificationE";
    protected static final String DEVICE_IDENTIFICATION_G = "DeviceIdentificationG";
    protected static final String ORGANISATION_IDENTIFICATION = "OrganisationIdentification";
    protected static final String CORRELATION_UID = "CorrelationUid";

    private TestCase testCase;

    @Autowired
    protected WsdlProjectFactory wsdlProjectFactory;

    @Autowired
    protected TestCaseRunner testCaseRunner;

    @Autowired
    protected RunXpathResult runXpathResult;

    @Autowired
    private OrganisationId organisationId;

    protected String RequestRunner(final Map<String, String> propertiesMap, final String TEST_CASE_NAME_REQUEST,
            final String TEST_CASE_XML, final String TEST_SUITE_XML) throws Throwable {

        String response;
        String correlationUid;
        Pattern correlationUidPattern;
        final Matcher correlationUidMatcher;

        correlationUidPattern = Pattern.compile(this.organisationId.getOrganisationId() + XPATH_MATCHER_CORRELATIONUID);
        this.testCase = this.wsdlProjectFactory.createWsdlTestCase(SOAP_PROJECT_XML, TEST_SUITE_XML, TEST_CASE_XML);

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, propertiesMap,
                TEST_CASE_NAME_REQUEST);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        response = ((MessageExchange) wsdlTestCaseRunner.getResults().get(0)).getResponseContent();
        correlationUidMatcher = correlationUidPattern.matcher(response);
        assertTrue(correlationUidMatcher.find());
        correlationUid = correlationUidMatcher.group();

        return correlationUid;
    }

    protected String ResponseRunner(final Map<String, String> propertiesMap, final String TEST_CASE_NAME_RESPONSE,
            final Logger LOGGER) throws Throwable {

        String response;

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, propertiesMap,
                TEST_CASE_NAME_RESPONSE);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        response = ((MessageExchange) wsdlTestCaseRunner.getResults().get(0)).getResponseContent();
        LOGGER.info(TEST_CASE_NAME_RESPONSE + " response {}", response);

        return response;
    }
}
