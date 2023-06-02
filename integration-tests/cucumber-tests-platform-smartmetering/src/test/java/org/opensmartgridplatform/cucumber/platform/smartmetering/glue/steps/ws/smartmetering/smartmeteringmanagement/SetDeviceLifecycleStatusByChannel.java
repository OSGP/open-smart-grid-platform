//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SetDeviceLifecycleStatusByChannelRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class SetDeviceLifecycleStatusByChannel {

  @Autowired
  private SmartMeteringManagementRequestClient<
          SetDeviceLifecycleStatusByChannelAsyncResponse, SetDeviceLifecycleStatusByChannelRequest>
      smManagementRequestClient;

  @Autowired
  private SmartMeteringManagementResponseClient<
          SetDeviceLifecycleStatusByChannelResponse, SetDeviceLifecycleStatusByChannelAsyncRequest>
      smManagementResponseClient;

  @Autowired private SmartMeterRepository smartMeterRepository;

  private static final String OPERATION = "Set device lifecycle status by channel";

  @When("^a set device lifecycle status by channel request is received$")
  public void aSetDeviceLifecycleStatusByChannelRequestIsReceived(
      final Map<String, String> settings) throws Throwable {

    final SetDeviceLifecycleStatusByChannelRequest request =
        SetDeviceLifecycleStatusByChannelRequestFactory.fromParameterMap(settings);

    final SetDeviceLifecycleStatusByChannelAsyncResponse asyncResponse =
        this.smManagementRequestClient.doRequest(request);

    assertThat(asyncResponse)
        .as("setDeviceCommunicationSettingsAsyncResponse should not be null")
        .isNotNull();
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the set device lifecycle status by channel response is returned$")
  public void theSetDeviceLifecycleStatusByChannelResponseIsReturned(
      final Map<String, String> settings) throws Throwable {

    final SetDeviceLifecycleStatusByChannelAsyncRequest asyncRequest =
        SetDeviceLifecycleStatusByChannelRequestFactory.fromScenarioContext();
    final SetDeviceLifecycleStatusByChannelResponse response =
        this.smManagementResponseClient.getResponse(asyncRequest);

    assertThat(response.getResult())
        .as(OPERATION + ", Checking result:")
        .isEqualTo(OsgpResultType.OK);
    assertThat(
            response
                .getSetDeviceLifecycleStatusByChannelResponseData()
                .getGatewayDeviceIdentification())
        .as(OPERATION + ", Checking gatewayDeviceId:")
        .isEqualTo(settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    assertThat(response.getSetDeviceLifecycleStatusByChannelResponseData().getChannel())
        .as(OPERATION + ", Checking channel:")
        .isEqualTo(Short.parseShort(settings.get(PlatformSmartmeteringKeys.CHANNEL)));

    final SmartMeter gatewayDevice =
        this.smartMeterRepository.findByDeviceIdentification(
            settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    final List<SmartMeter> mbusDevices =
        this.smartMeterRepository.getMbusDevicesForGateway(gatewayDevice.getId());
    SmartMeter mbusDevice = null;
    for (final SmartMeter device : mbusDevices) {
      if (device
          .getChannel()
          .equals(Short.parseShort(settings.get(PlatformSmartmeteringKeys.CHANNEL)))) {
        mbusDevice = device;
        break;
      }
    }

    assertThat(mbusDevice.getDeviceIdentification())
        .as(OPERATION + ", Checking mbusDeviceIdentification:")
        .isEqualTo(
            response
                .getSetDeviceLifecycleStatusByChannelResponseData()
                .getMbusDeviceIdentification());
    assertThat(mbusDevice.getDeviceLifecycleStatus())
        .as(OPERATION + ", Checking deviceLifecycleStatus of device:")
        .isEqualTo(
            DeviceLifecycleStatus.valueOf(
                settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_LIFECYCLE_STATUS)));
  }

  @Then("^set device lifecycle status by channel request should return an exception$")
  public void setDeviceLifecycleStatusByChannelRequestShouldReturnAnException() throws Throwable {

    final SetDeviceLifecycleStatusByChannelAsyncRequest asyncRequest =
        SetDeviceLifecycleStatusByChannelRequestFactory.fromScenarioContext();
    try {
      this.smManagementResponseClient.getResponse(asyncRequest);
      Assertions.fail("A SoapFaultClientException should be thrown.");
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
    }
  }
}
