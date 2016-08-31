/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringinstallation;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.hooks.CoupleDeviceHooks;
import com.alliander.osgp.platform.cucumber.smartmeteringmonitoring.ActualMeterReadsGas;
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

    private static final String XPATH_MATCHER_RESULT = "OK";
    private static final String XPATH_MATCHER_NOT_OK_RESULT = "NOT OK";

    private static final String TEST_SUITE_XML = "SmartmeterInstallation";
    private static final String TEST_CASE_XML = "637 Couple M-Bus Device";
    private static final String TEST_CASE_NAME_REQUEST = "CoupleMbusDevice";
    private static final String TEST_CASE_NAME_RESPONSE = "GetCoupleMbusDeviceResponse";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsGas.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private OrganisationId organisationId;

    @Autowired
    private CoupleDeviceHooks coupleDeviceHooks;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @Autowired
    private DeviceId deviceId;

    @Given("^an uncoupled gas device with DeviceID \"([^\"]*)\"$")
    public void anUncoupledGasDeviceWithDeviceID(String gasDevice) {
        this.deviceId.setDeviceIdG(gasDevice);
        this.coupleDeviceHooks.deCoupleDevices(this.deviceId.getDeviceIdE(), gasDevice);
    }

    @Given("^a free MBUS channel (\\d+)$")
    public void aFreeMBUSChannel(Short channel) {
        this.deviceId.setGasDeviceChannel(channel);
        this.coupleDeviceHooks.clearChannelForSmartMeterDevice(this.deviceId.getDeviceIdG());
    }

    @When("^the Link G-meter request is received$")
    public void theLinkGMeterRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_G_LABEL, this.deviceId.getDeviceIdG());
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(CHANNEL_LABEL, String.valueOf(this.deviceId.getGasDeviceChannel()));
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);

    }

    @Then("^the couple mbus device request response should be ok$")
    public void theCoupleMbusDeviceRequestResponseShouldBeOk() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        Assert.assertFalse(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_NOT_OK_RESULT));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
    }

    @And("^the gas device \"([^\"]*)\" should be linked to device \"([^\"]*)\" on MBUS channel (\\d+)$")
    public void theGasDeviceShouldBeLinkedToDevice(String mbusDeviceId, String deviceId, Short mbusChannel)
            throws Throwable {

        Assert.assertTrue(this.coupleDeviceHooks.areDevicesCoupled(deviceId, mbusDeviceId, mbusChannel));
    }

    @Then("^the response \"([^\"]*)\" should be given$")
    public void theResponseShouldBeGiven(String status) throws Throwable {

        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, status));
    }

    @When("^mbus device \"([^\"]*)\" is not coupled with device \"([^\"]*)\"$")
    public void mbusDeviceIsNotCoupledWithDevice(String mbusDeviceId, String deviceId) throws Throwable {
        this.coupleDeviceHooks.deCoupleDevices(deviceId, mbusDeviceId);
    }

    @Then("^the mbus device \"([^\"]*)\" shouldn't be linked to the device \"([^\"]*)\"$")
    public void theMbusDeviceShouldNotBeLinkedToTheDevice(String mbusDeviceId, String deviceId) throws Throwable {
        Assert.assertFalse(this.coupleDeviceHooks.areDevicesCoupled(deviceId, mbusDeviceId));
    }

    @When("^device with DeviceID \"([^\"]*)\" is inactive$")
    public void mbusDeviceWithDeviceIDIsInactive(String mbusDeviceId) throws Throwable {
        this.coupleDeviceHooks.inactivateDevice(mbusDeviceId);
    }

    @And("^a coupled gas device \"([^\"]*)\" on MBUS channel (\\d+)$")
    public void aCoupledGasDeviceOnMBUSChannel(String gasDevice, Short channel) throws Throwable {
        this.deviceId.setGasDeviceChannel(channel);
        this.coupleDeviceHooks.coupleDevices(this.deviceId.getDeviceIdE(), gasDevice, channel);
    }

    @Then("^the gas device \"([^\"]*)\" should be linked to the device \"([^\"]*)\"$")
    public void theGasDeviceShouldBeLinkedToTheDevice(String gasDevice, String device) throws Throwable {
        Assert.assertTrue(this.coupleDeviceHooks.areDevicesCoupled(device, gasDevice,
                this.deviceId.getGasDeviceChannel()));
    }

    @Then("^the gas device \"([^\"]*)\" shouldn't be linked to the device \"([^\"]*)\"$")
    public void theGasDeviceShouldnTBeLinkedToTheDevice(String gasDevice, String device) throws Throwable {
        Assert.assertFalse(this.coupleDeviceHooks.areDevicesCoupled(device, gasDevice));
    }
}
