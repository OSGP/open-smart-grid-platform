// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    this.throttlingConfig = new ThrottlingConfig("throttling-client-test", 2, 3, 4, 5);
    this.throttlingClient =
        new ThrottlingClient(
            this.throttlingConfig,
            this.mockWebServer.url("/").url().toExternalForm(),
            Duration.ofSeconds(1),
            2,
            10);
  }

  private MockResponse requestReceivedAtUnexpectedEndpointResponse() {
    return new MockResponse().setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value());
  }

  private MockResponse okWithIdResponse(final int id) {
    return new MockResponse()
        .setResponseCode(HttpStatus.OK.value())
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody(String.valueOf(id));
  }

  private MockResponse notFound() {
    return new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value());
  }

  private void whenTheThrottlingServiceReturnsFailureOnRegistration() {
    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {
            if (ThrottlingClientTest.this.isThrottlingConfigRegister(request)) {
              return ThrottlingClientTest.this.notFound();
            }

            return ThrottlingClientTest.this.requestReceivedAtUnexpectedEndpointResponse();
          }
        });
  }

  private boolean isClientRegister(final RecordedRequest request) {
    return "/clients".equals(request.getPath())
        && request.getBodySize() == 0
        && "POST".equals(request.getMethod());
  }

  private boolean isThrottlingConfigRegister(final RecordedRequest request) {
    return "/throttling-configs".equals(request.getPath())
        && "{\"name\":\"throttling-client-test\",\"maxConcurrency\":2,\"maxNewConnections\":3,\"maxNewConnectionsResetTimeInMs\":4,\"maxNewConnectionsWaitTimeInMs\":5}"
            .equals(request.getBody().readUtf8())
        && "POST".equals(request.getMethod());
  }

  @Test
  void clientIsUnregisteredWithTheThrottlingService() {
    final int clientId = 42;
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingServiceAcceptsUnregistrationWithClientID(clientId);

    this.throttlingClient.unregister();

    assertThat(this.throttlingClient.getClientId()).isNull();
  }

  @Test
  void unregisteredClientIsUnregisteredWithTheThrottlingService() {
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

            return ThrottlingClientTest.this.requestReceivedAtUnexpectedEndpointResponse();
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
    final int priority = 8;
    this.whenTheThrottlingConfigIsIdentifiedById(throttlingConfigId);
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingClientUsesNextRequestId(requestId);
    this.whenTheThrottlingServiceGrantsTheRequestedPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, priority);

    final Permit expectedPermit =
        new Permit(throttlingConfigId, clientId, requestId, baseTransceiverStationId, cellId, null);

    final Optional<Permit> requestedPermit =
        this.throttlingClient.requestPermit(baseTransceiverStationId, cellId, priority);

    assertThat(requestedPermit)
        .usingRecursiveComparison()
        .ignoringExpectedNullFields()
        .isEqualTo(Optional.of(expectedPermit));
  }

  @Test
  void unregisteredClientRequestsPermitByNetworkSegment() {
    final short throttlingConfigId = 37;
    final int clientId = 347198;
    final int baseTransceiverStationId = 983745;
    final int cellId = 2;
    final int requestId = 894;
    final int priority = 7;
    this.whenTheThrottlingClientUsesNextRequestId(requestId);
    this.whenTheThrottlingServiceGrantsTheRequestedPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, priority);

    final Permit expectedPermit =
        new Permit(throttlingConfigId, clientId, requestId, baseTransceiverStationId, cellId, null);

    final Optional<Permit> requestedPermit =
        this.throttlingClient.requestPermit(baseTransceiverStationId, cellId, priority);

    assertThat(requestedPermit)
        .usingRecursiveComparison()
        .ignoringExpectedNullFields()
        .isEqualTo(Optional.of(expectedPermit));
  }

  @Test
  void registerFailureClientRequestsPermitByNetworkSegment() {
    final int baseTransceiverStationId = 983745;
    final int cellId = 2;
    final int priority = 7;
    this.whenTheThrottlingServiceReturnsFailureOnRegistration();

    final Optional<Permit> requestedPermit =
        this.throttlingClient.requestPermit(baseTransceiverStationId, cellId, priority);

    assertThat(requestedPermit).isNotPresent();
  }

  private boolean isPermitRequestForNetworkSegment(
      final RecordedRequest request,
      final String method,
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    return String.format(
                "/permits/%d/%d/%d/%d",
                throttlingConfigId, clientId, baseTransceiverStationId, cellId)
            .equals(request.getPath())
        && requestId == Integer.parseInt(request.getBody().readUtf8())
        && method.equals(request.getMethod());
  }

  private boolean isPermitRequestForNetworkSegment(
      final RecordedRequest request,
      final String method,
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId,
      final int priority) {

    return String.format(
                "/permits/%d/%d/%d/%d?priority=%d",
                throttlingConfigId, clientId, baseTransceiverStationId, cellId, priority)
            .equals(request.getPath())
        && requestId == Integer.parseInt(request.getBody().readUtf8())
        && method.equals(request.getMethod());
  }

  private boolean isPermitRequestForUnknownNetworkSegment(
      final RecordedRequest request,
      final String method,
      final short throttlingConfigId,
      final int clientId,
      final int requestId) {

    return String.format("/permits/%d/%d", throttlingConfigId, clientId).equals(request.getPath())
        && requestId == Integer.parseInt(request.getBody().readUtf8())
        && method.equals(request.getMethod());
  }

  private boolean isPermitRequestForUnknownNetworkSegment(
      final RecordedRequest request,
      final String method,
      final short throttlingConfigId,
      final int clientId,
      final int requestId,
      final int priority) {

    return String.format("/permits/%d/%d?priority=%d", throttlingConfigId, clientId, priority)
            .equals(request.getPath())
        && requestId == Integer.parseInt(request.getBody().readUtf8())
        && method.equals(request.getMethod());
  }

  private void whenTheThrottlingServiceGrantsTheRequestedPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId,
      final int priority) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {
            if (ThrottlingClientTest.this.isThrottlingConfigRegister(request)) {
              return ThrottlingClientTest.this.okWithIdResponse(throttlingConfigId);
            }
            if (ThrottlingClientTest.this.isClientRegister(request)) {
              return ThrottlingClientTest.this.okWithIdResponse(clientId);
            }

            if (ThrottlingClientTest.this.isPermitRequestForNetworkSegment(
                request,
                "POST",
                throttlingConfigId,
                clientId,
                baseTransceiverStationId,
                cellId,
                requestId,
                priority)) {

              return ThrottlingClientTest.this.permitRequestGrantedResponse();
            }

            return ThrottlingClientTest.this.requestReceivedAtUnexpectedEndpointResponse();
          }
        });
  }

  private void whenTheThrottlingServiceGrantsTheRequestedPermit(
      final short throttlingConfigId, final int clientId, final int requestId, final int priority) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {
            if (ThrottlingClientTest.this.isThrottlingConfigRegister(request)) {
              return ThrottlingClientTest.this.okWithIdResponse(throttlingConfigId);
            }
            if (ThrottlingClientTest.this.isClientRegister(request)) {
              return ThrottlingClientTest.this.okWithIdResponse(clientId);
            }

            if (ThrottlingClientTest.this.isPermitRequestForUnknownNetworkSegment(
                request, "POST", throttlingConfigId, clientId, requestId, priority)) {

              return ThrottlingClientTest.this.permitRequestGrantedResponse();
            }

            return ThrottlingClientTest.this.requestReceivedAtUnexpectedEndpointResponse();
          }
        });
  }

  private MockResponse permitRequestGrantedResponse() {
    return new MockResponse()
        .setResponseCode(HttpStatus.OK.value())
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("1");
  }

  @Test
  void clientRequestsPermitForUnknownNetworkSegment() {
    final short throttlingConfigId = 5456;
    final int clientId = 573467;
    final int requestId = 946585809;
    final int priority = 8;
    this.whenTheThrottlingConfigIsIdentifiedById(throttlingConfigId);
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingClientUsesNextRequestId(requestId);
    this.whenTheThrottlingServiceGrantsTheRequestedPermit(
        throttlingConfigId, clientId, requestId, priority);

    final Permit expectedPermit =
        new Permit(throttlingConfigId, clientId, requestId, null, null, null);

    final Optional<Permit> requestedPermit = this.throttlingClient.requestPermit(priority);

    assertThat(requestedPermit)
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(Instant.class)
        .isEqualTo(Optional.of(expectedPermit));
  }

  @Test
  void unregisteredClientRequestsPermitForUnknownNetworkSegment() {
    final short throttlingConfigId = 5456;
    final int clientId = 573467;
    final int requestId = 946585809;
    final int priority = 4;
    this.whenTheThrottlingClientUsesNextRequestId(requestId);
    this.whenTheThrottlingServiceGrantsTheRequestedPermit(
        throttlingConfigId, clientId, requestId, priority);

    final Permit expectedPermit =
        new Permit(throttlingConfigId, clientId, requestId, null, null, null);

    final Optional<Permit> requestedPermit = this.throttlingClient.requestPermit(priority);

    assertThat(requestedPermit)
        .usingRecursiveComparison()
        .ignoringFieldsOfTypes(Instant.class)
        .isEqualTo(Optional.of(expectedPermit));
  }

  @Test
  void registerFailureClientRequestsPermitForUnknownNetworkSegment() {
    final int priority = 8;
    this.whenTheThrottlingServiceReturnsFailureOnRegistration();

    final Optional<Permit> requestedPermit = this.throttlingClient.requestPermit(priority);

    assertThat(requestedPermit).isNotPresent();
  }

  @Test
  void clientRequestsPermitWhichIsNotGranted() {
    final short throttlingConfigId = 82;
    final int clientId = 9281;
    final int baseTransceiverStationId = 3498;
    final int cellId = 3;
    final int requestId = 311;
    final int priority = 8;
    this.whenTheThrottlingConfigIsIdentifiedById(throttlingConfigId);
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingClientUsesNextRequestId(requestId);
    this.whenTheThrottlingServiceRejectsTheRequestedPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, priority);

    final Optional<Permit> requestedPermit =
        this.throttlingClient.requestPermit(baseTransceiverStationId, cellId, priority);

    assertThat(requestedPermit).isEmpty();
  }

  @Test
  void anExceptionIsThrownWhenThePermitIsNotGrantedCallingTheMethodWithNullableIds() {
    final short throttlingConfigId = 32;
    final int clientId = 43;
    final int requestId = 54;
    final int priority = 8;
    this.whenTheThrottlingConfigIsIdentifiedById(throttlingConfigId);
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingClientUsesNextRequestId(requestId);
    this.whenTheThrottlingServiceRejectsTheRequestedPermit(
        throttlingConfigId, clientId, requestId, priority);

    assertThrows(
        ThrottlingPermitDeniedException.class,
        () ->
            this.throttlingClient.requestPermitUsingNetworkSegmentIfIdsAreAvailable(
                null, null, priority));
  }

  private void whenTheThrottlingServiceRejectsTheRequestedPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId,
      final int priority) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {

            if (ThrottlingClientTest.this.isPermitRequestForNetworkSegment(
                request,
                "POST",
                throttlingConfigId,
                clientId,
                baseTransceiverStationId,
                cellId,
                requestId,
                priority)) {

              return ThrottlingClientTest.this.permitRequestRejectedResponse();
            }

            return ThrottlingClientTest.this.requestReceivedAtUnexpectedEndpointResponse();
          }
        });
  }

  private void whenTheThrottlingServiceRejectsTheRequestedPermit(
      final short throttlingConfigId, final int clientId, final int requestId, final int priority) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {

            if (ThrottlingClientTest.this.isPermitRequestForUnknownNetworkSegment(
                request, "POST", throttlingConfigId, clientId, requestId, priority)) {

              return ThrottlingClientTest.this.permitRequestRejectedResponse();
            }

            return ThrottlingClientTest.this.requestReceivedAtUnexpectedEndpointResponse();
          }
        });
  }

  private MockResponse permitRequestRejectedResponse() {
    return new MockResponse()
        .setResponseCode(HttpStatus.CONFLICT.value())
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("0");
  }

  @Test
  void clientReleasesPermitForNetworkSegment() {
    final short throttlingConfigId = 901;
    final int clientId = 4518988;
    final int baseTransceiverStationId = 10029;
    final int cellId = 1;
    final int requestId = 23938477;
    final int priority = 4;
    this.whenTheThrottlingConfigIsIdentifiedById(throttlingConfigId);
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingServiceReleasesThePermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, true);

    final Permit permitToBeReleased =
        new Permit(
            throttlingConfigId,
            clientId,
            requestId,
            baseTransceiverStationId,
            cellId,
            Instant.now().minusSeconds(3));

    final boolean released = this.throttlingClient.releasePermit(permitToBeReleased);

    assertThat(released).isTrue();
  }

  @Test
  void unregisteredClientReleasesPermitForNetworkSegment() {
    final short throttlingConfigId = 901;
    final int clientId = 4518988;
    final int baseTransceiverStationId = 10029;
    final int cellId = 1;
    final int requestId = 23938477;
    final int priority = 7;
    this.whenTheThrottlingServiceReleasesThePermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId, true);

    final Permit permitToBeReleased =
        new Permit(
            throttlingConfigId,
            clientId,
            requestId,
            baseTransceiverStationId,
            cellId,
            Instant.now().minusSeconds(3));

    final boolean released = this.throttlingClient.releasePermit(permitToBeReleased);

    assertThat(released).isTrue();
  }

  @Test
  void registerFailureClientReleasesPermitForNetworkSegment() {
    final short throttlingConfigId = 901;
    final int clientId = 4518988;
    final int baseTransceiverStationId = 10029;
    final int cellId = 1;
    final int requestId = 23938477;
    this.whenTheThrottlingServiceReturnsFailureOnRegistration();
    final Permit permitToBeReleased =
        new Permit(
            throttlingConfigId,
            clientId,
            requestId,
            baseTransceiverStationId,
            cellId,
            Instant.now().minusSeconds(3));

    final boolean released = this.throttlingClient.releasePermit(permitToBeReleased);

    assertThat(released).isFalse();
  }

  @Test
  void clientReleasesPermitThatIsNotHeldForUnknownNetworkSegment() {
    final short throttlingConfigId = 11;
    final int clientId = 18;
    final int requestId = 21;
    final int priority = 4;
    this.whenTheThrottlingConfigIsIdentifiedById(throttlingConfigId);
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingServiceReleasesThePermit(
        throttlingConfigId, clientId, requestId, priority, false);

    final Permit permitToBeReleased =
        new Permit(
            throttlingConfigId, clientId, requestId, null, null, Instant.now().minusSeconds(2));

    final boolean released = this.throttlingClient.releasePermit(permitToBeReleased);

    assertThat(released).isFalse();
  }

  private void whenTheThrottlingServiceReleasesThePermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId,
      final boolean permitIsHeld) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {
            if (ThrottlingClientTest.this.isThrottlingConfigRegister(request)) {
              return ThrottlingClientTest.this.okWithIdResponse(throttlingConfigId);
            }
            if (ThrottlingClientTest.this.isClientRegister(request)) {
              return ThrottlingClientTest.this.okWithIdResponse(clientId);
            }

            if (ThrottlingClientTest.this.isPermitRequestForNetworkSegment(
                request,
                "DELETE",
                throttlingConfigId,
                clientId,
                baseTransceiverStationId,
                cellId,
                requestId)) {

              return new MockResponse()
                  .setResponseCode(
                      permitIsHeld ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value());
            }

            return ThrottlingClientTest.this.requestReceivedAtUnexpectedEndpointResponse();
          }
        });
  }

  private void whenTheThrottlingServiceReleasesThePermit(
      final short throttlingConfigId,
      final int clientId,
      final int requestId,
      final int priority,
      final boolean permitIsHeld) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {

            if (ThrottlingClientTest.this.isPermitRequestForUnknownNetworkSegment(
                request, "DELETE", throttlingConfigId, clientId, requestId)) {

              return new MockResponse()
                  .setResponseCode(
                      permitIsHeld ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value());
            }

            return ThrottlingClientTest.this.requestReceivedAtUnexpectedEndpointResponse();
          }
        });
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void clientDiscardsPermitOfWhichItDoesNotKnowWhetherItWasGranted(final boolean permitWasGranted) {
    /*
     * If the client, for instance due to network errors, has requested a permit, but did not
     * receive a response whether it was granted or denied, the client can discard the permit by the
     * requestId that is unique to the client.
     */
    final short throttlingConfigId = 99;
    final int clientId = 33;
    final int requestId = 121;
    this.whenTheThrottlingConfigIsIdentifiedById(throttlingConfigId);
    this.whenTheThrottlingClientHasRegisteredWithId(clientId);
    this.whenTheThrottlingServiceDiscardsThePermit(clientId, requestId, permitWasGranted);

    assertDoesNotThrow(() -> this.throttlingClient.discardPermit(requestId));
  }

  private void whenTheThrottlingServiceDiscardsThePermit(
      final int clientId, final int requestId, final boolean permitIsHeld) {

    this.mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(final RecordedRequest request) {

            if (String.format("/permits/discard/%d/%d", clientId, requestId)
                    .equals(request.getPath())
                && request.getBodySize() == 0
                && "DELETE".equals(request.getMethod())) {

              return new MockResponse()
                  .setResponseCode(
                      permitIsHeld ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value());
            }

            return ThrottlingClientTest.this.requestReceivedAtUnexpectedEndpointResponse();
          }
        });
  }
}
