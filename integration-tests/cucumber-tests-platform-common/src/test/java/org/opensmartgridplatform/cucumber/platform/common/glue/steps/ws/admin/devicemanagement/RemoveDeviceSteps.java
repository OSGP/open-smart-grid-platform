// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.admin.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonDefaults;
import org.opensmartgridplatform.cucumber.platform.common.PlatformCommonKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.admin.AdminDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class RemoveDeviceSteps {

  @Autowired private AdminDeviceManagementClient client;

  /**
   * Send a remove device request to the Platform.
   *
   * @param requestParameters An list with request parameters for the request.
   * @throws IOException
   * @throws GeneralSecurityException
   * @throws WebServiceSecurityException
   */
  @When("^receiving a remove device request$")
  public void receivingARemoveDeviceRequest(final Map<String, String> requestParameters)
      throws WebServiceSecurityException, GeneralSecurityException, IOException {
    final RemoveDeviceRequest request = new RemoveDeviceRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformCommonKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformCommonDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    try {
      ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, this.client.removeDevice(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformCommonKeys.RESPONSE, ex);
      GenericResponseSteps.verifySoapFault(requestParameters);
    }
  }

  /**
   * Send a remove device request to the Platform.
   *
   * @param requestParameters An list with request parameters for the request.
   * @throws Throwable
   */
  @When("^receiving a remove device request with unknown device identification$")
  public void receivingARemoveDeviceRequestWithUnknownDeviceIdentification(
      final Map<String, String> requestParameters) throws Throwable {
    ScenarioContext.current()
        .put(PlatformCommonKeys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

    this.receivingARemoveDeviceRequest(requestParameters);
  }

  /**
   * Send a remove device request to the Platform.
   *
   * @param requestParameters An list with request parameters for the request.
   * @throws Throwable
   */
  @When("^receiving a remove device request with empty device identification$")
  public void receivingARemoveDeviceRequestWithEmptyDeviceIdentification(
      final Map<String, String> requestParameters) throws Throwable {
    this.receivingARemoveDeviceRequest(requestParameters);
  }

  /** The check for the response from the Platform. */
  @Then("^the remove device response is successful$")
  public void theRemoveDeviceResponseIsSuccessful() throws Throwable {
    assertThat(
            ScenarioContext.current().get(PlatformCommonKeys.RESPONSE)
                instanceof RemoveDeviceResponse)
        .isTrue();
  }

  /** The check for the response from the Platform. */
  @Then("^the remove device response contains soap fault$")
  public void theRemoveDeviceResponseContainsSoapFault(final Map<String, String> expectedResult)
      throws Throwable {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }
}
