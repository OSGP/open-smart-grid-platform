/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.throttling.api.Permit;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

class ThrottlingClientTest {

  private MockWebServer mockWebServer;

  private ThrottlingConfig throttlingConfig;
  private ThrottlingClient throttlingClient;

  @BeforeEach
  void beforeEach() {
    this.mockWebServer = new MockWebServer();
    this.throttlingConfig = new ThrottlingConfig("throttling-client-test", 2);
    this.throttlingClient =
        new ThrottlingClient(
            this.throttlingConfig,
            this.mockWebServer.url("/").url().toExternalForm(),
            Duration.ofSeconds(1));
  }

  @Test
  void throttlingConfigAndClientAreRegisteredWithTheThrottlingService() {
    final short throttlingConfigId = 83;
    final int clientId = 42;
    this.whenTheThrottlingServiceReturnsIdsOnRegistration(throttlingConfigId, clientId);

    this.throttlingClient.register();

    assertThat(this.throttlingConfig.getId()).isEqualTo(throttlingConfigId);
    assertThat(this.throttlingClient.getClientId()).isEqualTo(clientId);
  }

  private void whenTheThrottlingServiceReturnsIdsOnRegistration(
      final short throttlingConfigId, final int clientId) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {

            if ("/throttling-configs".equals(request.getPath())
                && "{\"name\":\"throttling-client-test\",\"maxConcurrency\":2}"
                    .equals(request.getBody().readUtf8())
                && "POST".equals(request.getMethod())) {

              return new MockResponse()
                  .setResponseCode(200)
                  .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .setBody(String.valueOf(throttlingConfigId));
            }

            if ("/clients".equals(request.getPath())
                && request.getBodySize() == 0
                && "POST".equals(request.getMethod())) {

              return new MockResponse()
                  .setResponseCode(200)
                  .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .setBody(String.valueOf(clientId));
            }

            return new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value());
          }
        });
  }

  @Test
  void clientIsUnregisteredWithTheThrottlingService() {
    final int clientId = 42;
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingServiceAcceptsUnregistrationWithClientID(clientId);

    this.throttlingClient.unregister();

    assertThat(this.throttlingClient.getClientId()).isNull();
  }

  private void whenTheThrottlingConfigIsIdentifiedById(final short throttlingConfigId) {
    this.throttlingConfig.setId(throttlingConfigId);
  }

  private void whenTheThrottlingClientHasRegisteredWithId(final int clientId) {
    ReflectionTestUtils.setField(this.throttlingClient, "clientId", clientId);
  }

  private void whenTheThrottlingClientUsesNextRequestId(final int requestId) {
    ReflectionTestUtils.setField(
        this.throttlingClient, "requestIdCounter", new AtomicInteger(requestId - 1));
  }

  private void whenTheThrottlingServiceAcceptsUnregistrationWithClientID(final int clientId) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {

            if (String.format("/clients/%d", clientId).equals(request.getPath())
                && "DELETE".equals(request.getMethod())) {

              return new MockResponse().setResponseCode(HttpStatus.ACCEPTED.value());
            }

            return new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value());
          }
        });
  }

  @Test
  void clientRequestsPermitByNetworkSegment() {
    final short throttlingConfigId = 37;
    final int clientId = 347198;
    final int baseTransceiverStationId = 983745;
    final int cellId = 2;
    final int requestId = 894;
    this.whenTheThrottlingConfigIsIdentifiedById(throttlingConfigId);
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingClientUsesNextRequestId(requestId);
    this.whenTheThrottlingServiceGrantsTheRequestedPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);

    final Permit expectedPermit =
        new Permit(throttlingConfigId, clientId, requestId, baseTransceiverStationId, cellId, null);

    final Optional<Permit> requestedPermit =
        this.throttlingClient.requestPermit(baseTransceiverStationId, cellId);

    assertThat(requestedPermit)
        .usingRecursiveComparison()
        .ignoringExpectedNullFields()
        .isEqualTo(Optional.of(expectedPermit));
  }

  private void whenTheThrottlingServiceGrantsTheRequestedPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {

            if (String.format(
                        "/permits/%d/%d/%d/%d",
                        throttlingConfigId, clientId, baseTransceiverStationId, cellId)
                    .equals(request.getPath())
                && requestId == Integer.parseInt(request.getBody().readUtf8())
                && "POST".equals(request.getMethod())) {

              return new MockResponse()
                  .setResponseCode(HttpStatus.OK.value())
                  .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .setBody("1");
            }

            return new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value());
          }
        });
  }

  private void whenTheThrottlingServiceGrantsTheRequestedPermit(
      final short throttlingConfigId, final int clientId, final int requestId) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {

            if (String.format("/permits/%d/%d", throttlingConfigId, clientId)
                    .equals(request.getPath())
                && requestId == Integer.parseInt(request.getBody().readUtf8())
                && "POST".equals(request.getMethod())) {

              return new MockResponse()
                  .setResponseCode(HttpStatus.OK.value())
                  .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .setBody("1");
            }

            return new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value());
          }
        });
  }

  @Test
  void clientRequestsPermitForUnknownNetworkSegment() {
    final short throttlingConfigId = 5456;
    final int clientId = 573467;
    final int requestId = 946585809;
    this.whenTheThrottlingConfigIsIdentifiedById(throttlingConfigId);
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingClientUsesNextRequestId(requestId);
    this.whenTheThrottlingServiceGrantsTheRequestedPermit(throttlingConfigId, clientId, requestId);

    final Permit expectedPermit =
        new Permit(throttlingConfigId, clientId, requestId, null, null, null);

    final Optional<Permit> requestedPermit = this.throttlingClient.requestPermit();

    assertThat(requestedPermit)
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(Instant.class)
        .isEqualTo(Optional.of(expectedPermit));
  }
}
