// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SetDeviceCommunicationSettingsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management.SmartMeteringManagementResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class SetDeviceCommunicationSettingsSteps {

  @Autowired private DlmsDeviceRepository dlmsDeviceRepository;

  @Autowired
  private SmartMeteringManagementRequestClient<
          SetDeviceCommunicationSettingsAsyncResponse, SetDeviceCommunicationSettingsRequest>
      smManagementRequestClientSetDeviceCommunicationSettings;

  @Autowired
  private SmartMeteringManagementResponseClient<
          SetDeviceCommunicationSettingsResponse, SetDeviceCommunicationSettingsAsyncRequest>
      smManagementResponseClientSetDeviceCommunicationSettings;

  @When("^the set device communication settings request is received$")
  public void theSetDeviceCommunicationSettingsRequestIsReceived(
      final Map<String, String> requestData) throws Throwable {

    final SetDeviceCommunicationSettingsRequest setDeviceCommunicationSettingsRequest =
        SetDeviceCommunicationSettingsRequestFactory.fromParameterMap(requestData);

    final SetDeviceCommunicationSettingsAsyncResponse setDeviceCommunicationSettingsAsyncResponse =
        this.smManagementRequestClientSetDeviceCommunicationSettings.doRequest(
            setDeviceCommunicationSettingsRequest);

    assertThat(setDeviceCommunicationSettingsAsyncResponse)
        .as("setDeviceCommunicationSettingsAsyncResponse should not be null")
        .isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            setDeviceCommunicationSettingsAsyncResponse.getCorrelationUid());
  }

  @Then("^the set device communication settings response should be \"([^\"]*)\"$")
  public void theSetDeviceCommunicationSettingsResponseShouldBe(final String result)
      throws Throwable {
    final SetDeviceCommunicationSettingsAsyncRequest setDeviceCommunicationSettingsAsyncRequest =
        SetDeviceCommunicationSettingsRequestFactory.fromScenarioContext();

    final SetDeviceCommunicationSettingsResponse setDeviceCommunicationSettingsResponse =
        this.smManagementResponseClientSetDeviceCommunicationSettings.getResponse(
            setDeviceCommunicationSettingsAsyncRequest);

    assertThat(setDeviceCommunicationSettingsResponse)
        .as("SetDeviceCommunicationSettingsResponse should not be null")
        .isNotNull();
    assertThat(setDeviceCommunicationSettingsResponse.getResult())
        .as("Expected OsgpResultType should not be null")
        .isNotNull();
  }

  @Then("^the device \"([^\"]*)\" should be in the database with attributes$")
  public void theDeviceShouldBeInTheDatabaseWithAttributes(
      final String deviceIdentification, final Map<String, String> settings) throws Throwable {
    final DlmsDevice device =
        this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);

    final int expectedResultChallengeLength =
        getInteger(
            settings,
            PlatformSmartmeteringKeys.CHALLENGE_LENGTH,
            PlatformSmartmeteringDefaults.CHALLENGE_LENGTH);

    final int expectedResultGetWithListMax =
        getInteger(
            settings,
            PlatformSmartmeteringKeys.WITH_LIST_MAX,
            PlatformSmartmeteringDefaults.WITH_LIST_MAX);

    assertThat(device.getChallengeLength().intValue())
        .as("Number of challenge length should match")
        .isEqualTo(expectedResultChallengeLength);
    assertThat(device.getWithListMax().intValue())
        .as("With list max should match")
        .isEqualTo(expectedResultGetWithListMax);
    assertThat(device.isSelectiveAccessSupported())
        .as("Selective access supported should match")
        .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.SELECTIVE_ACCESS_SUPPORTED));
    assertThat(device.isIpAddressIsStatic())
        .as("IP address is static should match")
        .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.IP_ADDRESS_IS_STATIC));
    assertThat(device.isUseSn())
        .as("Use SN should match")
        .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.USE_SN));
    assertThat(device.isUseHdlc())
        .as("Use HDLC should match")
        .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.USE_HDLC));
    assertThat(device.isPolyphase())
        .as("Polyphase should match")
        .isEqualTo(getBoolean(settings, PlatformSmartmeteringKeys.POLYPHASE));
  }
}
