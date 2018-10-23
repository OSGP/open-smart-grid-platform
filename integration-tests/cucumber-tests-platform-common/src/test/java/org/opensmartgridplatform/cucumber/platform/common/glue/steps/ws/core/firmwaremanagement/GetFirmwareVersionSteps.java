/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.firmwaremanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleType;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareVersion;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class with all the firmware requests steps
 */
public class GetFirmwareVersionSteps {
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
        request.setDeviceIdentification(getString(requestParameters, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.getFirmwareVersion(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
        }
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @apiNote The response will contain the correlation uid, so store that in the
     *       current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the get firmware version async response contains$")
    public void theGetFirmwareVersionResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final GetFirmwareVersionAsyncResponse asyncResponse = (GetFirmwareVersionAsyncResponse) ScenarioContext
                .current().get(PlatformKeys.RESPONSE);

        Assert.assertEquals(getString(expectedResponseData, PlatformKeys.KEY_DEVICE_IDENTIFICATION),
                asyncResponse.getAsyncResponse().getDeviceId());
        Assert.assertNotNull(asyncResponse.getAsyncResponse().getCorrelationUid());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(asyncResponse.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the platform buffers a get firmware version response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetFirmwareVersionResponseMessage(final String deviceIdentification,
            final Map<String, String> expectedResponseData) throws Throwable {
        final GetFirmwareVersionAsyncRequest request = new GetFirmwareVersionAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        final GetFirmwareVersionResponse response = Wait.untilAndReturn(() -> {
            final GetFirmwareVersionResponse retval = this.client.getGetFirmwareVersion(request);
            Assert.assertNotNull(retval);
            Assert.assertEquals(getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class),
                    retval.getResult());
            return retval;
        });

        if (response.getFirmwareVersion() != null) {
            final FirmwareVersion fwv = response.getFirmwareVersion().get(0);
            if (fwv.getVersion() != null) {
                Assert.assertEquals(getString(expectedResponseData, PlatformKeys.FIRMWARE_VERSION), fwv.getVersion());
            }
            if (fwv.getFirmwareModuleType() != null) {
                Assert.assertEquals(
                        getEnum(expectedResponseData, PlatformKeys.KEY_FIRMWARE_MODULE_TYPE, FirmwareModuleType.class),
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