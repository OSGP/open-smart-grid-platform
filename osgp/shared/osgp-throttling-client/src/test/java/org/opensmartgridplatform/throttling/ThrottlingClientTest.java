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
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

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
                && "{\"name\":\"throttling-client\"}".equals(request.getBody().readUtf8())
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
    this.whenTheThrottlingServiceAcceptsUnregistrationWithClientID(42);

    this.throttlingClient.unregister();

    assertThat(this.throttlingClient.getClientId()).isNull();
  }

  private void whenTheThrottlingServiceAcceptsUnregistrationWithClientID(final int clientId) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {

            if (String.format("/clients/%d", clientId).equals(request.getPath())
                && "DELETE".equals(request.getMethod())) {

              return new MockResponse()
                  .setResponseCode(200)
                  .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                  .setBody(String.valueOf(System.currentTimeMillis()));
            }

            return new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value());
          }
        });
  }
}
