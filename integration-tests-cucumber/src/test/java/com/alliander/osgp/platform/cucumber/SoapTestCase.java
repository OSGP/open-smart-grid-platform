/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

/**
 * Super class for TestCase runner implementations using Soap. Each Runner will
 * be called from a subclass.
 */
public abstract class SoapTestCase extends SoapUiRunner {
	protected static final String XPATH_MATCHER_CORRELATIONUID = "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}";
    protected static final String DEVICE_IDENTIFICATION_E_LABEL = "DeviceIdentificationE";
    protected static final String DEVICE_IDENTIFICATION_G_LABEL = "DeviceIdentificationG";
    protected static final String ORGANISATION_IDENTIFICATION_LABEL = "OrganisationIdentification";
    protected static final String ENDPOINT_LABEL = "ServiceEndpoint";
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
    protected void requestRunner(TestStepStatus testStepStatus, final Map<String, String> propertiesMap, final String testCaseNameRequest,
            final String testCaseXml, final String testSuiteXml) throws Throwable {

    	super.requestRunner(testStepStatus, propertiesMap, testCaseNameRequest, testCaseXml, testSuiteXml);

    	this.correlationUidPattern = Pattern
                .compile(this.organisationId.getOrganisationId() + XPATH_MATCHER_CORRELATIONUID);
        
    	this.correlationUidMatcher = this.correlationUidPattern.matcher(this.response);
        assertTrue(this.correlationUidMatcher.find());
        this.correlationUid = this.correlationUidMatcher.group();
    }
}
