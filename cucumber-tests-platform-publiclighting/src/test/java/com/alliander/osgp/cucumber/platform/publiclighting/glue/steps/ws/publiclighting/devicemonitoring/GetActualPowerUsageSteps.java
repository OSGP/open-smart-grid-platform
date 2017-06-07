/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.devicemonitoring;

import static com.alliander.osgp.cucumber.core.Helpers.getDate;
import static com.alliander.osgp.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;

import org.joda.time.DateTimeZone;
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
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.RelayData;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import com.alliander.osgp.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import com.alliander.osgp.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingDeviceMonitoringClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the get actual power usage requests steps
 */
public class GetActualPowerUsageSteps {

    @Autowired
    private PublicLightingDeviceMonitoringClient client;

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

        final GetActualPowerUsageRequest request = new GetActualPowerUsageRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                    this.client.getActualPowerUsage(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
        }
    }

    @When("^receiving a get actual power usage request as an unknown organization$")
    public void receivingAGetActualPowerUsageRequestAsAnUnknownOrganization(final Map<String, String> requestParameters)
            throws Throwable {
        // Force the request being sent to the platform as a given organization.
        ScenarioContext.current().put(PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
                "unknown-organization");

        this.receivingAGetActualPowerUsageRequest(requestParameters);
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

        final GetActualPowerUsageAsyncResponse asyncResponse = (GetActualPowerUsageAsyncResponse) ScenarioContext
                .current().get(PlatformPubliclightingKeys.RESPONSE);

        Assert.assertNotNull(asyncResponse.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION),
                asyncResponse.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(asyncResponse.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: ["
                + ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the get actual power usage response contains soap fault$")
    public void theGetActualPowerUsageResponseContainsSoapFault(final Map<String, String> expectedResponseData) {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }

    /**
     * The platform should receive a get actual power usage response message.
     *
     * @param deviceIdentification
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the platform buffers a get actual power usage response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetActualPowerUsageResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final GetActualPowerUsageAsyncRequest request = new GetActualPowerUsageAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid(
                (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        final GetActualPowerUsageResponse response = Wait.untilAndReturn(() -> {
            final GetActualPowerUsageResponse retval = this.client.getGetActualPowerUsageResponse(request);
            Assert.assertNotNull(retval);
            Assert.assertEquals(getEnum(expectedResult, PlatformPubliclightingKeys.KEY_STATUS, OsgpResultType.class,
                    PlatformPubliclightingDefaults.DEFAULT_PUBLICLIGHTING_STATUS), retval.getResult());
            return retval;
        });

        if (expectedResult.containsKey(PlatformPubliclightingKeys.KEY_DESCRIPTION)) {
            Assert.assertEquals(
                    getString(expectedResult, PlatformPubliclightingKeys.KEY_DESCRIPTION,
                            PlatformPubliclightingDefaults.DEFAULT_PUBLICLIGHTING_DESCRIPTION),
                    response.getDescription());
        }

        final PowerUsageData data = response.getPowerUsageData();

        Assert.assertEquals((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_CONSUMED_POWER,
                PlatformPubliclightingDefaults.DEFAULT_ACTUAL_CONSUMED_POWER), data.getActualConsumedPower());
        Assert.assertEquals((int) getInteger(expectedResult, PlatformPubliclightingKeys.TOTAL_CONSUMED_ENERGY,
                PlatformPubliclightingDefaults.DEFAULT_ACTUAL_CONSUMED_ENERGY), data.getTotalConsumedEnergy());

        // Note: This piece of code has been made because there are multiple
        // enumerations with the name MeterType, but not all of them has all
        // values the same. Some with underscore and some without.
        final String meterType = getString(expectedResult, PlatformPubliclightingKeys.METER_TYPE);
        if (data.getMeterType().toString().contains("_") && !meterType.contains("_")) {
            final String[] sMeterTypeArray = meterType.split("");
            Assert.assertEquals(sMeterTypeArray[0] + "_" + sMeterTypeArray[1], data.getMeterType().toString());
        } else {
            Assert.assertEquals(getEnum(expectedResult, PlatformPubliclightingKeys.METER_TYPE, MeterType.class,
                    PlatformPubliclightingDefaults.DEFAULT_METER_TYPE), data.getMeterType());
        }

        Assert.assertEquals(DatatypeFactory.newInstance()
                .newXMLGregorianCalendar((getDate(expectedResult, PlatformPubliclightingKeys.RECORD_TIME))
                        .toDateTime(DateTimeZone.UTC).toGregorianCalendar()),
                data.getRecordTime());

        Assert.assertEquals(
                (int) getInteger(expectedResult, PlatformPubliclightingKeys.TOTAL_LIGHTING_HOURS,
                        PlatformPubliclightingDefaults.DEFAULT_TOTAL_LIGHTING_HOURS),
                data.getPsldData().getTotalLightingHours());
        Assert.assertEquals(
                (int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_CURRENT1,
                        PlatformPubliclightingDefaults.DEFAULT_ACTUAL_CURRENT1),
                data.getSsldData().getActualCurrent1());
        Assert.assertEquals(
                (int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_CURRENT2,
                        PlatformPubliclightingDefaults.DEFAULT_ACTUAL_CURRENT2),
                data.getSsldData().getActualCurrent2());
        Assert.assertEquals(
                (int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_CURRENT3,
                        PlatformPubliclightingDefaults.DEFAULT_ACTUAL_CURRENT3),
                data.getSsldData().getActualCurrent3());
        Assert.assertEquals((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_POWER1,
                PlatformPubliclightingDefaults.DEFAULT_ACTUAL_POWER1), data.getSsldData().getActualPower1());
        Assert.assertEquals((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_POWER2,
                PlatformPubliclightingDefaults.DEFAULT_ACTUAL_POWER2), data.getSsldData().getActualPower2());
        Assert.assertEquals((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_POWER3,
                PlatformPubliclightingDefaults.DEFAULT_ACTUAL_POWER3), data.getSsldData().getActualPower3());
        Assert.assertEquals(
                (int) getInteger(expectedResult, PlatformPubliclightingKeys.AVERAGE_POWER_FACTOR1,
                        PlatformPubliclightingDefaults.DEFAULT_AVERAGE_POWER_FACTOR1),
                data.getSsldData().getAveragePowerFactor1());
        Assert.assertEquals(
                (int) getInteger(expectedResult, PlatformPubliclightingKeys.AVERAGE_POWER_FACTOR2,
                        PlatformPubliclightingDefaults.DEFAULT_AVERAGE_POWER_FACTOR2),
                data.getSsldData().getAveragePowerFactor2());
        Assert.assertEquals(
                (int) getInteger(expectedResult, PlatformPubliclightingKeys.AVERAGE_POWER_FACTOR3,
                        PlatformPubliclightingDefaults.DEFAULT_AVERAGE_POWER_FACTOR3),
                data.getSsldData().getAveragePowerFactor3());

        final List<RelayData> relayDataList = data.getSsldData().getRelayData();
        if (relayDataList != null && !relayDataList.isEmpty()) {
            final String[] expectedRelayData = getString(expectedResult, PlatformPubliclightingKeys.RELAY_DATA)
                    .split(PlatformPubliclightingKeys.SEPARATOR_SEMICOLON);
            Assert.assertEquals(expectedRelayData.length, relayDataList.size());

            for (int i = 0; i < expectedRelayData.length; i++) {
                final String sRelayData = String.format("%d,%d", relayDataList.get(i).getIndex(),
                        relayDataList.get(i).getTotalLightingMinutes());
                Assert.assertEquals(expectedRelayData[i], sRelayData);
            }
        }
    }
}