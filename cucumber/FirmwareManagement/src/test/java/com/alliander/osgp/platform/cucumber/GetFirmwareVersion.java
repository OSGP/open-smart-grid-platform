package com.alliander.osgp.platform.cucumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetFirmwareVersion {
    private TestCase testCase;
    private String response;
    private String correlationUid;
    private String organisationId;
    private String deviceId;

    private static final String PATH_RESULT = "/Envelope/Body/GetFirmwareVersionResponse/Result/text()";
    private static final String XPATH_MATCHER_RESULT = "OK";

    private static final String SOAP_PROJECT_XML = "../soap-ui-project/FLEX-OVL-V3---SmartMetering-soapui-project.xml";
    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML = "261 Retrieve firmware version";
    private static final String TEST_CASE_NAME_REQUEST = "GetFirmwareVersion - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetGetFirmwareVersionResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareVersion.class);

    private Pattern patternCorrelationUid = null;
    private Matcher matcherCorrelationUid = null;

    @Autowired
    private WsdlProjectFactory wsdlProjectFactory;

    @Autowired
    private TestCaseRunner testCaseRunner;

    @Autowired
    private RunXpathResult xpathResult;

    @Given("^a device with DeviceID \"([^\"]*)\"$")
    public void aDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId = deviceId;
        this.testCase = this.wsdlProjectFactory.createWsdlTestCase(SOAP_PROJECT_XML, TEST_SUITE_XML, TEST_CASE_XML);
    }

    @And("^an organisation with OrganisationID \"([^\"]*)\"$")
    public void an_organisation_with_OrganisationID(final String organisationID) throws Throwable {
        this.organisationId = organisationID;
        this.patternCorrelationUid = Pattern.compile(this.organisationId + "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}");
    }

    @When("^the get firmware version request is received$")
    public void theGetFirmwareVersionRequestIsReceived() throws Throwable {
        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, this.deviceId,
                this.organisationId, this.correlationUid, TEST_CASE_NAME_REQUEST);

        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            this.response = ((MessageExchange) tcr).getResponseContent();
            this.matcherCorrelationUid = this.patternCorrelationUid.matcher(this.response);
        }
        this.matcherCorrelationUid.find();
        this.correlationUid = this.matcherCorrelationUid.group();

        Assert.assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());
    }

    @Then("^the firmware version result should be returned$")
    public void theFirmwareVersionResultShouldBeReturned() throws Throwable {
        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, this.deviceId,
                this.organisationId, this.correlationUid, TEST_CASE_NAME_RESPONSE);

        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            LOGGER.info(TEST_CASE_NAME_RESPONSE + " response {}",
                    this.response = ((MessageExchange) tcr).getResponseContent());
        }

        final XpathResult xpathResult = this.xpathResult.runXPathExpression(this.response, PATH_RESULT);
        final XPathExpression expr = xpathResult.getXpathExpression();

        Assert.assertEquals(XPATH_MATCHER_RESULT, expr.evaluate(xpathResult.getDocument(), XPathConstants.STRING));
        Assert.assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());
    }
}
