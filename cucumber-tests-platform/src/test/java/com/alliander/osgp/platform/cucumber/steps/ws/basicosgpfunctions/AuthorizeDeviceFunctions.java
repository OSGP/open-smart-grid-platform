/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.basicosgpfunctions;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the firmware requests steps
 */
public class AuthorizeDeviceFunctions {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeDeviceFunctions.class);

    private static String deviceIdentification;

    @When("receiving a device function request")
    public void receivingADeviceFunctionRequest(final Map<String, String> requestParameters)
            throws OperationNotSupportedException, WebServiceSecurityException, GeneralSecurityException, IOException {
        final DeviceFunction deviceFunction = getEnum(requestParameters, Keys.DEVICE_FUNCTION, DeviceFunction.class);

        deviceIdentification = getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION,
                Defaults.DEFAULT_DEVICE_IDENTIFICATION);

        switch (deviceFunction) {
        case START_SELF_TEST:

            break;
        case STOP_SELF_TEST:

            break;
        case SET_LIGHT:

            break;
        case GET_DEVICE_AUTHORIZATION:

            break;
        case SET_EVENT_NOTIFICATIONS:

            break;
        case GET_EVENT_NOTIFICATIONS:

            break;
        case UPDATE_FIRMWARE:

            break;
        case GET_FIRMWARE_VERSION:

            break;
        case SET_LIGHT_SCHEDULE:

            break;
        case SET_TARIFF_SCHEDULE:

            break;
        case SET_CONFIGURATION:

            break;
        case GET_CONFIGURATION:

            break;
        case GET_STATUS:

            break;
        case REMOVE_DEVICE:

            break;
        case GET_ACTUAL_POWER_USAGE:

            break;
        case GET_POWER_USAGE_HISTORY:

            break;
        case RESUME_SCHEDULE:

            break;
        case SET_REBOOT:

            break;
        case SET_TRANSITION:

            break;
        default:
            throw new OperationNotSupportedException("DeviceFunction " + deviceFunction + " does not exist.");
        }
    }

    @Then("the device function response is successful")
    public void theDeviceFunctionResponseIsSuccessful(final Map<String, String> responseParameters) {
        final Object response = ScenarioContext.Current().get(Keys.RESPONSE);

        Assert.assertTrue(getBoolean(responseParameters, Keys.ALLOWED, Defaults.ALLOWED) && response != null
                && !(response instanceof SoapFaultClientException));
    }

    // /**
    // * Sends a Get Configuration request to the platform for a given device
    // * identification.
    // *
    // * @param requestParameters
    // * The table with the request parameters.
    // * @throws Throwable
    // */
    // @When("^receiving a get configuration request$")
    // public void receivingAGetConfigurationRequest(final Map<String, String>
    // requestParameters) throws Throwable {
    // final GetConfigurationRequest request = new GetConfigurationRequest();
    // request.setDeviceIdentification(
    // getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION,
    // Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    //
    // try {
    // ScenarioContext.Current().put(Keys.RESPONSE,
    // this.client.getConfiguration(request));
    // } catch (final SoapFaultClientException ex) {
    // ScenarioContext.Current().put(Keys.RESPONSE, ex);
    // }
    // }
    //
    // /**
    // * The check for the response from the Platform.
    // *
    // * @param expectedResponseData
    // * The table with the expected fields in the response.
    // * @note The response will contain the correlation uid, so store that in
    // the
    // * current scenario context for later use.
    // * @throws Throwable
    // */
    // @Then("^the get configuration async response contains$")
    // public void theGetConfigurationResponseContains(final Map<String, String>
    // expectedResponseData) throws Throwable {
    // final GetConfigurationAsyncResponse response =
    // (GetConfigurationAsyncResponse) ScenarioContext.Current()
    // .get(Keys.RESPONSE);
    //
    // Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
    // Assert.assertEquals(getString(expectedResponseData,
    // Keys.KEY_DEVICE_IDENTIFICATION),
    // response.getAsyncResponse().getDeviceId());
    //
    // // Save the returned CorrelationUid in the Scenario related context for
    // // further use.
    // saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
    // getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
    // Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    //
    // LOGGER.info("Got CorrelationUid: [" +
    // ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    // }
    //
    // /**
    // * The check for the response from the Platform.
    // *
    // * @param expectedResponseData
    // * The table with the expected fields in the response.
    // * @throws Throwable
    // */
    // @Then("^the get configuration async response contains soap fault$")
    // public void theGetConfigurationResponseContainsSoapFault(final
    // Map<String, String> expectedResponseData)
    // throws Throwable {
    // GenericResponseSteps.verifySoapFault(expectedResponseData);
    // }
    //
    // @Then("^the platform buffers a get configuration response message for
    // device \"([^\"]*)\"$")
    // public void
    // thePlatformBufferesAGetConfigurationResponseMessageForDevice(final String
    // deviceIdentification,
    // final Map<String, String> expectedResponseData) throws Throwable {
    // final GetConfigurationAsyncRequest request = new
    // GetConfigurationAsyncRequest();
    // final AsyncRequest asyncRequest = new AsyncRequest();
    // asyncRequest.setDeviceId(deviceIdentification);
    // asyncRequest.setCorrelationUid((String)
    // ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
    // request.setAsyncRequest(asyncRequest);
    //
    // GetConfigurationResponse response = null;
    //
    // boolean success = false;
    // int count = 0;
    // while (!success) {
    // if (count > this.configuration.getTimeout()) {
    // throw new TimeoutException();
    // }
    //
    // count++;
    // Thread.sleep(1000);
    //
    // response = this.client.getGetConfiguration(request);
    //
    // if (!expectedResponseData.containsKey(Keys.KEY_RESULT)
    // || getEnum(expectedResponseData, Keys.KEY_RESULT, OsgpResultType.class)
    // != response.getResult()) {
    // continue;
    // }
    //
    // success = true;
    // }
    //
    // final Configuration configuration = response.getConfiguration();
    //
    // if (configuration == null) {
    // Assert.assertNotNull(configuration);
    // }
    //
    // if (expectedResponseData.containsKey(Keys.KEY_LIGHTTYPE)
    // && !expectedResponseData.get(Keys.KEY_LIGHTTYPE).isEmpty() &&
    // configuration.getLightType() != null) {
    // Assert.assertEquals(getEnum(expectedResponseData, Keys.KEY_LIGHTTYPE,
    // LightType.class),
    // configuration.getLightType());
    // }
    //
    // final DaliConfiguration daliConfiguration =
    // configuration.getDaliConfiguration();
    // if (daliConfiguration != null) {
    //
    // if (expectedResponseData.containsKey(Keys.DC_LIGHTS) &&
    // !expectedResponseData.get(Keys.DC_LIGHTS).isEmpty()
    // && daliConfiguration.getNumberOfLights() != 0) {
    // Assert.assertEquals((int) getInteger(expectedResponseData,
    // Keys.DC_LIGHTS),
    // daliConfiguration.getNumberOfLights());
    // }
    //
    // if (expectedResponseData.containsKey(Keys.DC_MAP) &&
    // !expectedResponseData.get(Keys.DC_MAP).isEmpty()
    // && daliConfiguration.getIndexAddressMap() != null) {
    // final List<IndexAddressMap> indexAddressMapList =
    // daliConfiguration.getIndexAddressMap();
    // final String[] dcMapArray = getString(expectedResponseData,
    // Keys.DC_MAP).split(";");
    // for (int i = 0; i < dcMapArray.length; i++) {
    // final String[] dcMapArrayElements = dcMapArray[i].split(",");
    // Assert.assertEquals(Integer.parseInt(dcMapArrayElements[0]),
    // indexAddressMapList.get(i).getIndex());
    // Assert.assertEquals(Integer.parseInt(dcMapArrayElements[1]),
    // indexAddressMapList.get(i).getAddress());
    // }
    // }
    // }
    //
    // final RelayConfiguration relayConfiguration =
    // configuration.getRelayConfiguration();
    // if (relayConfiguration != null) {
    //
    // if (expectedResponseData.containsKey(Keys.RC_MAP) &&
    // !expectedResponseData.get(Keys.RC_MAP).isEmpty()
    // && relayConfiguration.getRelayMap() != null) {
    // final List<RelayMap> relayMapList = relayConfiguration.getRelayMap();
    // final String[] rcMapArray = getString(expectedResponseData,
    // Keys.RC_MAP).split(";");
    // for (int i = 0; i < rcMapArray.length; i++) {
    // final String[] rcMapArrayElements = rcMapArray[i].split(",");
    // if (rcMapArrayElements.length > 0 && relayMapList.size() > 0) {
    // Assert.assertEquals(Integer.parseInt(rcMapArrayElements[0]),
    // relayMapList.get(i).getIndex());
    // Assert.assertEquals(Integer.parseInt(rcMapArrayElements[1]),
    // relayMapList.get(i).getAddress());
    //
    // if (expectedResponseData.containsKey(Keys.KEY_RELAY_TYPE)
    // && !expectedResponseData.get(Keys.KEY_RELAY_TYPE).isEmpty()
    // && relayMapList.get(i).getRelayType() != null) {
    // Assert.assertEquals(getEnum(expectedResponseData, Keys.KEY_RELAY_TYPE,
    // RelayType.class),
    // relayMapList.get(i).getRelayType());
    // }
    // }
    // }
    // }
    // }
    //
    // //// Note: This information isn't added in the fitnesse test, how to
    // //// test this?
    // // configuration.getRelayLinking();
    //
    // if (expectedResponseData.containsKey(Keys.KEY_PREFERRED_LINKTYPE)
    // && !expectedResponseData.get(Keys.KEY_PREFERRED_LINKTYPE).isEmpty()
    // && configuration.getPreferredLinkType() != null) {
    // Assert.assertEquals(getEnum(expectedResponseData,
    // Keys.KEY_PREFERRED_LINKTYPE, LinkType.class),
    // configuration.getPreferredLinkType());
    // }
    //
    // // Note: This piece of code has been made because there are multiple
    // // enumerations with the name MeterType, but not all of them has all
    // // values the same. Some with underscore and some without.s
    //
    // if (expectedResponseData.containsKey(Keys.METER_TYPE) &&
    // !expectedResponseData.get(Keys.METER_TYPE).isEmpty()
    // && configuration.getMeterType() != null) {
    // MeterType meterType = null;
    // final String sMeterType = getString(expectedResponseData,
    // Keys.METER_TYPE);
    // if (!sMeterType.toString().contains("_")
    // && sMeterType.equals(MeterType.P_1.toString().replaceAll("_", ""))) {
    // final String[] sMeterTypeArray = sMeterType.toString().split("");
    // meterType = MeterType.valueOf(sMeterTypeArray[0] + "_" +
    // sMeterTypeArray[1]);
    // } else {
    // meterType = getEnum(expectedResponseData, Keys.METER_TYPE,
    // MeterType.class);
    // }
    // Assert.assertEquals(meterType, configuration.getMeterType());
    // }
    //
    // if (expectedResponseData.containsKey(Keys.SHORT_INTERVAL)
    // && !expectedResponseData.get(Keys.SHORT_INTERVAL).isEmpty()
    // && configuration.getShortTermHistoryIntervalMinutes() != null) {
    // Assert.assertEquals(getInteger(expectedResponseData, Keys.SHORT_INTERVAL,
    // Defaults.SHORT_INTERVAL),
    // configuration.getShortTermHistoryIntervalMinutes());
    // }
    //
    // if (expectedResponseData.containsKey(Keys.LONG_INTERVAL)
    // && !expectedResponseData.get(Keys.LONG_INTERVAL).isEmpty()
    // && configuration.getLongTermHistoryInterval() != null) {
    // Assert.assertEquals(getInteger(expectedResponseData, Keys.LONG_INTERVAL,
    // Defaults.LONG_INTERVAL),
    // configuration.getLongTermHistoryInterval());
    // }
    //
    // if (expectedResponseData.containsKey(Keys.INTERVAL_TYPE)
    // && !expectedResponseData.get(Keys.INTERVAL_TYPE).isEmpty()
    // && configuration.getLongTermHistoryIntervalType() != null) {
    // Assert.assertEquals(getEnum(expectedResponseData, Keys.INTERVAL_TYPE,
    // LongTermIntervalType.class),
    // configuration.getLongTermHistoryIntervalType());
    // }
    // }
    //
    // @Then("^the platform buffers a get configuration response message for
    // device \"([^\"]*)\" contains soap fault$")
    // public void
    // thePlatformBufferesAGetConfigurationResponseMessageForDeviceContainsSoapFault(
    // final String deviceIdentification, final Map<String, String>
    // expectedResponseData) throws Throwable {
    // try {
    // this.thePlatformBufferesAGetConfigurationResponseMessageForDevice(deviceIdentification,
    // expectedResponseData);
    // } catch (final SoapFaultClientException ex) {
    // Assert.assertEquals(getString(expectedResponseData, Keys.KEY_MESSAGE),
    // ex.getMessage());
    // }
    // }
}