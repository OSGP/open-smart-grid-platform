//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.adhocmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import java.util.Objects;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.DeviceStatus;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.EventNotificationType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.GetStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LinkType;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingAdHocManagementClient;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.tariffswitching.TariffSwitchingAdHocManagementClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the set light requests steps */
public class GetStatusSteps {

  @Autowired private PublicLightingAdHocManagementClient publicLightingClient;

  @Autowired private TariffSwitchingAdHocManagementClient tariffSwitchingClient;

  private static final Logger LOGGER = LoggerFactory.getLogger(GetStatusSteps.class);

  /**
   * Sends a Get Status request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws WebServiceSecurityException
   */
  @When("^receiving a get status request$")
  public void receivingAGetStatusRequest(final Map<String, String> requestParameters)
      throws WebServiceSecurityException {

    final GetStatusRequest request = new GetStatusRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    try {
      ScenarioContext.current()
          .put(PlatformPubliclightingKeys.RESPONSE, this.publicLightingClient.getStatus(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
    }
  }

  @When("^receiving a get tariff status request$")
  public void receivingAGetTariffStatusRequest(final Map<String, String> requestParameters)
      throws WebServiceSecurityException {

    final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
            .GetStatusRequest
        request =
            new org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
                .GetStatusRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    try {
      ScenarioContext.current()
          .put(PlatformPubliclightingKeys.RESPONSE, this.tariffSwitchingClient.getStatus(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
    }
  }

  @When("^receiving a get status request by an unknown organization$")
  public void receivingAGetStatusRequestByAnUnknownOrganization(
      final Map<String, String> requestParameters) throws WebServiceSecurityException {
    // Force the request being send to the platform as a given organization.
    ScenarioContext.current()
        .put(PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

    this.receivingAGetStatusRequest(requestParameters);
  }

  @When("^receiving a get tariff status request by an unknown organization$")
  public void receivingAGetTariffStatusRequestByAnUnknownOrganization(
      final Map<String, String> requestParameters) throws WebServiceSecurityException {
    // Force the request being sent to the platform as a given organization.
    ScenarioContext.current()
        .put(PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

    this.receivingAGetTariffStatusRequest(requestParameters);
  }

  /**
   * The check for the response from the Platform.
   *
   * @param expectedResponseData The table with the expected fields in the response.
   * @apiNote The response will contain the correlation uid, so store that in the current scenario
   *     context for later use.
   */
  @Then("^the get status async response contains$")
  public void theGetStatusAsyncResponseContains(final Map<String, String> expectedResponseData) {

    final GetStatusAsyncResponse asyncResponse =
        (GetStatusAsyncResponse) ScenarioContext.current().get(PlatformPubliclightingKeys.RESPONSE);

    assertThat(asyncResponse.getAsyncResponse().getCorrelationUid()).isNotNull();
    assertThat(asyncResponse.getAsyncResponse().getDeviceId())
        .isEqualTo(
            getString(expectedResponseData, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION));

    // Save the returned CorrelationUid in the Scenario related context for
    // further use.
    saveCorrelationUidInScenarioContext(
        asyncResponse.getAsyncResponse().getCorrelationUid(),
        getString(
            expectedResponseData,
            PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    LOGGER.info(
        "Got CorrelationUid: ["
            + ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID)
            + "]");
  }

  @Then("^the get tariff status async response contains$")
  public void theGetTariffStatusAsyncResponseContains(
      final Map<String, String> expectedResponseData) {

    final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
            .GetStatusAsyncResponse
        asyncResponse =
            (org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
                    .GetStatusAsyncResponse)
                ScenarioContext.current().get(PlatformPubliclightingKeys.RESPONSE);

    assertThat(asyncResponse.getAsyncResponse().getCorrelationUid()).isNotNull();
    assertThat(asyncResponse.getAsyncResponse().getDeviceId())
        .isEqualTo(
            getString(expectedResponseData, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION));

    // Save the returned CorrelationUid in the Scenario related context for
    // further use.
    saveCorrelationUidInScenarioContext(
        asyncResponse.getAsyncResponse().getCorrelationUid(),
        getString(
            expectedResponseData,
            PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformPubliclightingDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

    LOGGER.info(
        "Got CorrelationUid: ["
            + ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID)
            + "]");
  }

  @Then("^the get status response contains soap fault$")
  public void theGetStatusResponseContainsSoapFault(
      final Map<String, String> expectedResponseData) {
    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }

  @Then("^the platform buffers a get status response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersAGetStatusResponseMessageForDevice(
      final String deviceIdentification, final Map<String, String> expectedResult) {
    final GetStatusAsyncRequest request = this.getGetStatusAsyncRequest(deviceIdentification);
    final GetStatusResponse response =
        Wait.untilAndReturn(
            () -> {
              final GetStatusResponse retval =
                  this.publicLightingClient.getGetStatusResponse(request);
              assertThat(retval).isNotNull();
              assertThat(retval.getResult())
                  .isEqualTo(
                      Enum.valueOf(
                          OsgpResultType.class,
                          expectedResult.get(PlatformPubliclightingKeys.KEY_RESULT)));
              return retval;
            });

    final DeviceStatus deviceStatus = (DeviceStatus) response.getStatus();

    assertThat(deviceStatus.getPreferredLinkType())
        .isEqualTo(
            getEnum(
                expectedResult, PlatformPubliclightingKeys.KEY_PREFERRED_LINKTYPE, LinkType.class));
    assertThat(deviceStatus.getActualLinkType())
        .isEqualTo(
            getEnum(
                expectedResult, PlatformPubliclightingKeys.KEY_ACTUAL_LINKTYPE, LinkType.class));
    assertThat(deviceStatus.getLightType())
        .isEqualTo(
            getEnum(expectedResult, PlatformPubliclightingKeys.KEY_LIGHTTYPE, LightType.class));

    if (expectedResult.containsKey(PlatformPubliclightingKeys.KEY_EVENTNOTIFICATIONTYPES)
        && !expectedResult.get(PlatformPubliclightingKeys.KEY_EVENTNOTIFICATIONTYPES).isEmpty()) {
      assertThat(deviceStatus.getEventNotifications().size())
          .isEqualTo(
              getString(
                      expectedResult,
                      PlatformPubliclightingKeys.KEY_EVENTNOTIFICATIONS,
                      PlatformPubliclightingDefaults.DEFAULT_EVENTNOTIFICATIONS)
                  .split(PlatformPubliclightingKeys.SEPARATOR_COMMA)
                  .length);

      for (final String eventNotification :
          getString(
                  expectedResult,
                  PlatformPubliclightingKeys.KEY_EVENTNOTIFICATIONS,
                  PlatformPubliclightingDefaults.DEFAULT_EVENTNOTIFICATIONS)
              .split(PlatformPubliclightingKeys.SEPARATOR_COMMA)) {
        assertThat(
                deviceStatus
                    .getEventNotifications()
                    .contains(Enum.valueOf(EventNotificationType.class, eventNotification)))
            .isTrue();
      }
    }

    if (expectedResult.containsKey(PlatformPubliclightingKeys.KEY_LIGHTVALUES)
        && !expectedResult.get(PlatformPubliclightingKeys.KEY_LIGHTVALUES).isEmpty()) {
      assertThat(deviceStatus.getLightValues().size())
          .isEqualTo(
              getString(
                      expectedResult,
                      PlatformPubliclightingKeys.KEY_LIGHTVALUES,
                      PlatformPubliclightingDefaults.DEFAULT_LIGHTVALUES)
                  .split(PlatformPubliclightingKeys.SEPARATOR_COMMA)
                  .length);

      for (final String lightValues :
          getString(
                  expectedResult,
                  PlatformPubliclightingKeys.KEY_LIGHTVALUES,
                  PlatformPubliclightingDefaults.DEFAULT_LIGHTVALUES)
              .split(PlatformPubliclightingKeys.SEPARATOR_COMMA)) {

        final String[] parts = lightValues.split(PlatformPubliclightingKeys.SEPARATOR_SEMICOLON);
        final Integer index = Integer.parseInt(parts[0]);
        final Boolean on = Boolean.parseBoolean(parts[1]);
        final Integer dimValue = Integer.parseInt(parts[2]);

        boolean found = false;
        for (final LightValue lightValue : deviceStatus.getLightValues()) {

          if (Objects.equals(lightValue.getIndex(), index)
              && lightValue.isOn() == on
              && Objects.equals(lightValue.getDimValue(), dimValue)) {
            found = true;
            break;
          }
        }

        assertThat(found).isTrue();
      }
    }
  }

  @Then("^the platform buffers a get tariff status response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersAGetTariffStatusResponseMessageForDevice(
      final String deviceIdentification, final Map<String, String> expectedResult) {
    final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
            .GetStatusAsyncRequest
        request = this.getGetTariffStatusAsyncRequest(deviceIdentification);
    final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
            .GetStatusResponse
        response =
            Wait.untilAndReturn(
                () -> {
                  final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
                          .GetStatusResponse
                      retval = this.tariffSwitchingClient.getGetStatusResponse(request);
                  assertThat(retval);
                  assertThat(retval.getResult())
                      .isEqualTo(
                          Enum.valueOf(
                              org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common
                                  .OsgpResultType.class,
                              expectedResult.get(PlatformPubliclightingKeys.KEY_RESULT)));

                  return retval;
                });

    final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement.DeviceStatus
        deviceStatus = response.getDeviceStatus();
    assertThat(deviceStatus).isNotNull();
    assertThat(deviceStatus.getTariffValues()).isNotNull();
  }

  @Then(
      "^the platform buffers a get status response message for device \"([^\"]*)\" which contains soap fault$")
  public void thePlatformBuffersAGetStatusResponseMessageForDeviceWhichContainsSoapFault(
      final String deviceIdentification, final Map<String, String> expectedResult)
      throws WebServiceSecurityException {
    try {
      this.publicLightingClient.getGetStatusResponse(
          this.getGetStatusAsyncRequest(deviceIdentification));
    } catch (final SoapFaultClientException sfce) {
      ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, sfce);
    }

    GenericResponseSteps.verifySoapFault(expectedResult);
  }

  private GetStatusAsyncRequest getGetStatusAsyncRequest(final String deviceIdentification) {
    final GetStatusAsyncRequest request = new GetStatusAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    return request;
  }

  private org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
          .GetStatusAsyncRequest
      getGetTariffStatusAsyncRequest(final String deviceIdentification) {
    final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
            .GetStatusAsyncRequest
        request =
            new org.opensmartgridplatform.adapter.ws.schema.tariffswitching.adhocmanagement
                .GetStatusAsyncRequest();
    final org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common.AsyncRequest
        asyncRequest =
            new org.opensmartgridplatform.adapter.ws.schema.tariffswitching.common.AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    return request;
  }
}
