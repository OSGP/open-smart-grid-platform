// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.adhocmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.adhocmanagement.SetRebootRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.adhocmanagement.SetRebootResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreAdHocManagementClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the set light requests steps */
public class SetRebootSteps {

  @Autowired private CoreAdHocManagementClient client;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetRebootSteps.class);

  /**
   * Sends a Get Status request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable
   */
  @When("^receiving a set reboot request$")
  public void receivingASetRebootRequest(final Map<String, String> requestParameters)
      throws Throwable {
    final SetRebootRequest request = new SetRebootRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.setReboot(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @When("^receiving a set reboot request by an unknown organization$")
  public void receivingASetRebootRequestByAnUnknownOrganization(
      final Map<String, String> requestParameters) throws Throwable {
    // Force the request being send to the platform as a given organization.
    ScenarioContext.current()
        .put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

    this.receivingASetRebootRequest(requestParameters);
  }

  /**
   * The check for the response from the Platform.
   *
   * @param expectedResponseData The table with the expected fields in the response.
   * @apiNote The response will contain the correlation uid, so store that in the current scenario
   *     context for later use.
   */
  @Then("^the set reboot async response contains$")
  public void theSetRebootAsyncResponseContains(final Map<String, String> expectedResponseData) {
    final SetRebootAsyncResponse asyncResponse =
        (SetRebootAsyncResponse) ScenarioContext.current().get(PlatformKeys.RESPONSE);

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

  @Then("^the platform buffers a set reboot response message for device \"([^\"]*)\"$")
  public void thenThePlatformBuffersASetRebootResponseMessage(
      final String deviceIdentification, final Map<String, String> expectedResult) {
    final SetRebootAsyncRequest request = new SetRebootAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    Wait.until(
        () -> {
          SetRebootResponse response = null;
          try {
            response = this.client.getSetRebootResponse(request);
          } catch (final Exception e) {
            // do nothing
          }
          assertThat(response).isNotNull();
          assertThat(response.getResult())
              .isEqualTo(
                  Enum.valueOf(OsgpResultType.class, expectedResult.get(PlatformKeys.KEY_RESULT)));
        });
  }

  @Then("^the set reboot async response contains a soap fault$")
  public void theSetRebootAsyncResponseContainsASoapFault(
      final Map<String, String> expectedResult) {
    final SoapFaultClientException response =
        (SoapFaultClientException) ScenarioContext.current().get(PlatformKeys.RESPONSE);

    assertThat(response.getMessage()).isEqualTo(expectedResult.get(PlatformKeys.KEY_MESSAGE));
  }
}
