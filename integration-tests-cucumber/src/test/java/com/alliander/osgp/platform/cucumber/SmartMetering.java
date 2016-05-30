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

/**
 * Super class for TestCase runner implementations. Each Runner will be called
 * from a subclass.
 *
 * @author CGI
 *
 */

public class SmartMetering {
    protected static final String SOAP_PROJECT_XML = "../cucumber/soap-ui-project/FLEX-OVL-V3---SmartMetering-soapui-project.xml";
    protected static final String XPATH_MATCHER_CORRELATIONUID = "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}";
    protected static final String DEVICE_IDENTIFICATION_E = "DeviceIdentificationE";
    protected static final String DEVICE_IDENTIFICATION_G = "DeviceIdentificationG";
    protected static final String ORGANISATION_IDENTIFICATION = "OrganisationIdentification";
    private static final String CORRELATION_UID = "CorrelationUid";

    /**
     * The values below can be used to increase or decrease the maximum polling
     * time to the response database. the total polling time =
     * laptime*maxlapcount (where laptime = time in milisecs.
     *
     * So for example if the feature 'FastFeature' normally finishes within 10
     * seconds, the in FastFeature.java these lines could be added:
     * PROPERTIES_MAP.put(LAP_TIME, "500"); PROPERTIES_MAP.put(MAX_LAPCOUNT,
     * "100"); Hence instead of polling every 5 second, now we poll every half
     * second, and this should be finished within 50 seconds (500*100 = 50000
     * msecs)
     *
     */

    private static final String LAP_TIME = "LapTime";
    private static final String MAX_LAPCOUNT = "MaxLapCount";

    protected String response;
    protected String correlationUid;
    private Pattern correlationUidPattern;
    private Matcher correlationUidMatcher;

    private TestCase testCase;

    @Autowired
    protected WsdlProjectFactory wsdlProjectFactory;

    @Autowired
    protected TestCaseRunner testCaseRunner;

    @Autowired
    protected RunXpathResult runXpathResult;

    @Autowired
    private OrganisationId organisationId;

    /**
     * RequestRunner is called from the @When step from a subclass which
     * represents cucumber test scenario('s) The RequestRunner needs a
     * propertiesMap which includes a deviceId and organisationId to call the
     * SoapUI testcase step when the wsdlTestCase is created. The other
     * parameters are specific for which test to call. The correlationUid is the
     * eventually desired result to retrieve the result in the ResponseRunner
     * method.
     */

    protected void RequestRunner(final Map<String, String> propertiesMap, final String testCaseNameRequest,
            final String testCaseXml, final String testSuiteXml) throws Throwable {

        this.correlationUidPattern = Pattern.compile(this.organisationId.getOrganisationId()
                + XPATH_MATCHER_CORRELATIONUID);
        this.testCase = this.wsdlProjectFactory.createWsdlTestCase(SOAP_PROJECT_XML, testSuiteXml, testCaseXml);

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, propertiesMap,
                testCaseNameRequest);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        this.response = ((MessageExchange) wsdlTestCaseRunner.getResults().get(0)).getResponseContent();
        this.correlationUidMatcher = this.correlationUidPattern.matcher(this.response);
        assertTrue(this.correlationUidMatcher.find());
        this.correlationUid = this.correlationUidMatcher.group();
    }

    /**
     * ResponseRunner is called from the @Then step from a subclass which
     * represents cucumber test scenario('s) The ResponseRunner needs a
     * propertiesMap which includes a deviceId, organisationId and
     * organisationId to call the SoapUI testcase step when the wsdlTestCase is
     * created. The other parameters are specific for which test to call. The
     * response is the eventually desired result which can be used to validate
     * the test result.
     */

    protected void ResponseRunner(final Map<String, String> propertiesMap, final String testCaseNameResponse,
            final Logger logger) throws Throwable {

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, propertiesMap,
                testCaseNameResponse);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        this.response = ((MessageExchange) wsdlTestCaseRunner.getResults().get(0)).getResponseContent();
        logger.info(testCaseNameResponse + " response {}", this.response);
    }

    public static String getCorrelationUid() {
        return CORRELATION_UID;
    }

    public static String getLapTime() {
        return LAP_TIME;
    }

    public static String getMaxLapcount() {
        return MAX_LAPCOUNT;
    }
}
