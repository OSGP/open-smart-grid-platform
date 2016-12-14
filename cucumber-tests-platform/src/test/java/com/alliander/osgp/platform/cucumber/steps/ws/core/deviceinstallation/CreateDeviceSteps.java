/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.common.ResponseSteps;
import com.alliander.osgp.platform.cucumber.steps.database.core.DeviceSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.core.CoreStepsBase;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class CreateDeviceSteps extends CoreStepsBase {

    protected CreateDeviceSteps() throws Throwable {
        super();
    }

    private static final String TEST_SUITE = "DeviceInstallation";
    private static final String TEST_SUITE_UPDATE = "DeviceManagement";
    private static final String TEST_CASE_NAME_ADD_DEVICE = "AT AddDevice";
    private static final String TEST_CASE_NAME_REQUEST_ADD_DEVICE = "AddDevice";
//    private static final String TEST_CASE_NAME_UPDATE_DEVICE = "AT UpdateDevice";
    private static final String TEST_CASE_NAME_UPDATE_DEVICE = "UpdateDevice TestCase";
    private static final String TEST_CASE_NAME_REQUEST_UPDATE_DEVICE = "UpdateDevice";
    private static final String DEFAULT_DEVICEUID = "";
    private static final String DEFAULT_ALIAS = "";
    private static final String DEFAULT_OWNER = "";
    private static final String DEFAULT_CONTAINER_POSTAL_CODE = "";
    private static final String DEFAULT_CONTAINER_CITY = "";
    private static final String DEFAULT_CONTAINER_STREET = "";
    private static final String DEFAULT_CONTAINER_NUMBER = "";
    private static final String DEFAULT_GPSLATITUDE = "0";
    private static final String DEFAULT_GPSLONGITUDE = "0";
    private static final String DEFAULT_HASSCHEDULE = "false";
    private static final String DEFAULT_CONTAINER_MUNICIPALITY = "";
    private static final String DEFAULT_ACTIVATED = "true";
    private static final String DEFAULT_PUBLIC_KEY_PRESENT = "false";
    private static final String DEFAULT_MANUFACTURER = "Test";
    private static final String DEFAULT_MODELCODE = "TestModel";
    private static final String DEFAULT_DESCRIPTION = "Test";
    private static final String DEFAULT_METERED = "true";

    /**
     *
     * @param requestParameters
     */
    private void fillPropertiesMap(final Map<String, String> settings) {
        PROPERTIES_MAP.put("DeviceUid", getString(settings, "DeviceUid", DEFAULT_DEVICEUID));
        PROPERTIES_MAP.put("DeviceIdentification", getString(settings, "DeviceIdentification", Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        PROPERTIES_MAP.put("Alias", getString(settings, "Alias", DEFAULT_ALIAS));
        PROPERTIES_MAP.put("Owner", getString(settings, "Owner", DEFAULT_OWNER));
        PROPERTIES_MAP.put("ContainerPostalCode", getString(settings, "ContainerPostalCode", DEFAULT_CONTAINER_POSTAL_CODE));
        PROPERTIES_MAP.put("ContainerCity", getString(settings, "ContainerCity", DEFAULT_CONTAINER_CITY));
        PROPERTIES_MAP.put("ContainerStreet", getString(settings, "ContainerStreet", DEFAULT_CONTAINER_STREET));
        PROPERTIES_MAP.put("ContainerNumber", getString(settings, "ContainerNumber", DEFAULT_CONTAINER_NUMBER));
        PROPERTIES_MAP.put("ContainerMunicipality", getString(settings, "ContainerMunicipality", DEFAULT_CONTAINER_MUNICIPALITY));
        PROPERTIES_MAP.put("GpsLatitude", getString(settings, "GpsLatitude", DEFAULT_GPSLATITUDE));
        PROPERTIES_MAP.put("GpsLongitude", getString(settings, "GpsLongitude", DEFAULT_GPSLONGITUDE));
        PROPERTIES_MAP.put("Activated", getString(settings, "Activated", DEFAULT_ACTIVATED));
        PROPERTIES_MAP.put("HasSchedule", getString(settings, "HasSchedule", DEFAULT_HASSCHEDULE));
        PROPERTIES_MAP.put("PublicKeyPresent", getString(settings, "PublicKeyPresent", DEFAULT_PUBLIC_KEY_PRESENT));
        PROPERTIES_MAP.put("Manufacturer", getString(settings, "DeviceModelManufacturer", DEFAULT_MANUFACTURER));
        PROPERTIES_MAP.put("ModelCode", getString(settings, "ModelCode", DEFAULT_MODELCODE));
        PROPERTIES_MAP.put("Description", getString(settings, "Description", DEFAULT_DESCRIPTION));
        PROPERTIES_MAP.put("Metered", getString(settings, "Metered", DEFAULT_METERED));
    }
    
    private void fillUpdatePropertiesMap(final Map<String, String> settings) {
    	fillPropertiesMap(settings);
    	
    	PROPERTIES_MAP.put("UpdateDeviceIdentification", getString(settings, "DeviceIdentification", Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    	PROPERTIES_MAP.put("NetworkAddress", getString(settings, "NetworkAddress", "0.0.0.0"));
    	PROPERTIES_MAP.put("Active", getString(settings, "Active", "false"));
    	PROPERTIES_MAP.put("internalId", getString(settings, "internalId", "0"));
    	PROPERTIES_MAP.put("externalId", getString(settings, "externalId", "0"));
    	PROPERTIES_MAP.put("relayType", getString(settings, "relayType", "null"));
    	PROPERTIES_MAP.put("code", getString(settings, "code", "0"));
    	PROPERTIES_MAP.put("Index", getString(settings, "Index", "0"));
    	PROPERTIES_MAP.put("LastKnownState", getString(settings, "LastKnownState", "false"));
//    	PROPERTIES_MAP.put("LastKnowSwitchingTime", getString(settings, "LastKnowSwitchingTime", date.toString()));
    	PROPERTIES_MAP.put("InMaintenance", getString(settings, "InMaintenance", "false"));
//    	PROPERTIES_MAP.put("TechnicalInstallationDate", getString(settings, "TechnicalInstallationDate", date.toString()));
    	PROPERTIES_MAP.put("UsePrefix", getString(settings, "UsePrefix", "false"));
    	PROPERTIES_MAP.put("Metered", getString(settings, "Metered", "false"));
    	
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, 2016);
    	cal.set(Calendar.MONTH, 12);
    	cal.set(Calendar.DAY_OF_MONTH, 7);    	
    	
    	SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
    	df.applyPattern("yyyy-MM-dd HH:mm:ss.SSS");

    	String sLastKnowSwitchingTimeDate = cal.getTime().toString();
    	String sTechnicalInstallationDate = cal.getTime().toString();
    	
    	sTechnicalInstallationDate = getString(settings, "TechnicalInstallationDate", sTechnicalInstallationDate);
    	sLastKnowSwitchingTimeDate = getString(settings, "LastKnowSwitchingTime", sLastKnowSwitchingTimeDate);
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", java.util.Locale.ENGLISH);
    	
    	if (!settings.containsKey("LastKnowSwitchingTime"))
    	{
    		try {
        		sLastKnowSwitchingTimeDate = df.format(sdf.parse(sLastKnowSwitchingTimeDate));
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    		}
    	}
    	
    	if (!settings.containsKey("TechnicalInstallationDate"))
    	{
    		try {
    			sTechnicalInstallationDate = df.format(sdf.parse(sTechnicalInstallationDate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
			}
    	}
    	
    	sLastKnowSwitchingTimeDate = sLastKnowSwitchingTimeDate.replaceAll(" ", "T");
    	sTechnicalInstallationDate = sTechnicalInstallationDate.replaceAll(" ", "T");
    	
		PROPERTIES_MAP.put("LastKnowSwitchingTime", getString(settings, "LastKnowSwitchingTime", sLastKnowSwitchingTimeDate));
		PROPERTIES_MAP.put("TechnicalInstallationDate", getString(settings, "TechnicalInstallationDate", sTechnicalInstallationDate));
    }

    /**
     *
     * @throws Throwable
     */
    @When("^receiving an add device request$")
    public void receiving_an_add_device_request(final Map<String, String> settings) throws Throwable {

        this.fillPropertiesMap(settings);

        this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST_ADD_DEVICE,
                TEST_CASE_NAME_ADD_DEVICE, TEST_SUITE);
    }
    
    /**
     * Verify the response of a add device request.
     * 
     * @param settings
     * @throws Throwable
     */
    @Then("^the add device response is successfull$")
    public void the_add_device_response_is_successfull() throws Throwable {
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, "/Envelope/Body/AddDeviceResponse", ""));
    }

    /**
     *
     * @throws Throwable
     */
    @When("^receiving an update device request")
    public void receiving_an_update_device_request(final Map<String, String> requestParameters) throws Throwable {

        this.fillUpdatePropertiesMap(requestParameters);

        this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST_UPDATE_DEVICE,
                TEST_CASE_NAME_UPDATE_DEVICE, TEST_SUITE_UPDATE);
    }

    /**
     * Verify the response of a update device request.
     * 
     * @param settings
     * @throws Throwable
     */
    @Then("^the update device response is successfull$")
    public void the_update_device_response_is_successfull() throws Throwable {
    	LoggerFactory.getLogger(CreateDeviceSteps.class).info("Request: " + this.request);
    	LoggerFactory.getLogger(CreateDeviceSteps.class).info("Response: " + this.response);
    	Assert.assertTrue(this.runXpathResult.assertXpath(this.response, "/Envelope/Body/UpdateDeviceResponse", ""));
    }
    
    /**
     * Verify that the create organization response contains the fault with the given expectedResult parameters.
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the add device response contains$")
    public void the_add_device_response_contains(Map<String, String> expectedResult) throws Throwable {
        ResponseSteps.VerifyFaultResponse(this.runXpathResult, this.response, expectedResult);
    }
    
    /**
     * Verify that the update device response contains the fault with the given expectedResult parameters.
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the update device response contains$")
    public void the_update_device_response_contains(Map<String, String> expectedResult) throws Throwable {
    	ResponseSteps.VerifyFaultResponse(this.runXpathResult, this.response, expectedResult);
    }
}