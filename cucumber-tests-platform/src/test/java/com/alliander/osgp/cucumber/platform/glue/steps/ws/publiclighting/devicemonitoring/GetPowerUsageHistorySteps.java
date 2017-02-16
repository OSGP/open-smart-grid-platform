/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.publiclighting.devicemonitoring;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.PowerUsageData;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.RelayData;
import com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.TimePeriod;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.publiclighting.PublicLightingDeviceMonitoringClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the get power usage history requests steps
 */
public class GetPowerUsageHistorySteps {

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private PublicLightingDeviceMonitoringClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPowerUsageHistorySteps.class);

    /**
     * Sends a Get Power Usage history request to the platform for a given
     * device identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a get power usage history request$")
    public void receivingAGetPowerUsageHistoryRequest(final Map<String, String> requestParameters) throws Throwable {

        final GetPowerUsageHistoryRequest request = new GetPowerUsageHistoryRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        final TimePeriod tp = new TimePeriod();
        tp.setStartTime(DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(DateTime.parse(getString(requestParameters, Keys.FROM_DATE))
                        .toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        tp.setEndTime(DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(DateTime.parse(getString(requestParameters, Keys.UNTIL_DATE))
                        .toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        request.setTimePeriod(tp);

        request.setHistoryTermType(getEnum(requestParameters, Keys.HISTORY_TERM_TYPE, HistoryTermType.class,
                Defaults.DEFAULT_HISTORY_TERM_TYPE));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.getPowerUsageHistory(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @When("^receiving a get power usage history request as an unknown organization$")
    public void receivingAGetPowerUsageHistoryRequestAsAnUnknownOrganization(
            final Map<String, String> requestParameters) throws Throwable {
        // Force the request being sent to the platform as a given organization.
        ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

        this.receivingAGetPowerUsageHistoryRequest(requestParameters);
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
    @Then("^the get power usage history async response contains$")
    public void theGetPowerUsageHistoryAsyncResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {

        final GetPowerUsageHistoryAsyncResponse response = (GetPowerUsageHistoryAsyncResponse) ScenarioContext.Current()
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

    @Then("^the get power usage history response contains soap fault$")
    public void theGetPowerUsageHistoryResponseContainsSoapFault(final Map<String, String> expectedResponseData) {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }

    /**
     * The platform should receive a get power usage history response message.
     *
     * @param deviceIdentification
     * @param expectedResult
     * @throws Throwable
     */
    @Then("^the platform buffers a get power usage history response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetPowerUsageHistoryResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final GetPowerUsageHistoryAsyncRequest request = new GetPowerUsageHistoryAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        GetPowerUsageHistoryResponse response = null;
        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > this.configuration.getTimeout()) {
                Assert.fail("Timeout");
            }

            count++;
            Thread.sleep(1000);

            response = this.client.getGetPowerUsageHistoryResponse(request);
            success = true;
        }

        Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_STATUS)),
                response.getResult());
        final String expectedDescription = expectedResult.get(Keys.KEY_DESCRIPTION);
        if (!expectedDescription.isEmpty()) {
            Assert.assertEquals(expectedDescription, response.getDescription());
        }

        for (final PowerUsageData data : response.getPowerUsageData()) {
            Assert.assertEquals(expectedResult.get(Keys.ACTUAL_CONSUMED_POWER), data.getActualConsumedPower());
            Assert.assertEquals(expectedResult.get(Keys.TOTAL_CONSUMED_ENERGY), data.getTotalConsumedEnergy());
            Assert.assertEquals(Enum.valueOf(MeterType.class, expectedResult.get(Keys.METER_TYPE)),
                    data.getMeterType());
            Assert.assertEquals(expectedResult.get(Keys.RECORD_TIME), data.getRecordTime());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.TOTAL_LIGHTING_HOURS),
                    data.getPsldData().getTotalLightingHours());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.ACTUAL_CURRENT1),
                    data.getSsldData().getActualCurrent1());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.ACTUAL_CURRENT2),
                    data.getSsldData().getActualCurrent2());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.ACTUAL_CURRENT3),
                    data.getSsldData().getActualCurrent3());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.ACTUAL_POWER1),
                    data.getSsldData().getActualPower1());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.ACTUAL_POWER2),
                    data.getSsldData().getActualPower2());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.ACTUAL_POWER3),
                    data.getSsldData().getActualPower3());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.AVERAGE_POWER_FACTOR1),
                    data.getSsldData().getAveragePowerFactor1());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.AVERAGE_POWER_FACTOR2),
                    data.getSsldData().getAveragePowerFactor2());
            Assert.assertEquals((int) getInteger(expectedResult, Keys.AVERAGE_POWER_FACTOR3),
                    data.getSsldData().getAveragePowerFactor3());

            final List<RelayData> relayDataList = data.getSsldData().getRelayData();
            if (relayDataList != null && !relayDataList.isEmpty()) {
                final String[] expectedRelayData = getString(expectedResult, Keys.RELAY_DATA)
                        .split(Keys.SEPARATOR_SEMICOLON);
                Assert.assertEquals(expectedRelayData.length, relayDataList.size());

                for (int i = 0; i < expectedRelayData.length; i++) {
                    final String sRelayData = String.format("%d,%d", relayDataList.get(i).getIndex(),
                            relayDataList.get(i).getTotalLightingMinutes());
                    Assert.assertEquals(expectedRelayData[i], sRelayData);
                }
            }
        }
    }
}