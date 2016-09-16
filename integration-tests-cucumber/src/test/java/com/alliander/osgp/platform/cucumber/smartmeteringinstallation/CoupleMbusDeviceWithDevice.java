/**
 * Copyright 2016 Smart Society Services B.V.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringinstallation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.hooks.CoupleDeviceHooks;
import com.alliander.osgp.platform.cucumber.hooks.DeviceHooks;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.alliander.osgp.platform.cucumber.support.ServiceEndpoint;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CoupleMbusDeviceWithDevice extends SmartMetering {
    private static final String PATH_RESULT = "/Envelope/Body/CoupleMbusDeviceResponse/Result/text()";
    private static final String PATH_DESCRIPTION = "/Envelope/Body/CoupleMbusDeviceResponse/Description/text()";

    private static final String XPATH_MATCHER_RESULT = "OK";
    private static final String XPATH_MATCHER_NOT_OK_RESULT = "NOT OK";

    private static final String TEST_SUITE_XML = "SmartmeterInstallation";
    private static final String TEST_CASE_XML = "637 Couple M-Bus Device";
    private static final String TEST_CASE_NAME_REQUEST = "CoupleMbusDevice";
    private static final String TEST_CASE_NAME_RESPONSE = "GetCoupleMbusDeviceResponse";

    private static final Logger LOGGER = LoggerFactory.getLogger(CoupleMbusDeviceWithDevice.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private OrganisationId organisationId;

    @Autowired
    private CoupleDeviceHooks coupleDeviceHooks;

    @Autowired
    private DeviceHooks deviceHooks;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @Autowired
    private DeviceId deviceId;

    @Given("^an active uncoupled mbus device with DeviceID \"([^\"]*)\"$")
    public void anActiveUncoupledGasDeviceWithDeviceID(String gasDevice) {
        this.deviceId.setDeviceIdG(gasDevice);
        this.deviceHooks.activateDevice(gasDevice);
        this.coupleDeviceHooks.decoupleDevices(this.deviceId.getDeviceIdE(), gasDevice);
    }

    @Given("^a free MBUS channel (\\d+)$")
    public void aFreeMBUSChannel(Short channel) {
        this.deviceId.setMbusChannel(channel);
        this.coupleDeviceHooks.clearChannelForSmartMeterDevice(this.deviceId.getDeviceIdE(), channel);
    }

    @When("^the couple G-meter request on an unknown \"([^\"]*)\" device is received")
    public void theCoupleGMeterRequestOnAnUnknownDeviceIsReceived(String unknownDevice) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_G_LABEL, this.deviceId.getDeviceIdG());
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(CHANNEL_LABEL, String.valueOf(this.deviceId.getMbusChannel()));
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @When("^the couple G-meter request on inactive device \"([^\"]*)\" is received$")
    public void theCoupleGMeterRequestOnAnInactiveDeviceIsReceived(String inactiveDevice) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_G_LABEL, this.deviceId.getDeviceIdG());
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(CHANNEL_LABEL, String.valueOf(this.deviceId.getMbusChannel()));
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the couple response contains \"([^\"]*)\"$")
    public void theResponseContains(String message) {
        final Pattern messagePattern = Pattern.compile(message);
        final Matcher messageMatcher = messagePattern.matcher(this.response);
        Assert.assertTrue(messageMatcher.find());
    }

    @When("^the couple G-meter request is received$")
    public void theCoupleGMeterRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_G_LABEL, this.deviceId.getDeviceIdG());
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(CHANNEL_LABEL, String.valueOf(this.deviceId.getMbusChannel()));
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);

    }

    @Then("^the couple mbus device request response is ok$")
    public void theCoupleMbusDeviceRequestResponseIsOk() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        Assert.assertFalse(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_NOT_OK_RESULT));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
    }

    @And("^the mbus device \"([^\"]*)\" is coupled to device \"([^\"]*)\" on MBUS channel (\\d+)$")
    public void theMbusDeviceIsCoupledToDevice(String mbusDeviceId, String deviceId, Short mbusChannel) {

        Assert.assertTrue(this.coupleDeviceHooks.areDevicesCoupled(deviceId, mbusDeviceId, mbusChannel));
    }

    @Then("^the response contains 'SmartMeter with id \"([^\"]*)\" could not be found'$")
    public void theResponseContainsSmartMeterWithIdCouldNotBeFound(String unknownDeviceName) throws Throwable {

        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_DESCRIPTION, "SmartMeter with id \""
                + unknownDeviceName + "\" could not be found"));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_NOT_OK_RESULT));
    }

    @Then("^the response description contains 'SmartMeter with id \"([^\"]*)\" could not be found'$")
    public void theResponseDescriptionContainsSmartMeterWithIdCouldNotBeFound(String message) throws Throwable {
        this.theResponseDescriptionContains(message);
    }

    @Then("^the response description contains \"([^\"]*)\"$")
    public void theResponseDescriptionContains(String message) throws Throwable {

        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_DESCRIPTION, message));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_NOT_OK_RESULT));
    }

    @When("^mbus device \"([^\"]*)\" is not coupled with device \"([^\"]*)\"$")
    public void mbusDeviceIsNotCoupledWithDevice(String mbusDeviceId, String deviceId) {
        this.coupleDeviceHooks.decoupleDevices(deviceId, mbusDeviceId);
    }

    @Then("^the mbus device \"([^\"]*)\" is not coupled to the device \"([^\"]*)\"$")
    public void theMbusDeviceIsNotCoupledToTheDevice(String mbusDeviceId, String deviceId) {
        Assert.assertFalse(this.coupleDeviceHooks.areDevicesCoupled(deviceId, mbusDeviceId));
    }

    @When("^device with DeviceID \"([^\"]*)\" is inactive$")
    public void mbusDeviceWithDeviceIDIsInactive(String mbusDeviceId) {
        this.deviceHooks.deactivateDevice(mbusDeviceId);
    }

    @And("^an active coupled mbus device \"([^\"]*)\" on MBUS channel (\\d+)$")
    public void anActiveCoupledGasDeviceOnMBUSChannel(String mbusDeviceId, Short channel) {
        this.deviceId.setMbusChannel(channel);
        this.deviceId.setDeviceIdG(mbusDeviceId);
        this.deviceHooks.activateDevice(mbusDeviceId);
        this.coupleDeviceHooks.coupleDevices(this.deviceId.getDeviceIdE(), mbusDeviceId, channel);

    }

    @Then("^the mbus device \"([^\"]*)\" is coupled to the device \"([^\"]*)\"$")
    public void theGasDeviceIsCoupledToTheDevice(String gasDevice, String device) {
        Assert.assertTrue(this.coupleDeviceHooks.areDevicesCoupled(device, gasDevice, this.deviceId.getMbusChannel()));
    }

    @Then("^the mbus device \"([^\"]*)\" isn't be coupled to the device \"([^\"]*)\"$")
    public void theGasDeviceIsNotCoupledToTheDevice(String gasDevice, String device) {
        Assert.assertFalse(this.coupleDeviceHooks.areDevicesCoupled(device, gasDevice));
    }

    @Then("^the response \"([^\"]*)\" is given$")
    public void theResponseIsGiven(String status) throws Throwable {

        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, status));
    }

}
