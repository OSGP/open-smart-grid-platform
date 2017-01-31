/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.basicosgpfunctions;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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
import com.alliander.osgp.platform.cucumber.config.CoreDeviceConfiguration;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.GenericResponseSteps;
import com.alliander.osgp.platform.cucumber.support.ws.core.CoreConfigurationManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the firmware requests steps
 */
public class AuthorizeDeviceFunctions {
    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private CoreConfigurationManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeDeviceFunctions.class);

    // Given a registered device
    // | DeviceIdentification | TEST1024000000001 |
    // And an organization
    // | OrganizationIdentification | test-org |
    // | FunctionGroup | |
    // When receiving a device function request
    // | DeviceFunction | |
    // Then the device function response is successful

    /**
     * Sends a Get Configuration request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a get configuration request$")
    public void receivingAGetConfigurationRequest(final Map<String, String> requestParameters) throws Throwable {
        final GetConfigurationRequest request = new GetConfigurationRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.getConfiguration(request));
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
    @Then("^the get configuration async response contains$")
    public void theGetConfigurationResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final GetConfigurationAsyncResponse response = (GetConfigurationAsyncResponse) ScenarioContext.Current()
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
    public void thePlatformBufferesAGetConfigurationResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResponseData) throws Throwable {
        final GetConfigurationAsyncRequest request = new GetConfigurationAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        GetConfigurationResponse response = null;

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > this.configuration.getTimeout()) {
                throw new TimeoutException();
            }

            count++;
            Thread.sleep(1000);

            response = this.client.getGetConfiguration(request);

            if (!expectedResponseData.containsKey(Keys.KEY_RESULT)
                    || getEnum(expectedResponseData, Keys.KEY_RESULT, OsgpResultType.class) != response.getResult()) {
                continue;
            }

            success = true;
        }

        final Configuration configuration = response.getConfiguration();

        if (configuration == null) {
            Assert.assertNotNull(configuration);
        }

        if (expectedResponseData.containsKey(Keys.KEY_LIGHTTYPE)
                && !expectedResponseData.get(Keys.KEY_LIGHTTYPE).isEmpty() && configuration.getLightType() != null) {
            Assert.assertEquals(getEnum(expectedResponseData, Keys.KEY_LIGHTTYPE, LightType.class),
                    configuration.getLightType());
        }

        final DaliConfiguration daliConfiguration = configuration.getDaliConfiguration();
        if (daliConfiguration != null) {

            if (expectedResponseData.containsKey(Keys.DC_LIGHTS) && !expectedResponseData.get(Keys.DC_LIGHTS).isEmpty()
                    && daliConfiguration.getNumberOfLights() != 0) {
                Assert.assertEquals((int) getInteger(expectedResponseData, Keys.DC_LIGHTS),
                        daliConfiguration.getNumberOfLights());
            }

            if (expectedResponseData.containsKey(Keys.DC_MAP) && !expectedResponseData.get(Keys.DC_MAP).isEmpty()
                    && daliConfiguration.getIndexAddressMap() != null) {
                final List<IndexAddressMap> indexAddressMapList = daliConfiguration.getIndexAddressMap();
                final String[] dcMapArray = getString(expectedResponseData, Keys.DC_MAP).split(";");
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

            if (expectedResponseData.containsKey(Keys.RC_MAP) && !expectedResponseData.get(Keys.RC_MAP).isEmpty()
                    && relayConfiguration.getRelayMap() != null) {
                final List<RelayMap> relayMapList = relayConfiguration.getRelayMap();
                final String[] rcMapArray = getString(expectedResponseData, Keys.RC_MAP).split(";");
                for (int i = 0; i < rcMapArray.length; i++) {
                    final String[] rcMapArrayElements = rcMapArray[i].split(",");
                    if (rcMapArrayElements.length > 0 && relayMapList.size() > 0) {
                        Assert.assertEquals(Integer.parseInt(rcMapArrayElements[0]), relayMapList.get(i).getIndex());
                        Assert.assertEquals(Integer.parseInt(rcMapArrayElements[1]), relayMapList.get(i).getAddress());

                        if (expectedResponseData.containsKey(Keys.KEY_RELAY_TYPE)
                                && !expectedResponseData.get(Keys.KEY_RELAY_TYPE).isEmpty()
                                && relayMapList.get(i).getRelayType() != null) {
                            Assert.assertEquals(getEnum(expectedResponseData, Keys.KEY_RELAY_TYPE, RelayType.class),
                                    relayMapList.get(i).getRelayType());
                        }
                    }
                }
            }
        }

        //// Note: This information isn't added in the fitnesse test, how to
        //// test this?
        // configuration.getRelayLinking();

        if (expectedResponseData.containsKey(Keys.KEY_PREFERRED_LINKTYPE)
                && !expectedResponseData.get(Keys.KEY_PREFERRED_LINKTYPE).isEmpty()
                && configuration.getPreferredLinkType() != null) {
            Assert.assertEquals(getEnum(expectedResponseData, Keys.KEY_PREFERRED_LINKTYPE, LinkType.class),
                    configuration.getPreferredLinkType());
        }

        // Note: This piece of code has been made because there are multiple
        // enumerations with the name MeterType, but not all of them has all
        // values the same. Some with underscore and some without.s

        if (expectedResponseData.containsKey(Keys.METER_TYPE) && !expectedResponseData.get(Keys.METER_TYPE).isEmpty()
                && configuration.getMeterType() != null) {
            MeterType meterType = null;
            final String sMeterType = getString(expectedResponseData, Keys.METER_TYPE);
            if (!sMeterType.toString().contains("_")
                    && sMeterType.equals(MeterType.P_1.toString().replaceAll("_", ""))) {
                final String[] sMeterTypeArray = sMeterType.toString().split("");
                meterType = MeterType.valueOf(sMeterTypeArray[0] + "_" + sMeterTypeArray[1]);
            } else {
                meterType = getEnum(expectedResponseData, Keys.METER_TYPE, MeterType.class);
            }
            Assert.assertEquals(meterType, configuration.getMeterType());
        }

        if (expectedResponseData.containsKey(Keys.SHORT_INTERVAL)
                && !expectedResponseData.get(Keys.SHORT_INTERVAL).isEmpty()
                && configuration.getShortTermHistoryIntervalMinutes() != null) {
            Assert.assertEquals(getInteger(expectedResponseData, Keys.SHORT_INTERVAL, Defaults.SHORT_INTERVAL),
                    configuration.getShortTermHistoryIntervalMinutes());
        }

        if (expectedResponseData.containsKey(Keys.LONG_INTERVAL)
                && !expectedResponseData.get(Keys.LONG_INTERVAL).isEmpty()
                && configuration.getLongTermHistoryInterval() != null) {
            Assert.assertEquals(getInteger(expectedResponseData, Keys.LONG_INTERVAL, Defaults.LONG_INTERVAL),
                    configuration.getLongTermHistoryInterval());
        }

        if (expectedResponseData.containsKey(Keys.INTERVAL_TYPE)
                && !expectedResponseData.get(Keys.INTERVAL_TYPE).isEmpty()
                && configuration.getLongTermHistoryIntervalType() != null) {
            Assert.assertEquals(getEnum(expectedResponseData, Keys.INTERVAL_TYPE, LongTermIntervalType.class),
                    configuration.getLongTermHistoryIntervalType());
        }
    }

    @Then("^the platform buffers a get configuration response message for device \"([^\"]*)\" contains soap fault$")
    public void thePlatformBufferesAGetConfigurationResponseMessageForDeviceContainsSoapFault(
            final String deviceIdentification, final Map<String, String> expectedResponseData) throws Throwable {
        try {
            this.thePlatformBufferesAGetConfigurationResponseMessageForDevice(deviceIdentification,
                    expectedResponseData);
        } catch (final SoapFaultClientException ex) {
            Assert.assertEquals(getString(expectedResponseData, Keys.KEY_MESSAGE), ex.getMessage());
        }
    }
}