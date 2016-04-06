package com.alliander.osgp.platform.cucumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;
import com.eviware.soapui.model.testsuite.TestSuite;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetFirmwareVersion {
    private WsdlProject project;
    private TestSuite testSuite;
    private TestCase testCase;

    private String response;

    private static final String SOAP_PROJECT_XML = "src/test/resources/FirmwareManagement-soapui-project.xml";
    private static final String TEST_SUITE_XML = "FirmwareManagementPortSoap11 TestSuite";
    private static final String TEST_CASE_XML = "GetFirmwareVersion TestCase";
    private String correlationUid;
    private String organisationId;
    private String deviceId;

    Pattern pCorrelationUid = Pattern.compile("");
    Matcher mCorrelationUid = this.pCorrelationUid.matcher("");

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareVersion.class);

    @Given("^a device with DeviceID \"([^\"]*)\"$")
    public void aDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId = deviceId;
        this.project = new WsdlProject(GetFirmwareVersion.SOAP_PROJECT_XML);
        this.testSuite = this.project.getTestSuiteByName(GetFirmwareVersion.TEST_SUITE_XML);
        this.testCase = this.testSuite.getTestCaseByName(GetFirmwareVersion.TEST_CASE_XML);
    }

    @And("^an organisation with OrganisationID \"([^\"]*)\"$")
    public void an_organisation_with_OrganisationID(final String organisationID) throws Throwable {
        this.organisationId = organisationID;
        this.pCorrelationUid = Pattern.compile(this.organisationId + "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}");
    }

    @When("^the get firmware version request is received$")
    public void theGetFirmwareVersionRequestIsReceived() throws Throwable {

        final WsdlTestCase wsdlTestCase = (WsdlTestCase) this.testCase;
        wsdlTestCase.setPropertyValue("DeviceIdentificationE", this.deviceId);
        wsdlTestCase.setPropertyValue("OrganisationIdentification", this.organisationId);
        final WsdlTestCaseRunner wsdlTestCaseRunner = new WsdlTestCaseRunner(wsdlTestCase, new PropertiesMap());
        final TestStepResult runTestStepByName = wsdlTestCaseRunner.runTestStepByName("GetFirmwareVersion");

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            this.response = ((MessageExchange) tcr).getResponseContent();
            this.mCorrelationUid = this.pCorrelationUid.matcher(this.response);
        }
        this.mCorrelationUid.find();
        this.correlationUid = this.mCorrelationUid.group();

        Assert.assertEquals(TestStepStatus.OK, runTestStepByName.getStatus());
    }

    @Then("^the firmware version result should be returned$")
    public void theFirmwareVersionResultShouldBeReturned() throws Throwable {

        final WsdlTestCase wsdlTestCase = (WsdlTestCase) this.testCase;
        wsdlTestCase.setPropertyValue("DeviceIdentificationE", this.deviceId);
        wsdlTestCase.setPropertyValue("OrganisationIdentification", this.organisationId);
        wsdlTestCase.setPropertyValue("CorrelationUid", this.correlationUid);
        final WsdlTestCaseRunner wsdlTestCaseRunner = new WsdlTestCaseRunner(wsdlTestCase, new PropertiesMap());

        try {
            Thread.sleep(15000);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        final TestStepResult runTestStepByName = wsdlTestCaseRunner.runTestStepByName("GetGetFirmwareVersionResponse");

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            LOGGER.info("GetGetFirmwareVersionResponse response {}",
                    this.response = ((MessageExchange) tcr).getResponseContent());
        }

        Assert.assertEquals(TestStepStatus.OK, runTestStepByName.getStatus());
    }
}
