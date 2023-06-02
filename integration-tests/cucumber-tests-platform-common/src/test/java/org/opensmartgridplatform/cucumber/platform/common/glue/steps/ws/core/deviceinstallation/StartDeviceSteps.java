//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.deviceinstallation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceInstallationClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class StartDeviceSteps {

  @Autowired private CoreDeviceInstallationClient client;

  @When("receiving a start device request")
  public void receivingAStartDeviceRequest(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final StartDeviceTestRequest request = new StartDeviceTestRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.startDeviceTest(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @Then("^the platform buffers a start device response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersAStartDeviceResponseMessageForDevice(
      final String deviceIdentification, final Map<String, String> expectedResult)
      throws InterruptedException {
    final StartDeviceTestAsyncRequest request = new StartDeviceTestAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    Wait.until(
        () -> {
          StartDeviceTestResponse response = null;
          try {
            response = this.client.getStartDeviceTestResponse(request);
          } catch (final Exception e) {
            // do nothing
          }
          assertThat(response).isNotNull();
          assertThat(response.getResult())
              .isEqualTo(getEnum(expectedResult, PlatformKeys.KEY_RESULT, OsgpResultType.class));
        });
  }

  @Then("^the platform buffers no start device test response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersNoStartDeviceTestResponseMessageForDevice(
      final String deviceIdentification) throws InterruptedException {
    final StartDeviceTestAsyncRequest request = new StartDeviceTestAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    asyncRequest.setCorrelationUid(
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
    request.setAsyncRequest(asyncRequest);

    Wait.until(
        () -> {
          StartDeviceTestResponse response = null;
          try {
            response = this.client.getStartDeviceTestResponse(request);
          } catch (final Exception e) {
            // do nothing
          }
          assertThat(response).isNotNull();
          assertThat(response.getResult()).isNotEqualTo(OsgpResultType.NOT_FOUND);
        });
  }

  @Then("the start device async response contains")
  public void theStartDeviceAsyncResponseContains(final Map<String, String> expectedResponseData)
      throws Throwable {
    final StartDeviceTestAsyncResponse asyncResponse =
        (StartDeviceTestAsyncResponse) ScenarioContext.current().get(PlatformKeys.RESPONSE);

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
  }

  @Then("^the start device response contains soap fault$")
  public void theStartDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }
}
