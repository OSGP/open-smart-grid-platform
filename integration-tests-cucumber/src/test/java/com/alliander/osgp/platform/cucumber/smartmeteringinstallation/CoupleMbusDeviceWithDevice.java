/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringinstallation;

import static org.junit.Assert.assertTrue;

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
    }

    @Given("^a free MBUS channel (\\d+)$")
    public void aFreeMBUSChannel(int channel) {
        // TODO check if channel is free on device
    }

    @When("^the Link G-meter request is received$")
    public void theLinkGMeterRequestIsReceived() {
        // TODO link g-meter with device
    }

    @Then("^the couple mbus device request response should be ok$")
    public void theCoupleMbusDeviceRequestResponseShouldBeOk() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        Assert.assertFalse(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_NOT_OK_RESULT));
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
    }

    @When("^the couple mbus device \"([^\"]*)\" with device \"([^\"]*)\" on channel (\\d+) is received$")
    public void theCoupleMbusDeviceWithDeviceOnChannelIsReceived(String mbusDeviceId, String deviceId, int channel)
            throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_G_LABEL, mbusDeviceId);
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, deviceId);
        PROPERTIES_MAP.put(CHANNEL_LABEL, String.valueOf(channel));
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @And("^the mbus device \"([^\"]*)\" should be linked to device \"([^\"]*)\"$")
    public void theGasDeviceShouldBeLinkedToDevice(String mbusDeviceId, String deviceId) throws Throwable {
        Assert.assertTrue(this.coupleDeviceHooks.areDevicesCoupled(deviceId, mbusDeviceId));
    }

    @Then("^the response \"([^\"]*)\" should be given$")
    public void theResponseShouldBeGiven(String status) throws Throwable {
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, status));
    }

    @When("^mbus device \"([^\"]*)\" is not coupled with device \"([^\"]*)\"$")
    public void mbusDeviceIsNotCoupledWithDevice(String mbusDeviceId, String deviceId) throws Throwable {
        this.coupleDeviceHooks.deCoupleDevices(deviceId, mbusDeviceId);
    }

    @When("^mbus device \"([^\"]*)\" is coupled with device \"([^\"]*)\" on MBUS channel (\\d+)$")
    public void mbusDeviceIsCoupledWithDeviceOnMBUSChannel(String mbusDeviceId, String deviceId, int channel)
            throws Throwable {
        this.coupleDeviceHooks.coupleDevices(deviceId, mbusDeviceId, channel);
    }

    @Then("^the mbus device \"([^\"]*)\" shouldn't be linked to the device \"([^\"]*)\"$")
    public void theMbusDeviceShouldNotBeLinkedToTheDevice(String mbusDeviceId, String deviceId) throws Throwable {
        Assert.assertFalse(this.coupleDeviceHooks.areDevicesCoupled(deviceId, mbusDeviceId));
    }

    @When("^device with DeviceID \"([^\"]*)\" is inactive$")
    public void mbusDeviceWithDeviceIDIsInactive(String mbusDeviceId) throws Throwable {
        this.coupleDeviceHooks.inactivateDevice(mbusDeviceId);
    }

}
