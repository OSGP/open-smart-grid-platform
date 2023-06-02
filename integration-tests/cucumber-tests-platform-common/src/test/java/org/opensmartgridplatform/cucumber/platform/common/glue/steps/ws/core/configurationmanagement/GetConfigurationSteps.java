//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.configurationmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.Configuration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.DaliConfiguration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.IndexAddressMap;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LightType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.LinkType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayConfiguration;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayMap;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.RelayType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreConfigurationManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the get configuration requests steps */
public class GetConfigurationSteps {

  @Autowired private CoreConfigurationManagementClient client;

  private static final Logger LOGGER = LoggerFactory.getLogger(GetConfigurationSteps.class);

  /**
   * Sends a Get Configuration request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving a get configuration request$")
  public void receivingAGetConfigurationRequest(final Map<String, String> requestParameters)
      throws Throwable {
    final GetConfigurationRequest request = new GetConfigurationRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
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
   * @param expectedResponseData The table with the expected fields in the response.
   * @apiNote The response will contain the correlation uid, so store that in the current scenario
   *     context for later use.
   * @throws Throwable
   */
  @Then("^the get configuration async response contains$")
  public void theGetConfigurationResponseContains(final Map<String, String> expectedResponseData)
      throws Throwable {
    final GetConfigurationAsyncResponse asyncResponse =
        (GetConfigurationAsyncResponse) ScenarioContext.current().get(PlatformKeys.RESPONSE);

    assertThat(asyncResponse.getAsyncResponse().getCorrelationUid()).isNotNull();
    assertThat(asyncResponse.getAsyncResponse().getDeviceId())
        .isEqualTo(getString(expectedResponseData, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

    // Save the returned CorrelationUid in the Scenario related context for
    // further use.
    saveCorrelationUidInScenarioContext(
        asyncResponse.getAsyncResponse().getCorrelationUid(),
        getString(
            expectedResponseData,
            PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    LOGGER.info(
        "Got CorrelationUid: ["
            + ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID)
            + "]");
  }

  /**
   * The check for the response from the Platform.
   *
   * @param expectedResponseData The table with the expected fields in the response.
   * @throws Throwable
   */
  @Then("^the get configuration async response contains soap fault$")
  public void theGetConfigurationResponseContainsSoapFault(
      final Map<String, String> expectedResponseData) throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }

  @Then("^the platform buffers a get configuration response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersAGetConfigurationResponseMessageForDevice(
      final String deviceIdentification, final Map<String, String> expectedResponseData)
      throws Throwable {
    final GetConfigurationAsyncRequest request = new GetConfigurationAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    final GetConfigurationResponse response =
        Wait.untilAndReturn(
            () -> {
              final GetConfigurationResponse retval = this.client.getGetConfiguration(request);
              assertThat(retval).isNotNull();
              assertThat(retval.getResult())
                  .isEqualTo(
                      getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class));

              return retval;
            });

    final Configuration configuration = response.getConfiguration();
    assertThat(configuration).isNotNull();

    if (expectedResponseData.containsKey(PlatformKeys.KEY_LIGHTTYPE)
        && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.KEY_LIGHTTYPE))
        && configuration.getLightType() != null) {
      assertThat(configuration.getLightType())
          .isEqualTo(getEnum(expectedResponseData, PlatformKeys.KEY_LIGHTTYPE, LightType.class));
    }

    final DaliConfiguration daliConfiguration = configuration.getDaliConfiguration();
    if (daliConfiguration != null) {

      if (expectedResponseData.containsKey(PlatformKeys.DC_LIGHTS)
          && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.DC_LIGHTS))
          && daliConfiguration.getNumberOfLights() != 0) {
        assertThat(daliConfiguration.getNumberOfLights())
            .isEqualTo((int) getInteger(expectedResponseData, PlatformKeys.DC_LIGHTS));
      }

      if (expectedResponseData.containsKey(PlatformKeys.DC_MAP)
          && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.DC_MAP))
          && daliConfiguration.getIndexAddressMap() != null) {
        final List<IndexAddressMap> indexAddressMapList = daliConfiguration.getIndexAddressMap();
        final String[] dcMapArray = getString(expectedResponseData, PlatformKeys.DC_MAP).split(";");
        for (int i = 0; i < dcMapArray.length; i++) {
          final String[] dcMapArrayElements = dcMapArray[i].split(",");
          assertThat(indexAddressMapList.get(i).getIndex())
              .isEqualTo(Integer.parseInt(dcMapArrayElements[0]));

          assertThat(indexAddressMapList.get(i).getAddress())
              .isEqualTo(Integer.parseInt(dcMapArrayElements[1]));
        }
      }
    }

    final RelayConfiguration relayConfiguration = configuration.getRelayConfiguration();
    if (relayConfiguration != null) {

      if (expectedResponseData.containsKey(PlatformKeys.RELAY_CONF)
          && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.RELAY_CONF))
          && relayConfiguration.getRelayMap() != null) {
        final List<RelayMap> relayMapList = relayConfiguration.getRelayMap();
        final String[] rcMapArray =
            getString(expectedResponseData, PlatformKeys.RELAY_CONF).split(";");
        for (int i = 0; i < rcMapArray.length; i++) {
          final String[] rcMapArrayElements = rcMapArray[i].split(",");
          if (rcMapArrayElements.length > 0 && relayMapList.size() > 0) {
            assertThat(relayMapList.get(i).getIndex())
                .isEqualTo(Integer.parseInt(rcMapArrayElements[0]));

            assertThat(relayMapList.get(i).getAddress())
                .isEqualTo(Integer.parseInt(rcMapArrayElements[1]));

            if (expectedResponseData.containsKey(PlatformKeys.KEY_RELAY_TYPE)
                && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.KEY_RELAY_TYPE))
                && relayMapList.get(i).getRelayType() != null) {
              assertThat(relayMapList.get(i).getRelayType())
                  .isEqualTo(
                      getEnum(expectedResponseData, PlatformKeys.KEY_RELAY_TYPE, RelayType.class));
            }
          }
        }
      }
    }

    //// Note: How to test this?
    // configuration.getRelayLinking();

    if (expectedResponseData.containsKey(PlatformKeys.KEY_PREFERRED_LINKTYPE)
        && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.KEY_PREFERRED_LINKTYPE))
        && configuration.getPreferredLinkType() != null) {
      assertThat(configuration.getPreferredLinkType())
          .isEqualTo(
              getEnum(expectedResponseData, PlatformKeys.KEY_PREFERRED_LINKTYPE, LinkType.class));
    }

    if (expectedResponseData.containsKey(PlatformKeys.OSGP_IP_ADDRESS)
        && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.OSGP_IP_ADDRESS))) {
      assertThat(configuration.getOsgpIpAddress())
          .isEqualTo(getString(expectedResponseData, PlatformKeys.OSGP_IP_ADDRESS));
    }

    if (expectedResponseData.containsKey(PlatformKeys.OSGP_PORT)
        && StringUtils.isNotBlank(expectedResponseData.get(PlatformKeys.OSGP_PORT))) {
      assertThat(configuration.getOsgpPortNumber())
          .isEqualTo(getInteger(expectedResponseData, PlatformKeys.OSGP_PORT));
    }
  }

  @Then(
      "^the platform buffers a get configuration response message for device \"([^\"]*)\" contains soap fault$")
  public void thePlatformBuffersAGetConfigurationResponseMessageForDeviceContainsSoapFault(
      final String deviceIdentification, final Map<String, String> expectedResponseData)
      throws Throwable {
    final GetConfigurationAsyncRequest request = new GetConfigurationAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    Wait.until(
        () -> {
          GetConfigurationResponse response = null;
          try {
            response = this.client.getGetConfiguration(request);
          } catch (final Exception e) {
            // do nothing
          }

          assertThat(response).isNotNull();
        });
  }
}
