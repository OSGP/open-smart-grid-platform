// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.SecurityKey;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GenerateAndReplaceKeysRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.ReplaceKeysRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

public class ReplaceKeysSteps extends AbstractSmartMeteringSteps {

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @When("^the replace keys request is received$")
  public void theReplaceKeysRequestIsReceived(final Map<String, String> settings) throws Throwable {
    ScenarioContext.current()
        .put(
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

    this.putKeyInScenarioContext(settings, PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY);
    this.putKeyInScenarioContext(settings, PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY);

    final ReplaceKeysRequest request = ReplaceKeysRequestFactory.fromParameterMap(settings);
    final ReplaceKeysAsyncResponse asyncResponse =
        this.smartMeteringConfigurationClient.replaceKeys(request);
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @When("^multiple replace keys requests are received$")
  public void multipleReplaceKeysRequestsAreReceived(final Map<String, String> settings)
      throws Throwable {
    final List<Map<String, String>> listOfSettingsPerRequest =
        this.createSettingPerRequest(settings);
    final List<String> correlationUIDs = new ArrayList<>();
    for (final Map<String, String> settingsPerRequest : listOfSettingsPerRequest) {
      final ReplaceKeysRequest request =
          ReplaceKeysRequestFactory.fromParameterMap(settingsPerRequest);

      final ReplaceKeysAsyncResponse asyncResponse =
          this.smartMeteringConfigurationClient.replaceKeys(request);

      correlationUIDs.add(asyncResponse.getCorrelationUid());
    }
    ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, correlationUIDs);
  }

  private List<Map<String, String>> createSettingPerRequest(final Map<String, String> settings) {
    final List<Map<String, String>> settingPerRequest = new ArrayList<>();
    final int numberOfRequests = settings.values().iterator().next().split(",").length;
    for (int i = 0; i < numberOfRequests; i++) {
      final Map<String, String> map = new HashMap<>();
      for (final String key : settings.keySet()) {
        final String values = settings.get(key);
        final String value = values.split(",")[i];
        map.put(key, value);
      }
      settingPerRequest.add(map);
    }
    return settingPerRequest;
  }

  private void putKeyInScenarioContext(final Map<String, String> settings, final String key) {
    final String keyName = settings.get(key);
    if (keyName != null) {
      final String soapKey = SecurityKey.valueOf(keyName).getSoapRequestKey();
      ScenarioContext.current().put(key, soapKey);
    }
  }

  @Then("^the replace keys response should be returned$")
  public void theReplaceKeysResponseShouldBeReturned(final Map<String, String> responseParameters)
      throws Throwable {
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    this.assertReplaceKeysResponse(responseParameters, correlationUid);
  }

  @Then("^multiple replace keys responses should be returned$")
  public void multipleReplaceKeysResponsesShouldBeReturned(
      final Map<String, String> responseParameters) throws Throwable {

    final List<String> correlationUids =
        (List<String>) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    for (final String correlationUid : correlationUids) {
      this.assertReplaceKeysResponse(responseParameters, correlationUid);
    }
  }

  private void assertReplaceKeysResponse(
      final Map<String, String> responseParameters, final String correlationUid)
      throws WebServiceSecurityException {
    final Map<String, String> extendedParameters =
        SettingsHelper.addDefault(
            responseParameters, PlatformKeys.KEY_CORRELATION_UID, correlationUid);
    final ReplaceKeysAsyncRequest replaceKeysAsyncRequest =
        ReplaceKeysRequestFactory.fromParameterMapAsync(extendedParameters);

    final ReplaceKeysResponse response =
        this.smartMeteringConfigurationClient.getReplaceKeysResponse(replaceKeysAsyncRequest);

    final String expectedResult = responseParameters.get(PlatformKeys.KEY_RESULT);
    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(expectedResult);
  }

  @Then("^the replace keys response generating an error is received$")
  public void theReplaceKeysResponseGeneratingAnErrorIsReceived(
      final Map<String, String> responseParameters) throws Throwable {
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    final Map<String, String> extendedParameters =
        SettingsHelper.addDefault(
            responseParameters, PlatformKeys.KEY_CORRELATION_UID, correlationUid);
    try {
      final ReplaceKeysAsyncRequest replaceKeysAsyncRequest =
          ReplaceKeysRequestFactory.fromParameterMapAsync(extendedParameters);

      this.smartMeteringConfigurationClient.getReplaceKeysResponse(replaceKeysAsyncRequest);
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @When("^the generate and replace keys request is received$")
  public void theGenerateAndReplaceKeysRequestIsReceived(final Map<String, String> settings)
      throws Throwable {
    ScenarioContext.current()
        .put(
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    final GenerateAndReplaceKeysRequest request =
        GenerateAndReplaceKeysRequestFactory.fromParameterMap(settings);
    final GenerateAndReplaceKeysAsyncResponse asyncResponse =
        this.smartMeteringConfigurationClient.generateAndReplaceKeys(request);

    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("^the generate and replace keys response should be returned$")
  public void theGenerateAndReplaceKeysResponseShouldBeReturned(
      final Map<String, String> responseParameters) throws Throwable {
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    final Map<String, String> extendedParameters =
        SettingsHelper.addDefault(
            responseParameters, PlatformKeys.KEY_CORRELATION_UID, correlationUid);

    final GenerateAndReplaceKeysAsyncRequest generateAndReplaceKeysAsyncRequest =
        GenerateAndReplaceKeysRequestFactory.fromParameterMapAsync(extendedParameters);

    final GenerateAndReplaceKeysResponse response =
        this.smartMeteringConfigurationClient.getGenerateAndReplaceKeysResponse(
            generateAndReplaceKeysAsyncRequest);

    final String expectedResult = responseParameters.get(PlatformKeys.KEY_RESULT);
    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(expectedResult);
  }

  @When("multiple generate and replace keys requests are received")
  public void multipleGenerateAndReplaceKeysRequestsAreReceived(final Map<String, String> settings)
      throws Throwable {
    final List<Map<String, String>> listOfSettingsPerRequest =
        this.createSettingPerRequest(settings);
    final List<String> correlationUIDs = new ArrayList<>();
    for (final Map<String, String> settingsPerRequest : listOfSettingsPerRequest) {
      final GenerateAndReplaceKeysRequest request =
          GenerateAndReplaceKeysRequestFactory.fromParameterMap(settingsPerRequest);

      final GenerateAndReplaceKeysAsyncResponse asyncResponse =
          this.smartMeteringConfigurationClient.generateAndReplaceKeys(request);

      correlationUIDs.add(asyncResponse.getCorrelationUid());
    }
    ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, correlationUIDs);
  }

  @Then("multiple generate and replace keys responses should be returned")
  public void multipleGenerateAndReplaceKeysResponsesShouldBeReturned(
      final Map<String, String> responseParameters) throws Throwable {

    final List<String> correlationUids =
        (List<String>) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    for (final String correlationUid : correlationUids) {
      this.assertGenerateAndReplaceKeysResponse(responseParameters, correlationUid);
    }
  }

  private void assertGenerateAndReplaceKeysResponse(
      final Map<String, String> responseParameters, final String correlationUid)
      throws WebServiceSecurityException {
    final Map<String, String> extendedParameters =
        SettingsHelper.addDefault(
            responseParameters, PlatformKeys.KEY_CORRELATION_UID, correlationUid);
    final GenerateAndReplaceKeysAsyncRequest generateAndReplaceKeysAsyncRequest =
        GenerateAndReplaceKeysRequestFactory.fromParameterMapAsync(extendedParameters);

    final GenerateAndReplaceKeysResponse response =
        this.smartMeteringConfigurationClient.getGenerateAndReplaceKeysResponse(
            generateAndReplaceKeysAsyncRequest);

    final String expectedResult = responseParameters.get(PlatformKeys.KEY_RESULT);
    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(expectedResult);
  }
}
