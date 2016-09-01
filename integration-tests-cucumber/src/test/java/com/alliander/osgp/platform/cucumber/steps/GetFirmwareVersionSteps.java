/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps;

import cucumber.api.DataTable;
import cucumber.api.java.en.Then;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.platform.cucumber.SoapUiRunner;

import cucumber.api.java.en.Given;

/**
 * Class with all the firmware requests steps
 */
public class GetFirmwareVersionSteps extends SoapUiRunner {
	private final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareVersionSteps.class);
    
	private static final String SOAPUI_PROJECT = "/etc/osp/soapui/SmartMetering-soapui-project.xml";
	
	private static final String PATH_RESULT = "/Envelope/Body/GetFirmwareResponse/Result/text()";

    private static final String XPATH_MATCHER_RESULT = "OK";

    private static final String TEST_SUITE_XML = "FirmwareManagement";
    private static final String TEST_CASE_XML = "Retrieve GetFirmwareVersion result";
    private static final String TEST_CASE_NAME_REQUEST = "GetFirmwareVersion - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetFirmwareVersionResponse - Request 1";

    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();
    
    /**
     * Default constructor.
     */
    private GetFirmwareVersionSteps() {
    	super(SOAPUI_PROJECT);
    }
   
    /**
     * 
     * @throws Throwable
     */
	@Given("^receiving a get firmware version request$")
	public void givenReceivingAGetFirmwareVersionRequest(Map<String, String> requestParameters) throws Throwable {

		
		// Required parameters
		PROPERTIES_MAP.put("__DEVICE_IDENTIFICATION__", requestParameters.get("DeviceIdentification"));
	
		this.requestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
	}

	/**
	 * 
	 * @param arg1
	 * @throws Throwable
	 */
	@Then("^the get firmware version response contains$")
	public void thenTheGetFirmwareVersionResponseContains(DataTable expectedResponseParameters) throws Throwable {
		// TODO: Check response
	    //PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
	}
}