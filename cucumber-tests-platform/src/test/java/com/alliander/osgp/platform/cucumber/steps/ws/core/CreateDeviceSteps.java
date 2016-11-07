/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.core;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.common.ResponseSteps;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class CreateDeviceSteps extends CoreStepsBase {

    protected CreateDeviceSteps() throws Throwable {
        super();
        // TODO Auto-generated constructor stub
    }

    private static final String TEST_SUITE = "DeviceInstallation";
    private static final String TEST_CASE_NAME_ADD_DEVICE = "AT AddDevice";
    private static final String TEST_CASE_NAME_REQUEST_ADD_DEVICE = "AddDevice";
    private static final String TEST_CASE_NAME_UPDATE_DEVICE = "AT UpdateDevice";
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

        this.fillPropertiesMap(requestParameters);

        this.requestRunner(TestStepStatus.UNKNOWN, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST_UPDATE_DEVICE,
                TEST_CASE_NAME_UPDATE_DEVICE, TEST_SUITE);
    }

    /**
     * Verify the response of a update device request.
     * 
     * @param settings
     * @throws Throwable
     */
    @Then("^the update device response is successfull$")
    public void the_update_device_response_is_successfull() throws Throwable {
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