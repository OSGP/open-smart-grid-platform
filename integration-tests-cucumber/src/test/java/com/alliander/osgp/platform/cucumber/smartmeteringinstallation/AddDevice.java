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
import com.alliander.osgp.platform.cucumber.hooks.AddDeviceHooks;
import com.alliander.osgp.platform.cucumber.smartmeteringmonitoring.ActualMeterReadsGas;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.alliander.osgp.platform.cucumber.support.ServiceEndpoint;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AddDevice extends SmartMetering {
    private static final String PATH_RESULT = "/Envelope/Body/AddDeviceResponse/Result/text()";

    private static final String XPATH_MATCHER_RESULT = "OK";

    private static final String TEST_SUITE_XML = "SmartmeterInstallation";
    private static final String TEST_CASE_XML = "218 Retrieve AddDevice result";
    private static final String TEST_CASE_NAME_REQUEST = "AddDevice - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetAddDeviceResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsGas.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private OrganisationId organisationId;

    @Autowired
    private AddDeviceHooks addDeviceHooks;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @When("^the add device \"([^\"]*)\" request is received$")
    public void theAddDeviceRequestIsReceived(final String deviceId) throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, deviceId);
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
        PROPERTIES_MAP.put(ENDPOINT_LABEL, this.serviceEndpoint.getServiceEndpoint());

        this.requestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the device request response should be ok$")
    public void theDeviceRequestResponseShouldBeOk() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);

        this.responseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        Assert.assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
    }

    @And("^the device with id \"([^\"]*)\" should be added in the core database$")
    public void theDeviceShouldBeAddedInTheCoreDatabase(final String deviceId) throws Throwable {
        Assert.assertTrue(this.addDeviceHooks.testCoreDevice(deviceId));
    }

    @And("^the device with id \"([^\"]*)\" should be added in the dlms database$")
    public void theDeviceShouldBeAddedInTheDlmsDatabase(final String deviceId) throws Throwable {
        Assert.assertTrue(this.addDeviceHooks.testDlmsDevice(deviceId));
    }

}
