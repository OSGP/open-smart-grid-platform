/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.configurationmanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

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
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.IndexAddressMap;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LightType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LinkType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.LongTermIntervalType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayConfiguration;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayMap;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.RelayType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationResponse;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.config.PlatformApplicationConfiguration;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreConfigurationManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set configuration requests steps
 */
public class SetConfigurationSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationSteps.class);

    @Autowired
    private PlatformApplicationConfiguration configuration;

    @Autowired
    private CoreConfigurationManagementClient client;

    private void addFilledDaliConfigurationToConfiguration(final Map<String, String> requestParameters,
            final Configuration config) {
        final String dcMap = getString(requestParameters, Keys.DC_MAP);
        if (dcMap != null) {
            final DaliConfiguration daliConfiguration = new DaliConfiguration();
            final String[] daliMapArray = dcMap.split(";");
            for (final String daliMapElement : daliMapArray) {
                final String[] subDaliMapElement = daliMapElement.split(",");
                final IndexAddressMap indexAddressMap = new IndexAddressMap();
                if (!subDaliMapElement[0].isEmpty() && !subDaliMapElement[0].equals("0")) {
                    indexAddressMap.setIndex(Integer.parseInt(subDaliMapElement[0]));
                    if (subDaliMapElement[1] != null && !subDaliMapElement[1].isEmpty()
                            && !subDaliMapElement[0].equals("0")) {
                        indexAddressMap.setAddress(Integer.parseInt(subDaliMapElement[1]));
                    }
                }
                if (indexAddressMap.getIndex() != 0 || indexAddressMap.getAddress() != 0) {
                    daliConfiguration.getIndexAddressMap().add(indexAddressMap);
                }
            }

            Object dcLights = null;
            if (!getString(requestParameters, Keys.DC_LIGHTS).isEmpty()) {
                dcLights = getInteger(requestParameters, Keys.DC_LIGHTS);
                daliConfiguration.setNumberOfLights((int) dcLights);
            }

            if (dcLights != null && daliConfiguration.getIndexAddressMap().size() == (int) dcLights) {
                config.setDaliConfiguration(daliConfiguration);
            }
        }
    }

    private void addFilledRelayConfigurationToConfiguration(final Map<String, String> requestParameters,
            final Configuration config) {
        final String rcMap = getString(requestParameters, Keys.RC_MAP);
        if (rcMap != null) {
            final RelayConfiguration relayConfiguration = new RelayConfiguration();
            final RelayMap relayMap = new RelayMap();
            final String[] relayMapArray = rcMap.split(";");
            for (final String relayMapElement : relayMapArray) {
                final String[] subRelayMapElement = relayMapElement.split(",");
                if (!subRelayMapElement[0].isEmpty()) {
                    relayMap.setIndex(Integer.parseInt(subRelayMapElement[0]));
                    if (subRelayMapElement[1] != null && !subRelayMapElement[1].isEmpty()) {
                        relayMap.setAddress(Integer.parseInt(subRelayMapElement[1]));
                    }
                }
                if (requestParameters.containsKey(Keys.RC_TYPE) && !requestParameters.get(Keys.RC_TYPE).isEmpty()) {
                    relayMap.setRelayType(getEnum(requestParameters, Keys.RC_TYPE, RelayType.class));
                }
                if (relayMap.getIndex() != 0 || relayMap.getAddress() != 0 || relayMap.getRelayType() != null) {
                    relayConfiguration.getRelayMap().add(relayMap);
                }
            }
            if (relayConfiguration.getRelayMap().size() > 0) {
                config.setRelayConfiguration(relayConfiguration);
            }
        }
    }

    /**
     * Sends a Set Configuration request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set configuration request$")
    public void receivingASetConfigurationRequest(final Map<String, String> requestParameters) throws Throwable {
        final SetConfigurationRequest request = new SetConfigurationRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        final Configuration config = new Configuration();

        final LightType lightType = getEnum(requestParameters, Keys.KEY_LIGHTTYPE, LightType.class);
        config.setLightType(lightType);

        this.addFilledDaliConfigurationToConfiguration(requestParameters, config);
        this.addFilledRelayConfigurationToConfiguration(requestParameters, config);

        final LinkType preferredLinkType = getEnum(requestParameters, Keys.KEY_PREFERRED_LINKTYPE, LinkType.class);
        config.setPreferredLinkType(preferredLinkType);

        if (requestParameters.containsKey(Keys.METER_TYPE) && !requestParameters.get(Keys.METER_TYPE).isEmpty()) {
            // Note: This piece of code has been made because there are multiple
            // enumerations with the name MeterType, but not all of them has all
            // values the same. Some with underscore and some without.
            MeterType meterType = null;
            final String sMeterType = getString(requestParameters, Keys.METER_TYPE);
            if (sMeterType != null && !sMeterType.contains("_")
                    && sMeterType.equals(MeterType.P_1.toString().replace("_", ""))) {
                final String[] sMeterTypeArray = sMeterType.toString().split("");
                meterType = MeterType.valueOf(sMeterTypeArray[0] + "_" + sMeterTypeArray[1]);
            } else {
                meterType = getEnum(requestParameters, Keys.METER_TYPE, MeterType.class);
            }

            config.setMeterType(meterType);
        }
        if (requestParameters.containsKey(Keys.SHORT_INTERVAL)
                && !requestParameters.get(Keys.SHORT_INTERVAL).isEmpty()) {
            config.setShortTermHistoryIntervalMinutes(
                    getInteger(requestParameters, Keys.SHORT_INTERVAL, Defaults.SHORT_INTERVAL));
        }

        if (requestParameters.containsKey(Keys.INTERVAL_TYPE) && !requestParameters.get(Keys.INTERVAL_TYPE).isEmpty()) {
            final LongTermIntervalType intervalType = getEnum(requestParameters, Keys.INTERVAL_TYPE,
                    LongTermIntervalType.class, Defaults.INTERVAL_TYPE);
            if (requestParameters.containsKey(Keys.LONG_INTERVAL)
                    && !requestParameters.get(Keys.LONG_INTERVAL).isEmpty()) {
                config.setLongTermHistoryInterval(
                        getInteger(requestParameters, Keys.LONG_INTERVAL, Defaults.LONG_INTERVAL));
                config.setLongTermHistoryIntervalType(intervalType);
            }
        }

        request.setConfiguration(config);

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.setConfiguration(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @Then("^the platform buffers a set configuration response message for device \"([^\"]*)\"$")
    public void thePlatformBufferesASetConfigurationResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResponseData) throws Throwable {
        final SetConfigurationAsyncRequest request = new SetConfigurationAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > this.configuration.getTimeout()) {
                throw new TimeoutException();
            }

            count++;
            Thread.sleep(1000);

            final SetConfigurationResponse response = this.client.getSetConfiguration(request);

            if (!expectedResponseData.containsKey(Keys.KEY_RESULT)
                    || getEnum(expectedResponseData, Keys.KEY_RESULT, OsgpResultType.class) != response.getResult()) {
                continue;
            }

            success = true;
        }
    }

    @Then("^the platform buffers a set configuration response message for device \"([^\"]*)\" contains soap fault$")
    public void thePlatformBufferesASetConfigurationResponseMessageForDeviceContainsSoapFault(
            final String deviceIdentification, final Map<String, String> expectedResponseData) throws Throwable {
        try {
            this.thePlatformBufferesASetConfigurationResponseMessageForDevice(deviceIdentification,
                    expectedResponseData);
        } catch (final SoapFaultClientException ex) {
            Assert.assertEquals(getString(expectedResponseData, Keys.KEY_MESSAGE), ex.getMessage());
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
    @Then("^the set configuration async response contains$")
    public void theSetConfigurationResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final SetConfigurationAsyncResponse response = (SetConfigurationAsyncResponse) ScenarioContext.Current()
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
    @Then("^the set configuration async response contains soap fault$")
    public void theSetConfigurationResponseContainsSoapFault(final Map<String, String> expectedResponseData)
            throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResponseData);
    }
}