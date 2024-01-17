// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.functional.exceptions;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle.BundleSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration.GetAdministrativeStatus;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SpecificAttributeValueRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.BundleRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SmartMeteringBundleClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetAdministrativeStatusRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.AddDeviceRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ActualMeterReadsGasRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class FunctionalExceptionsSteps {

  /**
   * Some tests, for instance with some connection exceptions, require a relatively long wait time
   * before the actual response is available.
   *
   * <p>For steps that are used in scenarios requiring a longer wait time, as well as in scenarios
   * that don't, the same step may be used, looking at {@link #useLongWaitTime} to see which wait
   * time to apply.
   */
  private static final int LONG_WAIT_TIME = 900000;

  @Autowired private GetAdministrativeStatus getAdministrativeStatus;

  @Autowired private BundleSteps bundleSteps;

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired private SmartMeteringInstallationClient smartMeteringInstallationClient;

  @Autowired
  private SmartMeteringMonitoringRequestClient<
          ActualMeterReadsGasAsyncResponse, ActualMeterReadsGasRequest>
      actualMeterReadsGasRequestClient;

  @Autowired private SmartMeteringBundleClient smartMeteringBundleClient;

  @Autowired
  private SmartMeteringMonitoringResponseClient<
          ActualMeterReadsGasResponse, ActualMeterReadsGasAsyncRequest>
      actualMeterReadsGasResponseClient;

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest>
      getSpecificAttributeValueRequestClient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest>
      getSpecificAttributeValueResponseClient;

  private boolean useLongWaitTime;

  @Before
  public void before(final Scenario scenario) {
    this.useLongWaitTime = scenario.getSourceTagNames().contains("@NightlyBuildOnly");
  }

  @When("^the get administrative status request for an invalid organisation is received$")
  public void theRetrieveAdministrativeStatusRequestForAnInvalidOrganisationIsReceived(
      final Map<String, String> requestData) throws Throwable {
    final GetAdministrativeStatusRequest getAdministrativeStatusRequest =
        GetAdministrativeStatusRequestFactory.fromParameterMap(requestData);

    if (requestData.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
      final String organisation =
          getString(requestData, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION);
      ScenarioContext.current().put(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION, organisation);
    }

    try {
      this.smartMeteringConfigurationClient.getAdministrativeStatus(getAdministrativeStatusRequest);
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @When("^the get administrative status request for an invalid device is received$")
  public void theGetAdministrativeStatusRequestForAnInvalidDeviceIsReceived(
      final Map<String, String> requestData) throws Throwable {
    final GetAdministrativeStatusRequest getAdministrativeStatusRequest =
        GetAdministrativeStatusRequestFactory.fromParameterMap(requestData);
    try {
      this.smartMeteringConfigurationClient.getAdministrativeStatus(getAdministrativeStatusRequest);
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @Then("^the add device response for an existing device is received$")
  public void theAddDeviceResponseForAnExistingDeviceIsReceived(
      final Map<String, String> responseParameters) throws Throwable {
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    final Map<String, String> extendedParameters =
        SettingsHelper.addDefault(
            responseParameters, PlatformKeys.KEY_CORRELATION_UID, correlationUid);

    final AddDeviceAsyncRequest addDeviceAsyncRequest =
        AddDeviceRequestFactory.fromParameterMapAsync(extendedParameters);
    try {
      this.smartMeteringInstallationClient.getAddDeviceResponse(addDeviceAsyncRequest);
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @When("^receiving a smartmetering add device request with an invalid DSMR version$")
  public void receivingASmartmeteringAddDeviceRequestWithAnInvalidDsmrVersion(
      final Map<String, String> settings) throws Throwable {

    final AddDeviceRequest request = AddDeviceRequestFactory.fromParameterMap(settings);
    try {
      final AddDeviceAsyncResponse asyncResponse =
          this.smartMeteringInstallationClient.addDevice(request);
      final AddDeviceAsyncRequest addDeviceAsyncRequest = new AddDeviceAsyncRequest();
      addDeviceAsyncRequest.setCorrelationUid(asyncResponse.getCorrelationUid());
      addDeviceAsyncRequest.setDeviceIdentification(asyncResponse.getDeviceIdentification());
      this.smartMeteringInstallationClient.getAddDeviceResponse(addDeviceAsyncRequest);
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @When("^the get administrative status request generating an error is received$")
  public void theGetAdministrativeStatusRequestGeneratingAnErrorIsReceived(
      final Map<String, String> settings) throws Throwable {

    this.getAdministrativeStatus.theRetrieveAdministrativeStatusRequestIsReceived(settings);

    final GetAdministrativeStatusAsyncRequest getAdministrativeStatusAsyncRequest =
        GetAdministrativeStatusRequestFactory.fromScenarioContext();

    if (this.useLongWaitTime) {
      this.smartMeteringConfigurationClient.setWaitFailMillis(LONG_WAIT_TIME);
    }

    try {
      this.smartMeteringConfigurationClient.retrieveGetAdministrativeStatusResponse(
          getAdministrativeStatusAsyncRequest);
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @When("^the get actual meter reads gas request generating an error is received$")
  public void theGetActualMeterReadsGasRequestGeneratingAnErrorIsReceived(
      final Map<String, String> requestData) throws Throwable {
    final ActualMeterReadsGasRequest request =
        ActualMeterReadsGasRequestFactory.fromParameterMap(requestData);
    final ActualMeterReadsGasAsyncResponse asyncResponse =
        this.actualMeterReadsGasRequestClient.doRequest(request);
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());

    final ActualMeterReadsGasAsyncRequest asyncRequest =
        ActualMeterReadsGasRequestFactory.fromScenarioContext();

    try {
      this.actualMeterReadsGasResponseClient.getResponse(asyncRequest);
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @When("^the get specific attribute value request generating an error is received$")
  public void whenTheGetSpecificAttributeValueRequestGeneratingAnErrorIsReceived(
      final Map<String, String> settings) throws Throwable {
    final GetSpecificAttributeValueRequest request =
        SpecificAttributeValueRequestFactory.fromParameterMap(settings);
    final GetSpecificAttributeValueAsyncResponse asyncResponse =
        this.getSpecificAttributeValueRequestClient.doRequest(request);
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());

    final GetSpecificAttributeValueAsyncRequest asyncRequest =
        SpecificAttributeValueRequestFactory.fromScenarioContext();

    try {
      this.getSpecificAttributeValueResponseClient.getResponse(asyncRequest);
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @When("^the bundle request generating an error is received with headers$")
  public void theBundleRequestGeneratingAnErrorIsReceivedWithHeaders(
      final Map<String, String> settings) throws Throwable {

    this.bundleSteps.theBundleRequestIsReceivedWithHeaders(settings);

    final BundleAsyncRequest asyncRequest = BundleRequestFactory.fromScenarioContext();

    if (this.useLongWaitTime) {
      this.smartMeteringConfigurationClient.setWaitFailMillis(LONG_WAIT_TIME);
    }

    try {
      this.smartMeteringBundleClient.retrieveBundleResponse(asyncRequest);
    } catch (final Exception exception) {
      ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
    }
  }

  @When("^the bundle request generating an error is received$")
  public void theBundleRequestGeneratingAnErrorIsReceived() throws Throwable {
    this.theBundleRequestGeneratingAnErrorIsReceivedWithHeaders(new HashMap<>());
  }
}
