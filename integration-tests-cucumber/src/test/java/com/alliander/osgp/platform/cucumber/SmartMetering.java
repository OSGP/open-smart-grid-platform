package com.alliander.osgp.platform.cucumber;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

/**
 * Super class for TestCase runner implementations. Each Runner will be called
 * from a subclass.
 */

public class SmartMetering extends SoapTestCase {

    protected static final String XPATH_MATCHER_CORRELATIONUID = "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}";

    protected String correlationUid;

    /**
     * RequestRunner is called from the @When step from a subclass which
     * represents cucumber test scenario('s) and return the correlationUid.
     *
     * @param propertiesMap
     *            includes all needed properties for a specific test run such as
     *            DeviceId and OrganisationId
     * @param testCaseNameRequest
     *            is the specific testcase request step to be executed
     * @param testCaseXml
     *            is the testcase name which includes the testcase
     * @param testSuiteXml
     *            is the testsuite name which includes the testcase
     * @throws Throwable
     */
    @Override
    protected void requestRunner(final TestStepStatus testStepStatus, final Map<String, String> propertiesMap,
            final String testCaseNameRequest, final String testCaseXml, final String testSuiteXml) throws Throwable {

        super.requestRunner(testStepStatus, propertiesMap, testCaseNameRequest, testCaseXml, testSuiteXml);

        final Pattern correlationUidPattern = Pattern.compile(this.organisationId.getOrganisationId()
                + XPATH_MATCHER_CORRELATIONUID);
        final Matcher correlationUidMatcher = correlationUidPattern.matcher(this.response);
        if (testStepStatus == TestStepStatus.OK) {
            assertTrue(correlationUidMatcher.find());
            this.correlationUid = correlationUidMatcher.group();
        }
    }
}
