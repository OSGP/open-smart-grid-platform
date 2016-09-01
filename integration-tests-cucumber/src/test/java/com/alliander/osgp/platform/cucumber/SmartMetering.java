/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.support.OrganisationId;
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

/**
 * Super class for TestCase runner implementations. Each Runner will be called
 * from a subclass.
 */
public class SmartMetering extends SoapUiRunner {
    private final Logger LOGGER = LoggerFactory.getLogger(SmartMetering.class);
    
    protected static final String XPATH_MATCHER_CORRELATIONUID = "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}";
    protected static final String DEVICE_IDENTIFICATION_E_LABEL = "DeviceIdentificationE";
    protected static final String DEVICE_IDENTIFICATION_G_LABEL = "DeviceIdentificationG";
    protected static final String ORGANISATION_IDENTIFICATION_LABEL = "OrganisationIdentification";
    public final static String CORRELATION_UID_LABEL = "CorrelationUid";

    protected String correlationUid;
    private Pattern correlationUidPattern;
    private Matcher correlationUidMatcher;

    @Autowired
    private OrganisationId organisationId;

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
    protected void requestRunner(final Map<String, String> propertiesMap, final String testCaseNameRequest,
            final String testCaseXml, final String testSuiteXml) throws Throwable {

    	super.requestRunner(propertiesMap, testCaseNameRequest, testCaseXml, testSuiteXml);

    	this.correlationUidPattern = Pattern
                .compile(this.organisationId.getOrganisationId() + XPATH_MATCHER_CORRELATIONUID);
        
    	this.correlationUidMatcher = this.correlationUidPattern.matcher(this.response);
        assertTrue(this.correlationUidMatcher.find());
        this.correlationUid = this.correlationUidMatcher.group();
    }
}
