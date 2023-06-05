// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ProtocolInfo;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class ProtocolInfoSteps {

  @Autowired private AdminDeviceManagementClient client;

  @Autowired private DeviceRepository deviceRepository;

  @When("receiving a get protocol info request")
  public void whenReceivingGetProtocolInfoRequest() throws WebServiceSecurityException {
    final GetProtocolInfosRequest request = new GetProtocolInfosRequest();

    final GetProtocolInfosResponse response = this.client.getProtocolInfos(request);

    ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, response);
  }

  @Then("the get protocol info response should be returned")
  public void thenTheProtocolInfoResponseShouldBeReturned(final Map<String, String> input) {
    final GetProtocolInfosResponse response =
        (GetProtocolInfosResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    final ProtocolInfo expected = getProtocolInfo(input);

    assertThat(response.getProtocolInfos()).isNotEmpty();
    assertThat(response.getProtocolInfos())
        .usingElementComparatorIgnoringFields("id")
        .contains(expected);
  }

  @When("receiving a update device protocol request")
  public void whenReceivingUpdateDeviceProtocolRequest(final Map<String, String> input)
      throws WebServiceSecurityException {
    final UpdateDeviceProtocolRequest request = new UpdateDeviceProtocolRequest();
    request.setDeviceIdentification(getString(input, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    request.setProtocolInfo(getProtocolInfo(input));

    try {
      final UpdateDeviceProtocolResponse response = this.client.updateDeviceProtocol(request);

      ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, response);
    } catch (final SoapFaultClientException e) {
      ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, e);
    }
  }

  @Then("the update device protocol response should be returned")
  public void thenTheUpdateDeviceProtocolResponseShouldBeReturned() {
    final UpdateDeviceProtocolResponse response =
        (UpdateDeviceProtocolResponse) ScenarioContext.current().get(PlatformCommonKeys.RESPONSE);

    assertThat(response).isNotNull();
  }

  @Then("the device is configured with the protocol")
  public void thenTheDeviceIsConfiguredWithTheProtocol(final Map<String, String> input) {
    final String deviceIdentification = getString(input, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

    final String protocol = device.getProtocolInfo().getProtocol();
    final String version = device.getProtocolInfo().getProtocolVersion();
    final String variant = device.getProtocolInfo().getProtocolVariant();

    final ProtocolInfo expected = getProtocolInfo(input);

    assertThat(protocol).isEqualTo(expected.getProtocol());
    assertThat(version).isEqualTo(expected.getProtocolVersion());
    assertThat(variant).isEqualTo(expected.getProtocolVariant());
  }

  @Then("the update device protocol response contains an error")
  public void thenTheUpdateDeviceProtocolResponseContainsAnError(
      final Map<String, String> expected) {
    GenericResponseSteps.verifySoapFault(expected);
  }

  private static ProtocolInfo getProtocolInfo(final Map<String, String> input) {
    final ProtocolInfo protocolInfo = new ProtocolInfo();
    protocolInfo.setProtocol(getString(input, PlatformKeys.KEY_PROTOCOL));
    protocolInfo.setProtocolVariant(getString(input, PlatformKeys.KEY_PROTOCOL_VARIANT));
    protocolInfo.setProtocolVersion(getString(input, PlatformKeys.KEY_PROTOCOL_VERSION));

    return protocolInfo;
  }
}
