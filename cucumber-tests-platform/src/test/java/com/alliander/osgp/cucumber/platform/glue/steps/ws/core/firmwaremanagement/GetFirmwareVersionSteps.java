/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.firmwaremanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleType;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FirmwareVersion;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreFirmwareManagementClient;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class with all the firmware requests steps
 */
public class GetFirmwareVersionSteps {
    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private CoreFirmwareManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareVersionSteps.class);

    /**
     * Sends a Get Firmware Version request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @Given("^receiving a get firmware version request$")
    public void receivingAGetFirmwareVersionRequest(final Map<String, String> requestParameters) throws Throwable {

        final GetFirmwareVersionRequest request = new GetFirmwareVersionRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.getFirmwareVersion(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the
     *       current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the get firmware version async response contains$")
    public void theGetFirmwareVersionResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final GetFirmwareVersionAsyncResponse response = (GetFirmwareVersionAsyncResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        Assert.assertEquals(getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION),
                response.getAsyncResponse().getDeviceId());
        Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the platform buffers a get firmware version response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetFirmwareVersionResponseMessage(final String deviceIdentification,
            final Map<String, String> expectedResponseData) throws Throwable {
        final GetFirmwareVersionAsyncRequest request = new GetFirmwareVersionAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        GetFirmwareVersionResponse response = null;

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > this.configuration.getTimeout()) {
                Assert.fail("Timeout");
            }

            count++;
            Thread.sleep(1000);

            response = this.client.getGetFirmwareVersion(request);

            if (getEnum(expectedResponseData, Keys.KEY_RESULT, OsgpResultType.class) != response.getResult()) {
                continue;
            }

            success = true;
        }

        if (response.getFirmwareVersion() != null) {
            final FirmwareVersion fwv = response.getFirmwareVersion().get(0);
            if (fwv.getVersion() != null) {
                Assert.assertEquals(getString(expectedResponseData, Keys.FIRMWARE_VERSION), fwv.getVersion());
            }
            if (fwv.getFirmwareModuleType() != null) {
                Assert.assertEquals(
                        getEnum(expectedResponseData, Keys.KEY_FIRMWARE_MODULE_TYPE, FirmwareModuleType.class),
                        fwv.getFirmwareModuleType());
            }
        }
    }

    @Then("^the get firmware version response contains soap fault$")
    public void theGetFirmwareVersionResponseContainsSoapFault(final Map<String, String> expectedResponseData)
            throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }
}