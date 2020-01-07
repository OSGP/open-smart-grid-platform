/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.devicemonitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.GetPowerUsageHistoryResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.MeterType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.PowerUsageData;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.RelayData;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.TimePeriod;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingDeviceMonitoringClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the get power usage history requests steps
 */
public class GetPowerUsageHistorySteps {

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
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        final TimePeriod tp = new TimePeriod();
        tp.setStartTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                DateTime.parse(getString(requestParameters, PlatformPubliclightingKeys.FROM_DATE))
                        .toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        tp.setEndTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                DateTime.parse(getString(requestParameters, PlatformPubliclightingKeys.UNTIL_DATE))
                        .toDateTime(DateTimeZone.UTC).toGregorianCalendar()));
        request.setTimePeriod(tp);

        request.setHistoryTermType(getEnum(requestParameters, PlatformPubliclightingKeys.HISTORY_TERM_TYPE,
                HistoryTermType.class, PlatformPubliclightingDefaults.DEFAULT_HISTORY_TERM_TYPE));

        try {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE,
                    this.client.getPowerUsageHistory(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
        }
    }

    @When("^receiving a get power usage history request as an unknown organization$")
    public void receivingAGetPowerUsageHistoryRequestAsAnUnknownOrganization(
            final Map<String, String> requestParameters) throws Throwable {
        // Force the request being sent to the platform as a given organization.
        ScenarioContext.current().put(PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
                "unknown-organization");

        this.receivingAGetPowerUsageHistoryRequest(requestParameters);
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @apiNote The response will contain the correlation uid, so store that in
     *          the current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the get power usage history async response contains$")
    public void theGetPowerUsageHistoryAsyncResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {

        final GetPowerUsageHistoryAsyncResponse asyncResponse = (GetPowerUsageHistoryAsyncResponse) ScenarioContext
                .current().get(PlatformPubliclightingKeys.RESPONSE);

        assertThat(asyncResponse.getAsyncResponse().getCorrelationUid()).isNotNull();
        assertThat(asyncResponse.getAsyncResponse().getDeviceId())
                .isEqualTo(getString(expectedResponseData, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION));

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(asyncResponse.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: ["
                + ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the get power usage history response contains soap fault$")
    public void theGetPowerUsageHistoryResponseContainsSoapFault(final Map<String, String> expectedResponseData) {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }

    /**
     * The platform should receive a get power usage history response message.
     *
     * @throws Throwable
     */
    @Then("^the platform buffers a get power usage history response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetPowerUsageHistoryResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final GetPowerUsageHistoryAsyncRequest request = new GetPowerUsageHistoryAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid(
                (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        final GetPowerUsageHistoryResponse response = Wait.untilAndReturn(() -> {
            final GetPowerUsageHistoryResponse retval = this.client.getGetPowerUsageHistoryResponse(request);
            assertThat(retval).isNotNull();
            assertThat(retval.getResult()).isEqualTo(
                    Enum.valueOf(OsgpResultType.class, expectedResult.get(PlatformPubliclightingKeys.KEY_STATUS)));

            return retval;
        });

        if (expectedResult.containsKey(PlatformPubliclightingKeys.KEY_DESCRIPTION)) {
            assertThat(response.getDescription())
                    .isEqualTo(getString(expectedResult, PlatformPubliclightingKeys.KEY_DESCRIPTION,
                            PlatformPubliclightingDefaults.DEFAULT_PUBLICLIGHTING_DESCRIPTION));
        }

        for (final PowerUsageData data : response.getPowerUsageData()) {
            assertThat(data.getActualConsumedPower())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_CONSUMED_POWER, 0));
            assertThat(data.getTotalConsumedEnergy())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.TOTAL_CONSUMED_ENERGY, 0));

            // Note: This piece of code has been made because there are multiple
            // enumerations with the name MeterType, but not all of them has all
            // values the same. Some with underscore and some without.
            final String meterType = getString(expectedResult, PlatformPubliclightingKeys.METER_TYPE);
            if (data.getMeterType().toString().contains("_") && !meterType.contains("_")) {
                final String[] sMeterTypeArray = meterType.split("");
                assertThat(data.getMeterType().toString()).isEqualTo(sMeterTypeArray[0] + "_" + sMeterTypeArray[1]);
            } else {
                assertThat(data.getMeterType()).isEqualTo(getEnum(expectedResult, PlatformPubliclightingKeys.METER_TYPE,
                        MeterType.class, PlatformPubliclightingDefaults.DEFAULT_METER_TYPE));
            }
            assertThat(data.getRecordTime()).isEqualTo(DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar((getDate(expectedResult, PlatformPubliclightingKeys.RECORD_TIME))
                            .toDateTime(DateTimeZone.UTC).toGregorianCalendar()));

            assertThat(data.getPsldData().getTotalLightingHours())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.TOTAL_LIGHTING_HOURS));
            assertThat(data.getSsldData().getActualCurrent1())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_CURRENT1));
            assertThat(data.getSsldData().getActualCurrent2())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_CURRENT2));
            assertThat(data.getSsldData().getActualCurrent3())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_CURRENT3));
            assertThat(data.getSsldData().getActualPower1())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_POWER1));
            assertThat(data.getSsldData().getActualPower2())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_POWER2));
            assertThat(data.getSsldData().getActualPower3())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.ACTUAL_POWER3));
            assertThat(data.getSsldData().getAveragePowerFactor1())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.AVERAGE_POWER_FACTOR1));
            assertThat(data.getSsldData().getAveragePowerFactor2())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.AVERAGE_POWER_FACTOR2));
            assertThat(data.getSsldData().getAveragePowerFactor3())
                    .isEqualTo((int) getInteger(expectedResult, PlatformPubliclightingKeys.AVERAGE_POWER_FACTOR3));

            final List<RelayData> relayDataList = data.getSsldData().getRelayData();
            if (relayDataList != null && !relayDataList.isEmpty()) {
                final String[] expectedRelayData = getString(expectedResult, PlatformPubliclightingKeys.RELAY_DATA)
                        .split(PlatformPubliclightingKeys.SEPARATOR_SEMICOLON);
                assertThat(relayDataList.size()).isEqualTo(expectedRelayData.length);

                for (int i = 0; i < expectedRelayData.length; i++) {
                    final String sRelayData = String.format("%d,%d", relayDataList.get(i).getIndex(),
                            relayDataList.get(i).getTotalLightingMinutes());
                    assertThat(sRelayData).isEqualTo(expectedRelayData[i]);
                }
            }
        }
    }
}
