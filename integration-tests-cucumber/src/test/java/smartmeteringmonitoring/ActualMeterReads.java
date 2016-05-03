/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package smartmeteringmonitoring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import support.RunXpathResult;
import support.TestCaseResult;
import support.TestCaseRunner;
import support.WsdlProjectFactory;
import support.XpathResult;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ActualMeterReads {

    private TestCase testCase;
    private String response;
    private String correlationUid;
    private String organisationId;
    private String deviceId;

    private static final String PATH_RESULT_LOGTIME = "/Envelope/Body/ActualMeterReadsResponse/LogTime/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyImport/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyExport/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_ONE = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyImportTariffOne/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_TWO = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyImportTariffTwo/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_ONE = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyExportTariffOne/text()";
    private static final String PATH_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_TWO = "/Envelope/Body/ActualMeterReadsResponse/ActiveEnergyExportTariffTwo/text()";

    private static final String XPATH_MATCHER_RESULT_LOGTIME = "\\d+\\.\\d+";
    private static final String XPATH_MATCHER_RESULT_ACTIVE_ENERGY_IMPORT = "\\d+\\.\\d+";
    private static final String XPATH_MATCHER_RESULT_ACTIVE_ENERGY_EXPORT = "\\d+\\.\\d+";
    private static final String XPATH_MATCHER_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_ONE = "\\d+\\.\\d+";
    private static final String XPATH_MATCHER_RESULT_ACTIVE_ENERGY_IMPORT_TARIFF_TWO = "\\d+\\.\\d+";
    private static final String XPATH_MATCHER_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_ONE = "\\d+\\.\\d+";
    private static final String XPATH_MATCHER_RESULT_ACTIVE_ENERGY_EXPORT_TARIFF_TWO = "\\d+\\.\\d+";

    private static final String SOAP_PROJECT_XML = "../cucumber/soap-ui-project/FLEX-OVL-V3---SmartMetering-soapui-project.xml";
    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML = "392 Retrieve actual meter reads E";
    private static final String TEST_CASE_NAME_REQUEST = "GetActualMeterReads - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetActualMeterReadsResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReads.class);

    private Pattern correlationUidPattern;
    private Matcher correlationUidMatcher;

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
    public void anOrganisationWithOrganisationID(final String organisationID) throws Throwable {
        this.organisationId = organisationID;
        this.correlationUidPattern = Pattern.compile(this.organisationId + "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}");
    }

    @When("^the get actual meter reads request is received$")
    public void theGetActualMeterReadsRequestIsReceived() throws Throwable {
        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, this.deviceId,
                this.organisationId, this.correlationUid, TEST_CASE_NAME_REQUEST);

        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            this.response = ((MessageExchange) tcr).getResponseContent();
            this.correlationUidMatcher = this.correlationUidPattern.matcher(this.response);
        }
        this.correlationUidMatcher.find();
        this.correlationUid = this.correlationUidMatcher.group();

        Assert.assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());
    }

    @Then("^the actual meter reads result should be returned$")
    public void theActualMeterReadsResultShouldBeReturned() throws Throwable {
        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, this.deviceId,
                this.organisationId, this.correlationUid, TEST_CASE_NAME_RESPONSE);

        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            LOGGER.info(TEST_CASE_NAME_RESPONSE + " response {}",
                    this.response = ((MessageExchange) tcr).getResponseContent());
        }

        final XpathResult xpathResult = this.xpathResult.runXPathExpression(this.response, PATH_RESULT_LOGTIME);
        final XPathExpression expr = xpathResult.getXpathExpression();

        Assert.assertEquals(XPATH_MATCHER_RESULT_LOGTIME,
                expr.evaluate(xpathResult.getDocument(), XPathConstants.STRING));
        Assert.assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());
    }
}
