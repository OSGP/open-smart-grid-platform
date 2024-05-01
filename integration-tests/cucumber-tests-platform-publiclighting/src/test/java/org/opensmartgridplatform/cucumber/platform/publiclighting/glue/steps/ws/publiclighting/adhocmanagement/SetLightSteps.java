// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.adhocmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.SetLightResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingAdHocManagementClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** Class with all the set light requests steps */
public class SetLightSteps {

  @Autowired private PublicLightingAdHocManagementClient client;

  private static final Logger LOGGER = LoggerFactory.getLogger(SetLightSteps.class);

  /**
   * Sends a Set Light request to the platform for a given device identification.
   *
   * @param requestParameters The table with the request parameters.
   * @throws Throwable when an error occurs
   */
  @When("^receiving a set light request$")
  public void receivingASetLightRequest(final Map<String, String> requestParameters)
      throws Throwable {

    final SetLightRequest request = new SetLightRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    final LightValue lightValue = new LightValue();
    lightValue.setIndex(
        getInteger(requestParameters, PlatformKeys.KEY_INDEX, PlatformDefaults.DEFAULT_INDEX));
    if (requestParameters.containsKey(PlatformKeys.KEY_DIMVALUE)
        && !StringUtils.isEmpty(requestParameters.get(PlatformKeys.KEY_DIMVALUE))) {
      lightValue.setDimValue(
          getInteger(
              requestParameters, PlatformKeys.KEY_DIMVALUE, PlatformDefaults.DEFAULT_DIMVALUE));
    }
    lightValue.setOn(
        getBoolean(requestParameters, PlatformKeys.KEY_ON, PlatformDefaults.DEFAULT_ON));
    request.getLightValue().add(lightValue);

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.setLight(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @When(
      "^receiving a set light request with \"([^\"]*)\" valid lightvalues and \"([^\"]*)\" invalid lightvalues$")
  public void receivingAsetLightRequestWithValidLightValuesAndInvalidLightValues(
      final Integer nofValidLightValues,
      final Integer nofInvalidLightValues,
      final Map<String, String> requestParameters)
      throws Throwable {
    final SetLightRequest request = new SetLightRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    for (int i = 0; i < nofValidLightValues; i++) {
      final LightValue lightValue = new LightValue();
      lightValue.setIndex(i + 2);
      lightValue.setDimValue(
          getInteger(
              requestParameters, PlatformKeys.KEY_DIMVALUE, PlatformDefaults.DEFAULT_DIMVALUE));
      lightValue.setOn(
          getBoolean(requestParameters, PlatformKeys.KEY_ON, PlatformDefaults.DEFAULT_ON));
      request.getLightValue().add(lightValue);
    }

    for (int i = 0; i < nofInvalidLightValues; i++) {
      final LightValue lightValue = new LightValue();
      lightValue.setIndex(i + 2 + nofValidLightValues);
      lightValue.setDimValue(50);
      lightValue.setOn(false);
      request.getLightValue().add(lightValue);
    }

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.setLight(request));
    } catch (final SoapFaultClientException ex) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
    }
  }

  @When("^receiving a set light request with \"([^\"]*)\" light values$")
  public void receivingASetLightRequestWithLightValues(
      final Integer nofLightValues, final Map<String, String> requestParameters) throws Throwable {
    final SetLightRequest request = new SetLightRequest();
    request.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

    for (int i = 0; i < nofLightValues; i++) {
      final LightValue lightValue = new LightValue();
      lightValue.setIndex(i + 2);
      lightValue.setDimValue(
          getInteger(
              requestParameters, PlatformKeys.KEY_DIMVALUE, PlatformDefaults.DEFAULT_DIMVALUE));
      lightValue.setOn(
          getBoolean(requestParameters, PlatformKeys.KEY_ON, PlatformDefaults.DEFAULT_ON));
      request.getLightValue().add(lightValue);
    }

    try {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.setLight(request));
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
  @Then("^the set light async response contains$")
  public void theSetLightResponseContains(final Map<String, String> expectedResponseData) {

    final SetLightAsyncResponse asyncResponse =
        (SetLightAsyncResponse) ScenarioContext.current().get(PlatformKeys.RESPONSE);

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

  @Then("^the set light response contains soap fault$")
  public void theSetLightResponseContainsSoapFault(final Map<String, String> expectedResult) {
    GenericResponseSteps.verifySoapFault(expectedResult);
  }

  @Then("^the platform buffers a set light response message for device \"([^\"]*)\"$")
  public void thePlatformBuffersASetLightResponseMessage(
      final String deviceIdentification, final Map<String, String> expectedResult) {
    final SetLightAsyncRequest request = new SetLightAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setDeviceId(deviceIdentification);
    String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    System.out.println("correlationUid: "+correlationUid);
    asyncRequest.setCorrelationUid(correlationUid);
    request.setAsyncRequest(asyncRequest);

    Wait.until(
        () -> {
          SetLightResponse response = null;
          try {
            response = this.client.getSetLightResponse(request);
          } catch (final Exception e) {
            // do nothing
          }
          assertThat(response).isNotNull();
          assertThat(response.getResult())
              .isEqualTo(
                  Enum.valueOf(OsgpResultType.class, expectedResult.get(PlatformKeys.KEY_RESULT)));
        });
  }
}
