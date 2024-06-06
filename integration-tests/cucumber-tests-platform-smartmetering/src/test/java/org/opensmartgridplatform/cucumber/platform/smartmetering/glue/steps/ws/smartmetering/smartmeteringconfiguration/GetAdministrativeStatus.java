// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetAdministrativeStatusRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GetAdministrativeStatus {

  private static final String CORRELATION_UID_BY_DEVICE_IDENTIFICATION =
      "correlationUidByDeviceIdentification";

  protected static final Logger LOGGER = LoggerFactory.getLogger(GetAdministrativeStatus.class);

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @When("^the get administrative status request is received$")
  public void theRetrieveAdministrativeStatusRequestIsReceived(
      final Map<String, String> requestData) throws Throwable {
    final GetAdministrativeStatusRequest getAdministrativeStatusRequest =
        GetAdministrativeStatusRequestFactory.fromParameterMap(requestData);

    final GetAdministrativeStatusAsyncResponse getAdministrativeStatusAsyncResponse =
        this.smartMeteringConfigurationClient.getAdministrativeStatus(
            getAdministrativeStatusRequest);

    LOGGER.info(
        "Get administrative status asyncResponse is received {}",
        getAdministrativeStatusAsyncResponse);
    assertThat(getAdministrativeStatusAsyncResponse)
        .as("Get administrative status asyncResponse should not be null")
        .isNotNull();

    ScenarioContext.current()
        .put(
            PlatformKeys.KEY_CORRELATION_UID,
            getAdministrativeStatusAsyncResponse.getCorrelationUid());
  }

  @When("^the get administrative status request is received for devices$")
  public void theRetrieveAdministrativeStatusRequestIsReceivedForDevices(
      final List<String> deviceIdentifications) throws Throwable {

    final Map<String, String> parameterMap = new HashMap<>();
    final Map<String, String> correlationUidMap = new HashMap<>();
    for (final String deviceIdentification : deviceIdentifications) {
      parameterMap.put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, deviceIdentification);
      final GetAdministrativeStatusRequest getAdministrativeStatusRequest =
          GetAdministrativeStatusRequestFactory.fromParameterMap(parameterMap);
      final GetAdministrativeStatusAsyncResponse getAdministrativeStatusAsyncResponse =
          this.smartMeteringConfigurationClient.getAdministrativeStatus(
              getAdministrativeStatusRequest);
      assertThat(getAdministrativeStatusAsyncResponse)
          .as("Get administrative status asyncResponse should not be null")
          .isNotNull();
      correlationUidMap.put(
          deviceIdentification, getAdministrativeStatusAsyncResponse.getCorrelationUid());
    }
    ScenarioContext.current().put(CORRELATION_UID_BY_DEVICE_IDENTIFICATION, correlationUidMap);
  }

  @Then("^the administrative status should be returned$")
  public void theAdministrativeStatusShouldBeReturned(final Map<String, String> settings)
      throws Throwable {
    final GetAdministrativeStatusAsyncRequest getAdministrativeStatusAsyncRequest =
        GetAdministrativeStatusRequestFactory.fromScenarioContext();
    final GetAdministrativeStatusResponse getAdministrativeStatusResponse =
        this.smartMeteringConfigurationClient.retrieveGetAdministrativeStatusResponse(
            getAdministrativeStatusAsyncRequest);

    LOGGER.info("The administrative status is: {}", getAdministrativeStatusResponse.getEnabled());

    assertThat(getAdministrativeStatusResponse.getEnabled())
        .as("Administrative status type is null")
        .isNotNull();
  }

  @Then("^the administrative status should be returned for devices$")
  public void theAdministrativeStatusShouldBeReturnedForDevices(
      final List<String> deviceIdentifications) throws Throwable {

    @SuppressWarnings("unchecked")
    final Map<String, String> correlationUidMap =
        (Map<String, String>)
            ScenarioContext.current().get(CORRELATION_UID_BY_DEVICE_IDENTIFICATION);

    final CountDownLatch responseCountDownLatch = new CountDownLatch(deviceIdentifications.size());
    final List<String> responseNotifications = Collections.synchronizedList(new ArrayList<>());

    for (final String deviceIdentification : deviceIdentifications) {
      new Thread(
              () -> {
                final GetAdministrativeStatusAsyncRequest getAdministrativeStatusAsyncRequest;
                synchronized (correlationUidMap) {
                  ScenarioContext.current()
                      .put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, deviceIdentification);
                  ScenarioContext.current()
                      .put(
                          PlatformKeys.KEY_CORRELATION_UID,
                          Objects.requireNonNull(
                              correlationUidMap.get(deviceIdentification),
                              "Correlation UID for request with device identification "
                                  + deviceIdentification
                                  + " must be available"));
                  getAdministrativeStatusAsyncRequest =
                      GetAdministrativeStatusRequestFactory.fromScenarioContext();
                }
                try {
                  final GetAdministrativeStatusResponse getAdministrativeStatusResponse =
                      GetAdministrativeStatus.this.smartMeteringConfigurationClient
                          .retrieveGetAdministrativeStatusResponse(
                              getAdministrativeStatusAsyncRequest);
                  final Instant receivedAt = Instant.now();
                  responseNotifications.add(
                      String.format(
                          "%s - administrative status %s for device %s",
                          receivedAt,
                          getAdministrativeStatusResponse.getEnabled(),
                          deviceIdentification));
                } catch (final WebServiceSecurityException e) {
                  e.printStackTrace();
                } finally {
                  responseCountDownLatch.countDown();
                }
              })
          .start();
    }
    responseCountDownLatch.await();
    LOGGER.info(
        "Received administrative status responses:{}",
        responseNotifications.stream()
            .sorted()
            .collect(
                Collectors.joining(
                    System.lineSeparator() + " - ",
                    System.lineSeparator() + " - ",
                    System.lineSeparator())));
  }
}
