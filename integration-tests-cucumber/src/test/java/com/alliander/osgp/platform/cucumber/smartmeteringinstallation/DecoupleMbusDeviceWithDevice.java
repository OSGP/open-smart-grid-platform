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

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DecoupleMbusDeviceWithDevice extends SmartMetering {
    private static final String PATH_RESULT = "/Envelope/Body/DeCoupleMbusDeviceResponse/Result/text()";
    private static final String PATH_DESCRIPTION = "/Envelope/Body/DeCoupleMbusDeviceResponse/Description/text()";

    private static final String XPATH_MATCHER_RESULT = "OK";
    private static final String XPATH_MATCHER_NOT_OK_RESULT = "NOT OK";

    private static final String TEST_SUITE_XML = "SmartmeterInstallation";
    private static final String TEST_CASE_XML = "638 Decouple M-Bus Device";
    private static final String TEST_CASE_NAME_REQUEST = "DecoupleMbusDevice";
    private static final String TEST_CASE_NAME_RESPONSE = "GetDecoupleMbusDeviceResponse";

    private static final Logger LOGGER = LoggerFactory.getLogger(DecoupleMbusDeviceWithDevice.class);
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

    @When("^the decouple G-meter request is received$")
    public void theDecoupleGMeterRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_G_LABEL, this.deviceId.getDeviceIdG());
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(CHANNEL_LABEL, String.valueOf(this.deviceId.getMbusChannel()));
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(TestStepStatus.OK, PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the response \"([^\"]*)\" is given to the decouple request$")
    public void theResponseShouldBeGiven(String status) throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, status));
    }

    @Then("^the decouple request response description contains \"([^\"]*)\"$")
    public void theDecoupleRequestResponseDescriptionContains(String message) throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_DESCRIPTION, message));
    }

    @Then("^the decouple request response description contains 'SmartMeter with id \"([^\"]*)\" could not be found'$")
    public void theDecoupleRequestResponseDescriptionContainsCouldNotBeFound(String message) throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);
        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_DESCRIPTION, message));

    }

    @Then("^the mbus device \"([^\"]*)\" isn't coupled to the device \"([^\"]*)\"$")
    public void theMbusDeviceIsnTCoupledToTheDevice(String mbusDeviceId, String deviceId) {
        Assert.assertFalse(this.coupleDeviceHooks.areDevicesCoupled(deviceId, mbusDeviceId));
    }

    @Given("^an inactive mbus device with DeviceID \"([^\"]*)\" on MBUS channel (\\d+)$")
    public void anInactiveMbusDeviceWithDeviceIDOnMBUSChannel(String mbusDeviceId, Short mbusDeviceChannel) {
        this.deviceId.setDeviceIdG(mbusDeviceId);
        this.deviceId.setMbusChannel(mbusDeviceChannel);
        this.deviceHooks.deactivateDevice(mbusDeviceId);
    }

    @Given("^an active mbus device with DeviceID \"([^\"]*)\" on MBUS channel (\\d+)$")
    public void anActiveMbusDeviceWithDeviceIDOnMBUSChannel(String mbusDeviceId, Short mbusDeviceChannel) {
        this.deviceId.setDeviceIdG(mbusDeviceId);
        this.deviceId.setMbusChannel(mbusDeviceChannel);
        this.deviceHooks.activateDevice(mbusDeviceId);
    }

}
