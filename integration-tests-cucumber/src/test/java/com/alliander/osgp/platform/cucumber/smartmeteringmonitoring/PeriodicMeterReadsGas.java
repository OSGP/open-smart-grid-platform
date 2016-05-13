/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringmonitoring;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.support.DeviceId;
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

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class PeriodicMeterReadsGas {
    private TestCase testCase;
    private String response;
    private String correlationUid;

    private static final String PATH_RESULT_PERIODTYPE = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodType/text()";
    private static final String PATH_RESULT_LOGTIME = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodicMeterReadsGas/LogTime/text()";
    private static final String PATH_RESULT_CONSUMPTION = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodicMeterReadsGas/Consumption/text()";
    private static final String PATH_RESULT_CAPTURETIME = "/Envelope/Body/PeriodicMeterReadsGasResponse/PeriodicMeterReadsGas/CaptureTime/text()";

    private static final String XPATH_MATCHER_RESULT_PERIODTYPE = "\\w[A-Z]+";
    private static final String XPATH_MATCHER_RESULT_LOGTIME = "\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}Z";
    private static final String XPATH_MATCHER_RESULT_CONSUMPTION = "\\d+\\.\\d+";
    private static final String XPATH_MATCHER_RESULT_CAPTURETIME = "\\d+\\.\\d+";

    private static final String SOAP_PROJECT_XML = "../cucumber/soap-ui-project/FLEX-OVL-V3---SmartMetering-soapui-project.xml";
    private static final String TEST_SUITE_XML = "SmartmeterMonitoring";
    private static final String TEST_CASE_XML_225 = "225 Retrieve periodic meter reads gas";
    private static final String TEST_CASE_XML_228 = "228 Retrieve interval values gas";
    private static final String TEST_CASE_NAME_REQUEST = "GetPeriodicMeterReadsGas - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetPeriodicMeterReadsGasResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReads.class);
    private static final HashMap<String, String> PROPERTIESMAP = new HashMap<>();

    private Pattern correlationUidPattern;
    private Matcher correlationUidMatcher;

    @Autowired
    private WsdlProjectFactory wsdlProjectFactory;

    @Autowired
    private TestCaseRunner testCaseRunner;

    @Autowired
    private RunXpathResult runXpathResult;

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the get periodic meter reads gas request is received$")
    public void theGetPeriodicMeterReadsRequestIsReceived() throws Throwable {
        this.correlationUidPattern = Pattern.compile(this.organisationId.getOrganisationId()
                + "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}");
        this.testCase = this.wsdlProjectFactory.createWsdlTestCase(SOAP_PROJECT_XML, TEST_SUITE_XML, TEST_CASE_XML_225);

        PROPERTIESMAP.put("DeviceIdentificationE", this.deviceId.getDeviceId());
        PROPERTIESMAP.put("OrganisationIdentification", this.organisationId.getOrganisationId());

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, PROPERTIESMAP,
                TEST_CASE_NAME_REQUEST);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        Assert.assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            this.response = ((MessageExchange) tcr).getResponseContent();
            this.correlationUidMatcher = this.correlationUidPattern.matcher(this.response);
        }
        this.correlationUidMatcher.find();
        this.correlationUid = this.correlationUidMatcher.group();
    }

    @Then("^the periodic meter reads gas result should be returned$")
    public void thePeriodicMeterReadsResultShouldBeReturned() throws Throwable {
        PROPERTIESMAP.put("CorrelationUid", this.correlationUid);

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, PROPERTIESMAP,
                TEST_CASE_NAME_RESPONSE);

        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        Assert.assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            LOGGER.info(TEST_CASE_NAME_RESPONSE + " response {}",
                    this.response = ((MessageExchange) tcr).getResponseContent());
        }

        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_PERIODTYPE,
                XPATH_MATCHER_RESULT_PERIODTYPE));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_LOGTIME,
                XPATH_MATCHER_RESULT_LOGTIME));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CONSUMPTION,
                XPATH_MATCHER_RESULT_CONSUMPTION));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CAPTURETIME,
                XPATH_MATCHER_RESULT_CAPTURETIME));
    }

    @When("^the get interval meter reads gas request is received$")
    public void theGetIntervalMeterReadsRequestIsReceived() throws Throwable {
        this.correlationUidPattern = Pattern.compile(this.organisationId.getOrganisationId()
                + "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}");
        this.testCase = this.wsdlProjectFactory.createWsdlTestCase(SOAP_PROJECT_XML, TEST_SUITE_XML, TEST_CASE_XML_228);

        PROPERTIESMAP.put("DeviceIdentificationE", this.deviceId.getDeviceId());
        PROPERTIESMAP.put("OrganisationIdentification", this.organisationId.getOrganisationId());

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, PROPERTIESMAP,
                TEST_CASE_NAME_REQUEST);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        Assert.assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            this.response = ((MessageExchange) tcr).getResponseContent();
            this.correlationUidMatcher = this.correlationUidPattern.matcher(this.response);
        }
        this.correlationUidMatcher.find();
        this.correlationUid = this.correlationUidMatcher.group();
    }

    @Then("^the interval meter reads gas result should be returned$")
    public void theIntervalMeterReadsResultShouldBeReturned() throws Throwable {
        PROPERTIESMAP.put("CorrelationUid", this.correlationUid);

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, PROPERTIESMAP,
                TEST_CASE_NAME_RESPONSE);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        Assert.assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            LOGGER.info(TEST_CASE_NAME_RESPONSE + " response {}",
                    this.response = ((MessageExchange) tcr).getResponseContent());
        }

        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_PERIODTYPE,
                XPATH_MATCHER_RESULT_PERIODTYPE));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_LOGTIME,
                XPATH_MATCHER_RESULT_LOGTIME));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CONSUMPTION,
                XPATH_MATCHER_RESULT_CONSUMPTION));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CAPTURETIME,
                XPATH_MATCHER_RESULT_CAPTURETIME));
    }
}
