// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.firmwaremanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareModuleType;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.FirmwareVersion;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.GetFirmwareVersionResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the firmware requests steps */
public class GetFirmwareVersionSteps {
  @Autowired private CoreFirmwareManagementClient client;

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareVersionSteps.class);

  /**
   * Sends a Get Firmware Version request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @Given("^receiving a get firmware version request$")
  public void receivingAGetFirmwareVersionRequest(final Map<String, String> requestParameters)
      throws Throwable {

    final GetFirmwareVersionRequest request = new GetFirmwareVersionRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.getFirmwareVersion(request));
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
   */
  @Then("^the get firmware version async response contains$")
  public void theGetFirmwareVersionResponseContains(
      final Map<String, String> expectedResponseData) {
    final GetFirmwareVersionAsyncResponse asyncResponse =
        (GetFirmwareVersionAsyncResponse) ScenarioContext.current().get(PlatformKeys.RESPONSE);

    assertThat(asyncResponse.getAsyncResponse().getDeviceId())
        .isEqualTo(getString(expectedResponseData, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    assertThat(asyncResponse.getAsyncResponse().getCorrelationUid()).isNotNull();

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

  @Then("^the platform buffers a get firmware version response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersAGetFirmwareVersionResponseMessage(
      final String deviceIdentification, final Map<String, String> expectedResponseData) {
    final GetFirmwareVersionAsyncRequest request = new GetFirmwareVersionAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    final GetFirmwareVersionResponse response =
        Wait.untilAndReturn(
            () -> {
              final GetFirmwareVersionResponse retval = this.client.getGetFirmwareVersion(request);
              assertThat(retval).isNotNull();
              assertThat(retval.getResult())
                  .isEqualTo(
                      getEnum(expectedResponseData, PlatformKeys.KEY_RESULT, OsgpResultType.class));
              return retval;
            });

    if (response.getFirmwareVersion() != null) {
      final FirmwareVersion fwv = response.getFirmwareVersion().get(0);
      if (fwv.getVersion() != null) {
        assertThat(fwv.getVersion())
            .isEqualTo(getString(expectedResponseData, PlatformKeys.FIRMWARE_VERSION));
      }
      if (fwv.getFirmwareModuleType() != null) {
        assertThat(fwv.getFirmwareModuleType())
            .isEqualTo(
                getEnum(
                    expectedResponseData,
                    PlatformKeys.KEY_FIRMWARE_MODULE_TYPE,
                    FirmwareModuleType.class));
      }
    }
  }

  @Then("^the get firmware version response contains soap fault$")
  public void theGetFirmwareVersionResponseContainsSoapFault(
      final Map<String, String> expectedResponseData) {
    GenericResponseSteps.verifySoapFault(expectedResponseData);
  }
}
