/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.publiclighting.devicemonitoring;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetActualPowerUsageResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.PowerUsageData;
import com.alliander.osgp.platform.cucumber.config.CoreDeviceConfiguration;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.publiclighting.DeviceMonitoringClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the get actual power usage requests steps
 */
public class GetActualPowerUsageSteps {

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private DeviceMonitoringClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetActualPowerUsageSteps.class);

    /**
     * Sends a Get Actual Power Usage request to the platform for a given device
     * identification.
     * 
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a get actual power usage request$")
    public void receivingAGetActualPowerUsageRequest(final Map<String, String> requestParameters) throws Throwable {

        GetActualPowerUsageRequest request = new GetActualPowerUsageRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, client.getActualPowerUsage(request));
        } catch (SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @When("^receiving a get actual power usage request as an unknown organization$")
    public void receivingAGetActualPowerUsageRequestAsAnUnknownOrganization(final Map<String, String> requestParameters)
            throws Throwable {
        // Force the request being send to the platform as a given organization.
        ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

        receivingAGetActualPowerUsageRequest(requestParameters);
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
    @Then("^the get actual power usage async response contains$")
    public void theGetActualPowerUsageAsyncResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {

        GetActualPowerUsageAsyncResponse response = (GetActualPowerUsageAsyncResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION),
                response.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the get actual power usage response contains soap fault$")
    public void theGetActualPowerUsageResponseContainsSoapFault(final Map<String, String> expectedResponseData) {
        SoapFaultClientException response = (SoapFaultClientException) ScenarioContext.Current().get(Keys.RESPONSE);

        Assert.assertEquals(expectedResponseData.get(Keys.KEY_MESSAGE), response.getMessage());
    }

    /**
     * The platform should receive a get actual power usage response message.
     * @param deviceIdentification
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the platform buffers a get actual power usage response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetActualPowerUsageResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        GetActualPowerUsageAsyncRequest request = new GetActualPowerUsageAsyncRequest();
        AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        boolean success = false;
        int count = 0;
        GetActualPowerUsageResponse response = null;
        while (!success) {
            if (count > configuration.defaultTimeout) {
                Assert.fail("Timeout");
            }

            count++;
            Thread.sleep(1000);

            try {
                response = client.getGetActualPowerUsageResponse(request);
                success = true;
            } catch (Exception ex) {
                // Do nothing
                LOGGER.info(ex.getMessage());
            }
        }
        
        Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_STATUS)), response.getResult());
        String expectedDescription = expectedResult.get(Keys.KEY_DESCRIPTION);
        if (!expectedDescription.isEmpty()) {
            Assert.assertEquals(expectedDescription, response.getDescription());
        }
        
        PowerUsageData data = response.getPowerUsageData();
        
        Assert.assertEquals(expectedResult.get(Keys.ACTUALCONSUMEDPOWER), data.getActualConsumedPower());
        Assert.assertEquals(expectedResult.get(Keys.TOTALCONSUMEDENERGY), data.getTotalConsumedEnergy());
        Assert.assertEquals(Enum.valueOf(MeterType.class, expectedResult.get(Keys.METERTYPE)), data.getMeterType());
        Assert.assertEquals(expectedResult.get(Keys.RECORDTIME), data.getRecordTime());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.TOTALLIGHTINGHOURS), data.getPsldData().getTotalLightingHours());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.ACTUALCURRENT1), data.getSsldData().getActualCurrent1());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.ACTUALCURRENT2), data.getSsldData().getActualCurrent2());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.ACTUALCURRENT3), data.getSsldData().getActualCurrent3());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.ACTUALPOWER1), data.getSsldData().getActualPower1());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.ACTUALPOWER2), data.getSsldData().getActualPower2());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.ACTUALPOWER3), data.getSsldData().getActualPower3());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.AVERAGEPOWERFACTOR1), data.getSsldData().getAveragePowerFactor1());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.AVERAGEPOWERFACTOR2), data.getSsldData().getAveragePowerFactor2());
        Assert.assertEquals((int)getInteger(expectedResult, Keys.AVERAGEPOWERFACTOR3), data.getSsldData().getAveragePowerFactor3());
        
        // TODO RElaydata
    }
}