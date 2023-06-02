//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.deviceinstallation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.DeviceStatus;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.EventNotificationType;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.LightType;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.LightValue;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.LinkType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceInstallationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class GetStatusSteps {

  @Autowired private CoreDeviceInstallationClient client;

  @When("receiving a device installation get status request")
  public void receivingADeviceInstallationGetStatusRequest(final Map<String, String> settings)
      throws Throwable {
    final GetStatusRequest request = new GetStatusRequest();

    request.setDeviceIdentification(
        getString(
            settings,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.getStatus(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @Then("the device installation get status async response contains")
  public void theDeviceInstallationGetStatusAsyncResponseContains(
      final Map<String, String> expectedResponseData) throws Throwable {
    final GetStatusAsyncResponse asyncResponse =
        (GetStatusAsyncResponse) ScenarioContext.current().get(PlatformKeys.RESPONSE);

    assertThat(asyncResponse.getAsyncResponse().getCorrelationUid()).isNotNull();
    assertThat(asyncResponse.getAsyncResponse().getDeviceId())
        .isEqualTo(getString(expectedResponseData, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

    saveCorrelationUidInScenarioContext(
        asyncResponse.getAsyncResponse().getCorrelationUid(),
        getString(
            expectedResponseData,
            PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
  }

  @Then(
      "the platform buffers a device installation get status response message for device {string}")
  public void thePlatformBuffersADeviceInstallationGetStatusResponseMessageForDevice(
      final String deviceIdentification, final Map<String, String> expectedResult)
      throws Throwable {
    final GetStatusAsyncRequest request = new GetStatusAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    final GetStatusResponse response =
        Wait.untilAndReturn(
            () -> {
              final GetStatusResponse retval = this.client.getStatusResponse(request);
              assertThat(retval).isNotNull();
              assertThat(retval.getResult())
                  .isEqualTo(
                      Enum.valueOf(
                          OsgpResultType.class, expectedResult.get(PlatformKeys.KEY_RESULT)));

              return retval;
            });

    final DeviceStatus deviceStatus = response.getDeviceStatus();

    assertThat(deviceStatus.getPreferredLinkType())
        .isEqualTo(getEnum(expectedResult, PlatformKeys.KEY_PREFERRED_LINKTYPE, LinkType.class));
    assertThat(deviceStatus.getActualLinkType())
        .isEqualTo(getEnum(expectedResult, PlatformKeys.KEY_ACTUAL_LINKTYPE, LinkType.class));
    assertThat(deviceStatus.getLightType())
        .isEqualTo(getEnum(expectedResult, PlatformKeys.KEY_LIGHTTYPE, LightType.class));

    if (expectedResult.containsKey(PlatformKeys.KEY_EVENTNOTIFICATIONTYPES)
        && StringUtils.isNotBlank(expectedResult.get(PlatformKeys.KEY_EVENTNOTIFICATIONTYPES))) {
      assertThat(deviceStatus.getEventNotifications().size())
          .isEqualTo(
              getString(
                      expectedResult,
                      PlatformKeys.KEY_EVENTNOTIFICATIONS,
                      PlatformDefaults.DEFAULT_EVENTNOTIFICATIONS)
                  .split(PlatformKeys.SEPARATOR_COMMA)
                  .length);

      for (final String eventNotification :
          getString(
                  expectedResult,
                  PlatformKeys.KEY_EVENTNOTIFICATIONS,
                  PlatformDefaults.DEFAULT_EVENTNOTIFICATIONS)
              .split(PlatformKeys.SEPARATOR_COMMA)) {
        final EventNotificationType eventNotificationType =
            Enum.valueOf(EventNotificationType.class, eventNotification);
        assertThat(deviceStatus.getEventNotifications().contains(eventNotificationType)).isTrue();
      }
    }

    if (expectedResult.containsKey(PlatformKeys.KEY_LIGHTVALUES)
        && StringUtils.isNotBlank(expectedResult.get(PlatformKeys.KEY_LIGHTVALUES))) {
      assertThat(deviceStatus.getLightValues().size())
          .isEqualTo(
              getString(
                      expectedResult,
                      PlatformKeys.KEY_LIGHTVALUES,
                      PlatformDefaults.DEFAULT_LIGHTVALUES)
                  .split(PlatformKeys.SEPARATOR_COMMA)
                  .length);

      for (final String lightValues :
          getString(
                  expectedResult,
                  PlatformKeys.KEY_LIGHTVALUES,
                  PlatformDefaults.DEFAULT_LIGHTVALUES)
              .split(PlatformKeys.SEPARATOR_COMMA)) {

        final String[] parts = lightValues.split(PlatformKeys.SEPARATOR_SEMICOLON);
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
}
