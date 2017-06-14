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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonDefaults;
import com.alliander.osgp.cucumber.platform.common.PlatformCommonKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreConfigurationManagementClient;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set configuration requests steps
 */
public class SetConfigurationSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationSteps.class);

    @Autowired
    private CoreConfigurationManagementClient client;

    private void addFilledDaliConfigurationToConfiguration(final Map<String, String> requestParameters,
            final Configuration config) throws ArgumentNullOrEmptyException {
        final DaliConfiguration daliConfiguration = new DaliConfiguration();

        if (!StringUtils.isEmpty(getString(requestParameters, PlatformKeys.DC_LIGHTS))) {
            daliConfiguration.setNumberOfLights(getInteger(requestParameters, PlatformKeys.DC_LIGHTS));
        }

        final String dcMap = getString(requestParameters, PlatformKeys.DC_MAP);
        if (dcMap != null) {
            final String[] daliMapArray = dcMap.split(";");
            for (final String daliMapElement : daliMapArray) {
                final IndexAddressMap indexAddressMap = new IndexAddressMap();

                final String[] subDaliMapElement = daliMapElement.split(",");
                if (!StringUtils.isEmpty(subDaliMapElement[0])) {
                    indexAddressMap.setIndex(Integer.parseInt(subDaliMapElement[0]));
                }

                if (subDaliMapElement.length == 2 && !StringUtils.isEmpty(subDaliMapElement[1])) {
                    indexAddressMap.setAddress(Integer.parseInt(subDaliMapElement[1]));
                }

                daliConfiguration.getIndexAddressMap().add(indexAddressMap);
            }
        }

        config.setDaliConfiguration(daliConfiguration);
    }

    private void addFilledRelayConfigurationToConfiguration(final Map<String, String> requestParameters,
            final Configuration config) throws ArgumentNullOrEmptyException {
        final String rcMap = getString(requestParameters, PlatformKeys.RC_MAP);
        if (rcMap != null) {
            final RelayConfiguration relayConfiguration = new RelayConfiguration();
            final String[] relayMapArray = rcMap.split(";");
            for (final String relayMapElement : relayMapArray) {
                final RelayMap relayMap = new RelayMap();

                final String[] subRelayMapElement = relayMapElement.split(",");

                if (!StringUtils.isEmpty(subRelayMapElement[0])) {
                    relayMap.setIndex(Integer.parseInt(subRelayMapElement[0]));
                }
                if (subRelayMapElement.length == 2 && !StringUtils.isEmpty(subRelayMapElement[1])) {
                    relayMap.setAddress(Integer.parseInt(subRelayMapElement[1]));
                }

                if (requestParameters.containsKey(PlatformKeys.RC_TYPE)
                        && !requestParameters.get(PlatformKeys.RC_TYPE).isEmpty()) {
                    relayMap.setRelayType(getEnum(requestParameters, PlatformKeys.RC_TYPE, RelayType.class));
                }

                relayConfiguration.getRelayMap().add(relayMap);
            }

            config.setRelayConfiguration(relayConfiguration);
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
        request.setDeviceIdentification(getString(requestParameters, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        final Configuration config = new Configuration();

        if (StringUtils.isNotEmpty(requestParameters.get(PlatformCommonKeys.KEY_LIGHTTYPE))) {
            final LightType lightType = getEnum(requestParameters, PlatformKeys.KEY_LIGHTTYPE, LightType.class);
            config.setLightType(lightType);
        }

        if (StringUtils.isNotEmpty(getString(requestParameters, PlatformCommonKeys.DC_LIGHTS))
                || StringUtils.isNotEmpty(getString(requestParameters, PlatformCommonKeys.DC_MAP))) {
            this.addFilledDaliConfigurationToConfiguration(requestParameters, config);
        }

        if (StringUtils.isNotEmpty(getString(requestParameters, PlatformCommonKeys.RC_TYPE))
                || StringUtils.isNotEmpty(getString(requestParameters, PlatformCommonKeys.RC_MAP))) {
            this.addFilledRelayConfigurationToConfiguration(requestParameters, config);
        }

        final LinkType preferredLinkType = getEnum(requestParameters, PlatformKeys.KEY_PREFERRED_LINKTYPE,
                LinkType.class);
        config.setPreferredLinkType(preferredLinkType);

        if (requestParameters.containsKey(PlatformKeys.METER_TYPE)
                && !requestParameters.get(PlatformKeys.METER_TYPE).isEmpty()) {
            // Note: This piece of code has been made because there are multiple
            // enumerations with the name MeterType, but not all of them has all
            // values the same. Some with underscore and some without.
            MeterType meterType = null;
            final String sMeterType = getString(requestParameters, PlatformKeys.METER_TYPE);
            if (sMeterType != null && !sMeterType.contains("_")
                    && sMeterType.equals(MeterType.P_1.toString().replace("_", ""))) {
                final String[] sMeterTypeArray = sMeterType.toString().split("");
                meterType = MeterType.valueOf(sMeterTypeArray[0] + "_" + sMeterTypeArray[1]);
            } else {
                meterType = getEnum(requestParameters, PlatformKeys.METER_TYPE, MeterType.class);
            }

            config.setMeterType(meterType);
        }
        if (requestParameters.containsKey(PlatformKeys.SHORT_INTERVAL)
                && !requestParameters.get(PlatformKeys.SHORT_INTERVAL).isEmpty()) {
            config.setShortTermHistoryIntervalMinutes(
                    getInteger(requestParameters, PlatformKeys.SHORT_INTERVAL, PlatformCommonDefaults.SHORT_INTERVAL));
        }

        if (requestParameters.containsKey(PlatformKeys.INTERVAL_TYPE)
                && !requestParameters.get(PlatformKeys.INTERVAL_TYPE).isEmpty()) {
            final LongTermIntervalType intervalType = getEnum(requestParameters, PlatformKeys.INTERVAL_TYPE,
                    LongTermIntervalType.class, PlatformCommonDefaults.INTERVAL_TYPE);
            if (requestParameters.containsKey(PlatformKeys.LONG_INTERVAL)
                    && !requestParameters.get(PlatformKeys.LONG_INTERVAL).isEmpty()) {
                config.setLongTermHistoryInterval(getInteger(requestParameters, PlatformKeys.LONG_INTERVAL,
                        PlatformCommonDefaults.LONG_INTERVAL));
                config.setLongTermHistoryIntervalType(intervalType);
            }
        }

        request.setConfiguration(config);

        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.setConfiguration(request));
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
    @Then("^the set configuration async response contains$")
    public void theSetConfigurationResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final SetConfigurationAsyncResponse asyncResponse = (SetConfigurationAsyncResponse) ScenarioContext.current()
                .get(PlatformKeys.RESPONSE);

        Assert.assertNotNull(asyncResponse.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, PlatformKeys.KEY_DEVICE_IDENTIFICATION),
                asyncResponse.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(asyncResponse.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the platform buffers a set configuration response message for device \"([^\"]*)\"$")
    public void thePlatformBufferesASetConfigurationResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResponseData) throws Throwable {
        final SetConfigurationAsyncRequest request = new SetConfigurationAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        final SetConfigurationResponse response = Wait.untilAndReturn(() -> {
            final SetConfigurationResponse retval = this.client.getSetConfiguration(request);
            Assert.assertNotNull(retval);
            return retval;
        });

        Assert.assertEquals(getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class),
                response.getResult());
    }

    @Then("^the platform buffers a set configuration response message for device \"([^\"]*)\" contains soap fault$")
    public void thePlatformBufferesASetConfigurationResponseMessageForDeviceContainsSoapFault(
            final String deviceIdentification, final Map<String, String> expectedResponseData) throws Throwable {
        final SetConfigurationAsyncRequest request = new SetConfigurationAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        try {
            this.client.getSetConfiguration(request);
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
            GenericResponseSteps.verifySoapFault(expectedResponseData);
        }
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