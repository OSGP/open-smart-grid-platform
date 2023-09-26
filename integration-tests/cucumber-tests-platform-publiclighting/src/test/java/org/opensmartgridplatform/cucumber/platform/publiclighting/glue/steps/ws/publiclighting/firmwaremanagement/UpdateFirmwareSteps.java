// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.firmwaremanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.DateTimeHelper.getDateTime;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.firmwaremanagement.UpdateFirmwareResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreFirmwareManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.adhocmanagement.SetLightSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the firmware requests steps */
public class UpdateFirmwareSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetLightSteps.class);

  @Autowired private CoreFirmwareManagementClient client;

  /**
   * Sends a Update Firmware request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving an update firmware request$")
  public void receivingAnUpdateFirmwareRequest(final Map<String, String> requestParameters)
      throws Throwable {

    final UpdateFirmwareRequest request = new UpdateFirmwareRequest();

    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    request.setFirmwareIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_FIRMWARE_IDENTIFICATION,
            PlatformCommonDefaults.FIRMWARE_IDENTIFICATION));

    if (requestParameters.containsKey(PlatformCommonKeys.SCHEDULED_TIME)) {
      final GregorianCalendar c = new GregorianCalendar();
      c.setTime(
          Date.from(
              getDateTime(getString(requestParameters, PlatformCommonKeys.SCHEDULED_TIME))
                  .toInstant()));
      final XMLGregorianCalendar scheduledTime =
          DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
      request.setScheduledTime(scheduledTime);
    }

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.updateFirmware(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @Then("^the update firmware async response contains$")
  public void theUpdateFirmwareAsyncResponseContains(final Map<String, String> expectedResponseData)
      throws Throwable {
    final UpdateFirmwareAsyncResponse asyncResponse =
        (UpdateFirmwareAsyncResponse)
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

  @Then("^the update firmware response contains soap fault$")
  public void theUpdateFirmwareResponseContainsSoapFault(final Map<String, String> expectedResult) {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }

  @Then("^the platform buffers a update firmware response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersAUpdateFirmwareResponseMessage(
      final String deviceIdentification, final Map<String, String> expectedResult)
      throws Throwable {
    final UpdateFirmwareAsyncRequest request = new UpdateFirmwareAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    Wait.until(
        () -> {
          UpdateFirmwareResponse response = null;
          try {
            response = this.client.getUpdateFirmware(request);
          } catch (final Exception e) {
            // do nothing
          }
          assertThat(response).isNotNull();
          assertThat(response.getResult())
              .isEqualTo(
                  Enum.valueOf(
                      OsgpResultType.class,
                      expectedResult.get(PlatformPubliclightingKeys.KEY_RESULT)));
        });
  }
}
