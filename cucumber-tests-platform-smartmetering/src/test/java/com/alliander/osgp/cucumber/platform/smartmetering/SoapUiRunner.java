/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.smartmetering.support.RunXpathResult;
import com.alliander.osgp.cucumber.platform.smartmetering.support.TestCaseResult;
import com.alliander.osgp.cucumber.platform.smartmetering.support.TestCaseRunner;
import com.alliander.osgp.cucumber.platform.smartmetering.support.WsdlProjectFactory;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

/**
 * Super class for SOAP UI runner implementations. Each Runner will be called
 * from a subclass.
 *
 * @deprecated use a WebServiceFactoryTemplate instead.
 */
@Deprecated
public abstract class SoapUiRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Helpers.class);

    @Value("${serviceEndpoint}")
    protected String serviceEndpoint;

    @Value("${certificate.basepath}")
    private String certBasePath;

    private static final String DEFAULT_SOAPUI_PROJECT = "soap-ui-project/Core-SoapUI-project.xml";
    private String soapuiProject = DEFAULT_SOAPUI_PROJECT;

    protected static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    // Labels.
    protected static final String SERVICE_ENDPOINT_LABEL = "ServiceEndpoint";
    protected static final String PATH_DEVICE_IDENTIFICATION = "//*[local-name()='DeviceIdentification']/text()";
    protected static final String PATH_CORRELATION_UID = "//*[local-name()='CorrelationUid']/text()";
    protected static final String PATH_RESULT = "//*[local-name()='Result']/text()";

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
     */
    public SoapUiRunner(final String soapUiProject) {
        this.soapuiProject = soapUiProject;
    }

    /**
     * Create the WSDL project based on the given SoapUI project.
     *
     * @throws Throwable.
     */
    @PostConstruct
    protected void init() throws Throwable {
        this.wsdlProjectFactory = new WsdlProjectFactory(this.soapuiProject, this.certBasePath, this.serviceEndpoint);
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

        LOGGER.debug("Sending request [{} => {} => {}] ...", testSuiteXml, testCaseXml, testCaseNameRequest);
        propertiesMap.put(SERVICE_ENDPOINT_LABEL, this.serviceEndpoint);

        this.testCase = this.wsdlProjectFactory.createWsdlTestCase(testSuiteXml, testCaseXml);
        this.assertRequest(testCaseNameRequest, testCaseXml, testSuiteXml);

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, propertiesMap,
                testCaseNameRequest);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();

        Assert.assertEquals(testStepStatus, runTestStepByNameResult.getStatus());

        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        final MessageExchange messageExchange = (MessageExchange) wsdlTestCaseRunner.getResults().get(0);
        this.request = messageExchange.getRequestContent();

        this.response = messageExchange.getResponseContent();

        LOGGER.debug("Got response for request [{} => {} => {}] : [{}]", testSuiteXml, testCaseXml, testCaseNameRequest,
                this.response);
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
            // this.LOGGER.error(String.format(ERRMSG, xml, testSuiteXml,
            // testCaseXml, testCaseNameRequest));
        }
    }

    /**
     * Wait for a response.
     *
     * @note In order to get the actual response from the device of the original
     *       request to the platform, we need to poll for it.
     * @param propertiesMap
     * @param testCaseResultName
     * @param testCaseResultReqXML
     * @param testSuiteXML
     * @throws Throwable
     */
    public void waitForResponse(final TestStepStatus testStepStatus, final Map<String, String> propertiesMap,
            final String testCaseResultNameRequest, final String testCaseResultReqXml, final String testSuiteXml)
            throws Throwable {
        // Wait for OK response
        int count = 0;
        do {
            if (count > 120) {
                Assert.fail("Failed to retrieve a response");
            }

            // Wait for next try to retrieve a response
            count++;
            Thread.sleep(1000);

            this.requestRunner(testStepStatus, propertiesMap, testCaseResultNameRequest, testCaseResultReqXml,
                    testSuiteXml);
        } while (!this.runXpathResult.assertXpath(this.response, PATH_RESULT, "OK"));
    }
}
