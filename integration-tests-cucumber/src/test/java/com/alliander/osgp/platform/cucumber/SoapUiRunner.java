/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.alliander.osgp.platform.cucumber.support.RunXpathResult;
import com.alliander.osgp.platform.cucumber.support.TestCaseResult;
import com.alliander.osgp.platform.cucumber.support.TestCaseRunner;
import com.alliander.osgp.platform.cucumber.support.WsdlProjectFactory;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;
import com.eviware.soapui.support.SoapUIException;

/**
 * Super class for SOAP UI runner implementations. Each Runner will be called
 * from a subclass.
 */
@Configuration
@PropertySources({ @PropertySource("classpath:osgp-cucumber.properties"),
        @PropertySource(value = "file:/etc/osp/osgp-cucumber.properties", ignoreResourceNotFound = true), })
public abstract class SoapUiRunner {

    /**
     * The url of the server to test. Default to localhost:443.
     */
    @Value("${serviceEndpoint}")
    private String serviceEndpoint;

    @Value("${certificate.basepath}")
    private String certBasePath;

    private final Logger LOGGER = LoggerFactory.getLogger(SoapUiRunner.class);
    private static final String DEFAULT_SOAPUI_PROJECT = "soap-ui-project/Core-SoapUI-project.xml";
    private String soapuiProject = DEFAULT_SOAPUI_PROJECT;
    private static final String ERRMSG = "The soapUi xml fragment: \n %s \ndoes not contain all three tags: \n %s, %s and/or %s";

    /**
     * Default constructor. As default project the OSGP-SoapUI-project.xml will
     * be used.
     */
    protected SoapUiRunner() {
    }

    /**
     * Constructor.
     *
     * @param soapUiProject
     *            The full path of the soap ui project to be used.
     * @throws SoapUIException
     * @throws IOException
     * @throws XmlException
     */
    public SoapUiRunner(final String soapUiProject) {
        this.soapuiProject = soapUiProject;
    }

    @PostConstruct
    protected void init() throws XmlException, IOException, SoapUIException {
        this.wsdlProjectFactory = new WsdlProjectFactory(this.soapuiProject, this.certBasePath);
    }

    /**
     * TIME_OUT represents the time in milliseconds between each moment polling
     * the database for a response. MAX_TIME represents the maximum allowed
     * polling time in milliseconds within the response should be returned. When
     * this time is over, the polling will stop and return the result when
     * available.
     */

    public static final String TIME_OUT = "TimeOut";
    public static final String MAX_TIME = "MaxTime";

    protected String request;
    protected String response;

    private TestCase testCase;

    protected WsdlProjectFactory wsdlProjectFactory;

    @Autowired
    protected RunXpathResult runXpathResult;

    @Autowired
    protected TestCaseRunner testCaseRunner;

    /**
     * RequestRunner is called from the @When step from a subclass which
     * represents cucumber test scenario('s).
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
    protected void requestRunner(final TestStepStatus testStepStatus, final Map<String, String> propertiesMap,
            final String testCaseNameRequest, final String testCaseXml, final String testSuiteXml) throws Throwable {

        propertiesMap.put("ServiceEndpoint", this.serviceEndpoint);

        this.testCase = this.wsdlProjectFactory.createWsdlTestCase(testSuiteXml, testCaseXml);
        this.assertRequest(testCaseNameRequest, testCaseXml, testSuiteXml);

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, propertiesMap,
                testCaseNameRequest);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        assertEquals(testStepStatus, runTestStepByNameResult.getStatus());

        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        final MessageExchange messageExchange = (MessageExchange) wsdlTestCaseRunner.getResults().get(0);
        this.request = messageExchange.getRequestContent();
        this.response = messageExchange.getResponseContent();
    }

    /**
     * Here we check if the xml fragment does contain the tags to be used in the
     * test. If not this is logged. Probably the test will fail later on.
     *
     * @param testCaseNameRequest
     * @param testCaseXml
     * @param testSuiteXml
     */
    private void assertRequest(final String testCaseNameRequest, final String testCaseXml, final String testSuiteXml) {
        final WsdlTestCase wsdlTestcase = (WsdlTestCase) this.testCase;
        final String xml = wsdlTestcase.getConfig().toString();
        final boolean flag1 = xml.indexOf(testCaseNameRequest) > 0;
        final boolean flag2 = xml.indexOf(testCaseXml) > 0;
        final boolean flag3 = xml.indexOf(testSuiteXml) > 0;
        if (!flag1 || !flag2 || !flag3) {
            this.LOGGER.error(String.format(ERRMSG, xml, testSuiteXml, testCaseXml, testCaseNameRequest));
        }
    }

    /**
     * ResponseRunner is called from the @Then step from a subclass which
     * represents cucumber test scenario('s) and returns the response.
     *
     * @param propertiesMap
     *            includes all needed properties for a specific test run such as
     *            DeviceId, OrganisationId and CorrelationUid
     * @param testCaseNameResponse
     *            is the specific testcase response step to be executed
     * @param logger
     *            saves the response message in a logger
     * @throws Throwable
     */
    protected void responseRunner(final Map<String, String> propertiesMap, final String testCaseNameResponse,
            final Logger logger) throws Throwable {

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, propertiesMap,
                testCaseNameResponse);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        this.response = ((MessageExchange) wsdlTestCaseRunner.getResults().get(0)).getResponseContent();
        logger.info(testCaseNameResponse + " response {}", this.response);
    }
}
