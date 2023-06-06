// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class UpdateDeviceCdmaSettingsSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDeviceCdmaSettingsSteps.class);

  @Autowired private CoreDeviceManagementClient client;

  @Autowired private DeviceRepository deviceRepository;

  @When("^an update device CDMA settings request is received$")
  public void receiveAnUpdateDeviceCdmaSettingsRequest(final Map<String, String> requestParameters)
      throws Throwable {
    final String deviceIdentification =
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);
    final String mastSegment = getString(requestParameters, PlatformKeys.KEY_CDMA_MAST_SEGMENT);
    final Short batchNumber = getShort(requestParameters, PlatformKeys.KEY_CDMA_BATCH_NUMBER);

    LOGGER.info(
        "WHEN: Receive UpdateDeviceCdmaSettingsRequest [deviceIdentification={}, mastSegment={}, batchNumber={}]",
        deviceIdentification,
        mastSegment,
        batchNumber);

    final UpdateDeviceCdmaSettingsRequest request = new UpdateDeviceCdmaSettingsRequest();
    request.setDeviceIdentification(deviceIdentification);
    if (!StringUtils.isBlank(mastSegment)) {
      request.setMastSegment(mastSegment);
    }
    if (batchNumber != null) {
      request.setBatchNumber(batchNumber);
    }

    final UpdateDeviceCdmaSettingsAsyncResponse asyncResponse =
        this.client.updateDeviceCdmaSettings(request);

    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    ScenarioContext.current()
        .put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, asyncResponse.getDeviceId());
  }

  @Then("^the platform should buffer an update device CDMA settings response message$")
  public void thePlatformShouldBufferAnUpdateDeviceCdmaSettingsResponseMessage(
      final Map<String, String> responseParameters) {
    final String deviceIdentification =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);

    final OsgpResultType expectedResult =
        Enum.valueOf(OsgpResultType.class, responseParameters.get(PlatformKeys.KEY_RESULT));

    LOGGER.info(
        "THEN: Buffer UpdateCdmaSettingsResponse [correlationUid={}, deviceIdentification={}, result={}]",
        correlationUid,
        deviceIdentification,
        expectedResult);

    final UpdateDeviceCdmaSettingsAsyncRequest request = new UpdateDeviceCdmaSettingsAsyncRequest();
    request.setDeviceId(deviceIdentification);
    request.setCorrelationUid(correlationUid);

    Wait.until(
        () -> {
          UpdateDeviceCdmaSettingsResponse response = null;
          try {
            response = this.client.getUpdateDeviceCdmaSettingsResponse(request);
          } catch (final WebServiceSecurityException e) {
            // do nothing
          }
          assertThat(response).isNotNull();
          assertThat(response.getResult()).isEqualTo(expectedResult);
        });
  }

  @Then("^the device CDMA settings should be stored in the platform$")
  public void theDeviceCdmaSettingsShouldBeSet(final Map<String, String> settings) {
    final String mastSegment = getString(settings, PlatformKeys.KEY_CDMA_MAST_SEGMENT);
    final Short batchNumber = getShort(settings, PlatformKeys.KEY_CDMA_BATCH_NUMBER);

    CdmaSettings expectedCdmaSettings = null;
    if (StringUtils.isNotBlank(mastSegment) || batchNumber != null) {
      expectedCdmaSettings = new CdmaSettings(mastSegment, batchNumber);
    }

    LOGGER.info(
        "THEN: Store {}", expectedCdmaSettings == null ? "null" : expectedCdmaSettings.toString());

    final String deviceIdentification =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

    if (expectedCdmaSettings == null) {
      assertThat(device.getCdmaSettings()).isNull();
    } else {
      assertThat(device.getCdmaSettings()).isEqualTo(expectedCdmaSettings);
    }
  }
}
