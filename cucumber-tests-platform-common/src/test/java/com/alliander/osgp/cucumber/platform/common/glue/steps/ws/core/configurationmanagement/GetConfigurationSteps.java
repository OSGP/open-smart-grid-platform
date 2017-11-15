/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.core.configurationmanagement;

import static com.alliander.osgp.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.Configuration;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.DaliConfiguration;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationResponse;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.IndexAddressMap;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LightType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LinkType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LongTermIntervalType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayConfiguration;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayMap;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayType;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreConfigurationManagementClient;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the get configuration requests steps
 */
public class GetConfigurationSteps {

    @Autowired
    private CoreConfigurationManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetConfigurationSteps.class);

    /**
     * Sends a Get Configuration request to the platform for a given device
     * identification.
     *
     * @param requestParametersO
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a get configuration request$")
    public void receivingAGetConfigurationRequest(final Map<String, String> requestParameters) throws Throwable {
        final GetConfigurationRequest request = new GetConfigurationRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.getConfiguration(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
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
    @Then("^the get configuration async response contains$")
    public void theGetConfigurationResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final GetConfigurationAsyncResponse asyncResponse = (GetConfigurationAsyncResponse) ScenarioContext.current()
                .get(PlatformKeys.RESPONSE);

        Assert.assertNotNull(asyncResponse.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, PlatformKeys.KEY_DEVICE_IDENTIFICATION),
                asyncResponse.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(asyncResponse.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID) + "]");
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @throws Throwable
     */
    @Then("^the get configuration async response contains soap fault$")
    public void theGetConfigurationResponseContainsSoapFault(final Map<String, String> expectedResponseData)
            throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }

    @Then("^the platform buffers a get configuration response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetConfigurationResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResponseData) throws Throwable {
        final GetConfigurationAsyncRequest request = new GetConfigurationAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        final GetConfigurationResponse response = Wait.untilAndReturn(() -> {
            final GetConfigurationResponse retval = this.client.getGetConfiguration(request);
            Assert.assertNotNull(retval);
            Assert.assertEquals(getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class),
                    retval.getResult());
            return retval;
        });

        final Configuration configuration = response.getConfiguration();
        Assert.assertNotNull(configuration);

        if (expectedResponseData.containsKey(PlatformKeys.KEY_LIGHTTYPE)
                && !expectedResponseData.get(PlatformKeys.KEY_LIGHTTYPE).isEmpty()
                && configuration.getLightType() != null) {
            Assert.assertEquals(getEnum(expectedResponseData, PlatformKeys.KEY_LIGHTTYPE, LightType.class),
                    configuration.getLightType());
        }

        final DaliConfiguration daliConfiguration = configuration.getDaliConfiguration();
        if (daliConfiguration != null) {

            if (expectedResponseData.containsKey(PlatformKeys.DC_LIGHTS)
                    && !expectedResponseData.get(PlatformKeys.DC_LIGHTS).isEmpty()
                    && daliConfiguration.getNumberOfLights() != 0) {
                Assert.assertEquals((int) getInteger(expectedResponseData, PlatformKeys.DC_LIGHTS),
                        daliConfiguration.getNumberOfLights());
            }

            if (expectedResponseData.containsKey(PlatformKeys.DC_MAP)
                    && !expectedResponseData.get(PlatformKeys.DC_MAP).isEmpty()
                    && daliConfiguration.getIndexAddressMap() != null) {
                final List<IndexAddressMap> indexAddressMapList = daliConfiguration.getIndexAddressMap();
                final String[] dcMapArray = getString(expectedResponseData, PlatformKeys.DC_MAP).split(";");
                for (int i = 0; i < dcMapArray.length; i++) {
                    final String[] dcMapArrayElements = dcMapArray[i].split(",");
                    Assert.assertEquals(Integer.parseInt(dcMapArrayElements[0]), indexAddressMapList.get(i).getIndex());
                    Assert.assertEquals(Integer.parseInt(dcMapArrayElements[1]),
                            indexAddressMapList.get(i).getAddress());
                }
            }
        }

        final RelayConfiguration relayConfiguration = configuration.getRelayConfiguration();
        if (relayConfiguration != null) {

            if (expectedResponseData.containsKey(PlatformKeys.RELAY_CONF)
                    && !expectedResponseData.get(PlatformKeys.RELAY_CONF).isEmpty()
                    && relayConfiguration.getRelayMap() != null) {
                final List<RelayMap> relayMapList = relayConfiguration.getRelayMap();
                final String[] rcMapArray = getString(expectedResponseData, PlatformKeys.RELAY_CONF).split(";");
                for (int i = 0; i < rcMapArray.length; i++) {
                    final String[] rcMapArrayElements = rcMapArray[i].split(",");
                    if (rcMapArrayElements.length > 0 && relayMapList.size() > 0) {
                        Assert.assertEquals(Integer.parseInt(rcMapArrayElements[0]), relayMapList.get(i).getIndex());
                        Assert.assertEquals(Integer.parseInt(rcMapArrayElements[1]), relayMapList.get(i).getAddress());

                        if (expectedResponseData.containsKey(PlatformKeys.KEY_RELAY_TYPE)
                                && !expectedResponseData.get(PlatformKeys.KEY_RELAY_TYPE).isEmpty()
                                && relayMapList.get(i).getRelayType() != null) {
                            Assert.assertEquals(
                                    getEnum(expectedResponseData, PlatformKeys.KEY_RELAY_TYPE, RelayType.class),
                                    relayMapList.get(i).getRelayType());
                        }
                    }
                }
            }
        }

        //// Note: How to test this?
        // configuration.getRelayLinking();

        if (expectedResponseData.containsKey(PlatformKeys.KEY_PREFERRED_LINKTYPE)
                && !expectedResponseData.get(PlatformKeys.KEY_PREFERRED_LINKTYPE).isEmpty()
                && configuration.getPreferredLinkType() != null) {
            Assert.assertEquals(getEnum(expectedResponseData, PlatformKeys.KEY_PREFERRED_LINKTYPE, LinkType.class),
                    configuration.getPreferredLinkType());
        }

        // Note: This piece of code has been made because there are multiple
        // enumerations with the name MeterType, but not all of them has all
        // values the same. Some with underscore and some without.

        if (expectedResponseData.containsKey(PlatformKeys.METER_TYPE)
                && !expectedResponseData.get(PlatformKeys.METER_TYPE).isEmpty()
                && configuration.getMeterType() != null) {
            MeterType meterType = null;
            final String sMeterType = getString(expectedResponseData, PlatformKeys.METER_TYPE);
            if (!sMeterType.toString().contains("_")
                    && sMeterType.equals(MeterType.P_1.toString().replaceAll("_", ""))) {
                final String[] sMeterTypeArray = sMeterType.toString().split("");
                meterType = MeterType.valueOf(sMeterTypeArray[0] + "_" + sMeterTypeArray[1]);
            } else {
                meterType = getEnum(expectedResponseData, PlatformKeys.METER_TYPE, MeterType.class);
            }
            Assert.assertEquals(meterType, configuration.getMeterType());
        }

        if (expectedResponseData.containsKey(PlatformKeys.SHORT_INTERVAL)
                && !expectedResponseData.get(PlatformKeys.SHORT_INTERVAL).isEmpty()
                && configuration.getShortTermHistoryIntervalMinutes() != null) {
            Assert.assertEquals(
                    getInteger(expectedResponseData, PlatformKeys.SHORT_INTERVAL,
                            PlatformDefaults.DEFAULT_SHORT_INTERVAL),
                    configuration.getShortTermHistoryIntervalMinutes());
        }

        if (expectedResponseData.containsKey(PlatformKeys.LONG_INTERVAL)
                && !expectedResponseData.get(PlatformKeys.LONG_INTERVAL).isEmpty()
                && configuration.getLongTermHistoryInterval() != null) {
            Assert.assertEquals(getInteger(expectedResponseData, PlatformKeys.LONG_INTERVAL,
                    PlatformDefaults.DEFAULT_LONG_INTERVAL), configuration.getLongTermHistoryInterval());
        }

        if (expectedResponseData.containsKey(PlatformKeys.INTERVAL_TYPE)
                && !expectedResponseData.get(PlatformKeys.INTERVAL_TYPE).isEmpty()
                && configuration.getLongTermHistoryIntervalType() != null) {
            Assert.assertEquals(getEnum(expectedResponseData, PlatformKeys.INTERVAL_TYPE, LongTermIntervalType.class),
                    configuration.getLongTermHistoryIntervalType());
        }

        if (expectedResponseData.containsKey(PlatformKeys.OSGP_IP_ADDRESS)
                && !expectedResponseData.get(PlatformKeys.OSGP_IP_ADDRESS).isEmpty()) {
            Assert.assertEquals(getString(expectedResponseData, PlatformKeys.OSGP_IP_ADDRESS),
                    configuration.getOsgpIpAddress());
        }

        if (expectedResponseData.containsKey(PlatformKeys.OSGP_PORT)
                && !expectedResponseData.get(PlatformKeys.OSGP_PORT).isEmpty()) {
            Assert.assertEquals(getInteger(expectedResponseData, PlatformKeys.OSGP_PORT),
                    configuration.getOsgpPortNumber());
        }
    }

    @Then("^the platform buffers a get configuration response message for device \"([^\"]*)\" contains soap fault$")
    public void thePlatformBuffersAGetConfigurationResponseMessageForDeviceContainsSoapFault(
            final String deviceIdentification, final Map<String, String> expectedResponseData) throws Throwable {
        final GetConfigurationAsyncRequest request = new GetConfigurationAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        Wait.until(() -> {
            GetConfigurationResponse response = null;
            try {
                response = this.client.getGetConfiguration(request);
            } catch (final Exception e) {
                // do nothing
            }

            Assert.assertNotNull(response);
        });
    }
}
