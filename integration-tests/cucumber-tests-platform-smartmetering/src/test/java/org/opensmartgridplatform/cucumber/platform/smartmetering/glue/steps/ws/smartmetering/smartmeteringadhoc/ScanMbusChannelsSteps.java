// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.MbusChannelShortEquipmentIdentifier;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.MbusShortEquipmentIdentifier;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.ScanMbusChannelsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ScanMbusChannelsSteps {

  @Autowired
  private SmartMeteringAdHocRequestClient<ScanMbusChannelsAsyncResponse, ScanMbusChannelsRequest>
      requestClient;

  @Autowired
  private SmartMeteringAdHocResponseClient<ScanMbusChannelsResponse, ScanMbusChannelsAsyncRequest>
      responseClient;

  @When("^the scan M-Bus channels request is received$")
  public void theScanMBusChannelsRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final ScanMbusChannelsRequest request =
        ScanMbusChannelsRequestFactory.fromParameterMap(settings);
    final ScanMbusChannelsAsyncResponse asyncResponse = this.requestClient.doRequest(request);

    assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the found M-bus devices are in the response$")
  public void theFoundMBusDevicesAreInTheResponse(final Map<String, String> settings)
      throws Throwable {
    final ScanMbusChannelsAsyncRequest asyncRequest =
        ScanMbusChannelsRequestFactory.fromScenarioContext();
    final ScanMbusChannelsResponse response = this.responseClient.getResponse(asyncRequest);
    assertThat(response.getResult().name())
        .as("Result is not as expected.")
        .isEqualTo(settings.get(PlatformSmartmeteringKeys.RESULT));

    this.assertChannelShortIds(settings, response.getChannelShortIds());
  }

  public void assertChannelShortIds(
      final Map<String, String> expectedValues,
      final List<MbusChannelShortEquipmentIdentifier> channelShortIds) {

    for (int channel = 1; channel <= 4; channel++) {
      this.assertShortIdForChannel(expectedValues, channel, channelShortIds);
    }
  }

  private void assertShortIdForChannel(
      final Map<String, String> expectedValues,
      final int channel,
      final List<MbusChannelShortEquipmentIdentifier> channelShortIds) {

    final String channelPrefix = "Channel" + channel;
    final String expectedIdentificationNumber =
        expectedValues.get(channelPrefix + PlatformSmartmeteringKeys.MBUS_IDENTIFICATION_NUMBER);
    if (expectedIdentificationNumber == null) {
      /*
       * If no identification number is specified for the channel, do not
       * verify values from the response for it.
       */
      return;
    }
    final String expectedManufacturerIdentification =
        SettingsHelper.getNonBlankStringValue(
            expectedValues,
            channelPrefix + PlatformSmartmeteringKeys.MBUS_MANUFACTURER_IDENTIFICATION);
    final Short expectedVersion =
        SettingsHelper.getShortValue(
            expectedValues, channelPrefix + PlatformSmartmeteringKeys.MBUS_VERSION);
    final Short expectedDeviceTypeIdentification =
        SettingsHelper.getShortValue(
            expectedValues,
            channelPrefix + PlatformSmartmeteringKeys.MBUS_DEVICE_TYPE_IDENTIFICATION);

    final MbusShortEquipmentIdentifier shortId =
        this.findShortIdForChannel(channel, channelShortIds);
    assertThat(shortId).as("An M-Bus Short ID is expected for channel " + channel).isNotNull();

    assertThat(shortId.getIdentificationNumber())
        .as("M-Bus identification number for channel " + channel)
        .isEqualTo(expectedIdentificationNumber);
    assertThat(shortId.getManufacturerIdentification())
        .as("M-Bus manufacturer identification for channel " + channel)
        .isEqualTo(expectedManufacturerIdentification);
    assertThat(shortId.getVersionIdentification())
        .as("M-Bus version for channel " + channel)
        .isEqualTo(expectedVersion);
    assertThat(shortId.getDeviceTypeIdentification())
        .as("M-Bus device type identification for channel " + channel)
        .isEqualTo(expectedDeviceTypeIdentification);
  }

  private MbusShortEquipmentIdentifier findShortIdForChannel(
      final int channel, final List<MbusChannelShortEquipmentIdentifier> channelShortIds) {

    final Predicate<MbusChannelShortEquipmentIdentifier> channelMatches =
        channelShortId -> channel == channelShortId.getChannel();
    return channelShortIds.stream()
        .filter(channelMatches)
        .map(MbusChannelShortEquipmentIdentifier::getShortId)
        .findFirst()
        .orElse(null);
  }
}
