/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.configurationmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.Configuration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.DaliConfiguration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.IndexAddressMap;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LightType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LinkType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayConfiguration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayMap;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreConfigurationManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.domain.core.exceptions.ArgumentNullOrEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the set configuration requests steps */
public class SetConfigurationSteps {
  private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationSteps.class);

  @Autowired private CoreConfigurationManagementClient client;

  private void addFilledDaliConfigurationToConfiguration(
      final Map<String, String> requestParameters, final Configuration config)
      throws ArgumentNullOrEmptyException {
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

  private void addFilledRelayConfigurationToConfiguration(
      final Map<String, String> requestParameters, final Configuration config)
      throws ArgumentNullOrEmptyException {
    final String rcMap = getString(requestParameters, PlatformKeys.RELAY_CONF);
    if (rcMap != null) {
      final RelayConfiguration relayConfiguration = new RelayConfiguration();
      final String[] relayMapArray = rcMap.split(";");
      for (final String relayMapElement : relayMapArray) {
        final RelayMap relayMap = new RelayMap();

        final String[] subRelayMapElement = relayMapElement.split(",");

        if (!StringUtils.isEmpty(subRelayMapElement[0])) {
          relayMap.setIndex(Integer.parseInt(subRelayMapElement[0]));
        }
        if (subRelayMapElement.length >= 2 && !StringUtils.isEmpty(subRelayMapElement[1])) {
          relayMap.setAddress(Integer.parseInt(subRelayMapElement[1]));
        }
        if (subRelayMapElement.length >= 3 && !StringUtils.isEmpty(subRelayMapElement[2])) {
          relayMap.setRelayType(RelayType.valueOf(subRelayMapElement[2]));
        }

        relayConfiguration.getRelayMap().add(relayMap);
      }

      config.setRelayConfiguration(relayConfiguration);
    }
  }

  /**
   * Sends a Set Configuration request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving a set configuration request$")
  public void receivingASetConfigurationRequest(final Map<String, String> requestParameters)
      throws Throwable {
    final SetConfigurationRequest request = new SetConfigurationRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    final Configuration config = new Configuration();

    if (StringUtils.isNotEmpty(requestParameters.get(PlatformCommonKeys.KEY_LIGHTTYPE))) {
      final LightType lightType =
          getEnum(requestParameters, PlatformKeys.KEY_LIGHTTYPE, LightType.class);
      config.setLightType(lightType);
    }

    if (StringUtils.isNotEmpty(getString(requestParameters, PlatformCommonKeys.DC_LIGHTS))
        || StringUtils.isNotEmpty(getString(requestParameters, PlatformCommonKeys.DC_MAP))) {
      this.addFilledDaliConfigurationToConfiguration(requestParameters, config);
    }

    if (StringUtils.isNotEmpty(getString(requestParameters, PlatformCommonKeys.RC_TYPE))
        || StringUtils.isNotEmpty(getString(requestParameters, PlatformCommonKeys.RELAY_CONF))) {
      this.addFilledRelayConfigurationToConfiguration(requestParameters, config);
    }

    final LinkType preferredLinkType =
        getEnum(requestParameters, PlatformKeys.KEY_PREFERRED_LINKTYPE, LinkType.class);
    config.setPreferredLinkType(preferredLinkType);

    if (requestParameters.containsKey(PlatformKeys.OSGP_IP_ADDRESS)
        && StringUtils.isNotEmpty(requestParameters.get(PlatformKeys.OSGP_IP_ADDRESS))) {
      config.setOsgpIpAddress(requestParameters.get(PlatformKeys.OSGP_IP_ADDRESS));
    }

    if (requestParameters.containsKey(PlatformKeys.OSGP_PORT)
        && StringUtils.isNotEmpty(requestParameters.get(PlatformKeys.OSGP_PORT))) {
      config.setOsgpPortNumber(getInteger(requestParameters, PlatformKeys.OSGP_PORT));
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
   * @param expectedResponseData The table with the expected fields in the response.
   * @apiNote The response will contain the correlation uid, so store that in the current scenario
   *     context for later use.
   * @throws Throwable
   */
  @Then("^the set configuration async response contains$")
  public void theSetConfigurationResponseContains(final Map<String, String> expectedResponseData)
      throws Throwable {
    final SetConfigurationAsyncResponse asyncResponse =
        (SetConfigurationAsyncResponse) ScenarioContext.current().get(PlatformKeys.RESPONSE);

    assertThat(asyncResponse.getAsyncResponse().getCorrelationUid()).isNotNull();
    assertThat(asyncResponse.getAsyncResponse().getDeviceId())
        .isEqualTo(
            getString(expectedResponseData, PlatformKeys.KEY_DEVICE_IDENTIFICATION),
            asyncResponse.getAsyncResponse().getDeviceId());

    // Save the returned CorrelationUid in the Scenario related context for
    // further use.
    saveCorrelationUidInScenarioContext(
        asyncResponse.getAsyncResponse().getCorrelationUid(),
        getString(
            expectedResponseData,
            PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    LOGGER.info(
        "Got CorrelationUid: ["
            + ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID)
            + "]");
  }

  @Then("^the platform buffers a set configuration response message for device \"([^\"]*)\"$")
  public void thePlatformBufferesASetConfigurationResponseMessageForDevice(
      final String deviceIdentification, final Map<String, String> expectedResponseData)
      throws Throwable {
    final SetConfigurationAsyncRequest request = new SetConfigurationAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    final SetConfigurationResponse response =
        Wait.untilAndReturn(
            () -> {
              final SetConfigurationResponse retval = this.client.getSetConfiguration(request);
              assertThat(retval).isNotNull();
              return retval;
            });

    assertThat(response.getResult())
        .isEqualTo(getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class));
  }

  @Then(
      "^the platform buffers a set configuration response message for device \"([^\"]*)\" contains soap fault$")
  public void thePlatformBufferesASetConfigurationResponseMessageForDeviceContainsSoapFault(
      final String deviceIdentification, final Map<String, String> expectedResponseData)
      throws Throwable {
    final SetConfigurationAsyncRequest request = new SetConfigurationAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
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
   * @param expectedResponseData The table with the expected fields in the response.
   * @throws Throwable
   */
  @Then("^the set configuration async response contains soap fault$")
  public void theSetConfigurationResponseContainsSoapFault(
      final Map<String, String> expectedResponseData) throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }
}
