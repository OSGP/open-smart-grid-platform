// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.opensmartgridplatform.throttling.api.Permit;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/** Client for distributed segmented network max concurrency throttling. */
public class ThrottlingClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingClient.class);

  private Integer clientId;
  private final AtomicInteger requestIdCounter = new AtomicInteger(0);
  private final ThrottlingConfig throttlingConfig;
  private final RestTemplate restTemplate;

  public ThrottlingClient(
      final ThrottlingConfig throttlingConfig,
      final String throttlingServiceUrl,
      final Duration timeout,
      final int maxConnPerRoute,
      final int maxConnTotal) {

    this.throttlingConfig =
        Objects.requireNonNull(throttlingConfig, "throttlingConfig must not be null");
    this.restTemplate =
        this.createAndConfigureRestTemplate(
            Objects.requireNonNull(throttlingServiceUrl, "throttlingServiceUrl must not be null"),
            Objects.requireNonNull(timeout, "timeout must not be null"),
            Objects.requireNonNull(maxConnPerRoute, "maxConnPerRoute must not be null"),
            Objects.requireNonNull(maxConnTotal, "maxConnTotal must not be null"));
  }

  private RestTemplate createAndConfigureRestTemplate(
      final String throttlingServiceUrl,
      final Duration timeout,
      final int maxConnPerRoute,
      final int maxConnTotal) {

    final HttpClient httpClient =
        HttpClientBuilder.create()
            .setMaxConnPerRoute(maxConnPerRoute)
            .setMaxConnTotal(maxConnTotal)
            .build();
    final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
        new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setHttpClient(httpClient);
    clientHttpRequestFactory.setConnectTimeout((int) timeout.toMillis());

    final RestTemplate template = new RestTemplate(clientHttpRequestFactory);

    final DefaultUriBuilderFactory uriBuilderFactory =
        new DefaultUriBuilderFactory(throttlingServiceUrl);
    template.setUriTemplateHandler(uriBuilderFactory);

    template.setErrorHandler(
        new DefaultResponseErrorHandler() {
          @Override
          public void handleError(final ClientHttpResponse response, final HttpStatus statusCode)
              throws IOException {
            if (statusCode == HttpStatus.NOT_FOUND || statusCode == HttpStatus.CONFLICT) {
              /*
               * Do not treat HTTP status not found or conflict as an error. With the throttling API
               * these are regular response statuses for a number of requests, for instance when
               * discarding a permit that had not actually been granted, or when requesting a permit
               * which is not available.
               */
              return;
            }
            super.handleError(response, statusCode);
          }
        });

    return template;
  }

  /**
   * Have this client register the configured throttling config with the Throttling REST service. It
   * will also register itself and store the client-ID for subsequent calls.
   */
  private boolean register() {
    if (this.isRegistered()) {
      return true;
    }
    try {
      this.registerThrottlingConfig();
      this.registerClient();
    } catch (final Exception e) {
      LOGGER.error("Exception occurred while register client", e);
      return false;
    }
    return true;
  }

  private boolean isRegistered() {
    return this.clientId != null && this.throttlingConfig.getId() != null;
  }

  /*
   * registerThrottlingConfig should be synchronized, to make sure there is only
   * one thread registering the throttling config
   */
  private synchronized void registerThrottlingConfig() {
    if (this.throttlingConfig.getId() != null) {
      return;
    }
    final Short throttlingConfigId =
        this.restTemplate.postForObject("/throttling-configs", this.throttlingConfig, Short.class);

    if (throttlingConfigId == null) {
      throw new IllegalStateException(
          "No throttling config ID available after registration of " + this.throttlingConfig);
    }

    this.throttlingConfig.setId(throttlingConfigId);

    LOGGER.info("Registered {}", this.throttlingConfig);
  }

  /*
   * registerClient should be synchronized, to make sure there is only
   * one thread registering the client
   */
  private synchronized void registerClient() {
    if (this.clientId != null) {
      return;
    }

    this.clientId = this.restTemplate.postForObject("/clients", null, Integer.class);

    if (this.clientId == null) {
      throw new IllegalStateException("No client ID available after registration of client");
    }

    LOGGER.info("Registered throttling client with ID: {}", this.clientId);
  }

  /** Lets the Throttling REST service know this client is going away. */
  public void unregister() {
    if (this.clientId == null) {
      LOGGER.info("ThrottlingClient does not have a registered clientId, so skip unregistration");
      return;
    }

    final ResponseEntity<Void> responseEntity =
        this.restTemplate.exchange(
            "/clients/{clientId}", HttpMethod.DELETE, null, Void.class, this.clientId);

    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
      throw new IllegalStateException(
          "No HTTP success response on unregistration of " + this.clientId);
    }

    LOGGER.info("Unregistered clientId {}", this.clientId);
    this.clientId = null;
  }

  /**
   * Requests a permit for a network segment identified by {@code baseTransceiverStationId} and
   * {@code cellId} or for the entire network if any of the IDs is {@code null}.
   *
   * <p>The return value contains a request ID that is unique to this client. This permit, with at
   * least the request ID is required to release the permit when finished.
   *
   * @param baseTransceiverStationId ID of the BTS
   * @param cellId Cell ID within the BTS
   * @param priority Priority of the request
   * @return a permit granting access to a network or network segment
   * @throws ThrottlingPermitDeniedException if a permit is not granted
   */
  public Permit requestPermitUsingNetworkSegmentIfIdsAreAvailable(
      final Integer baseTransceiverStationId, final Integer cellId, final Integer priority) {

    if (baseTransceiverStationId != null && cellId != null) {
      return this.requestPermit(baseTransceiverStationId, cellId, priority)
          .orElseThrow(
              () ->
                  new ThrottlingPermitDeniedException(
                      this.throttlingConfig.getName(), baseTransceiverStationId, cellId, priority));
    }

    return this.requestPermit(priority)
        .orElseThrow(
            () -> new ThrottlingPermitDeniedException(this.throttlingConfig.getName(), priority));
  }

  /**
   * Requests a permit for the entire network. All calls to this method share a single throttling
   * limit.
   *
   * <p>The return value contains a request ID that is unique to this client. This permit, with the
   * request ID is required to release the permit when finished.
   *
   * @return a permit granting access to the network, containing a locally (this client) unique
   *     request ID, or an empty response if no permit was available.
   */
  public Optional<Permit> requestPermit(final int priority) {
    final int requestId = this.requestIdCounter.incrementAndGet();

    LOGGER.debug(
        "Requesting permit using requestId {} with priority {} for clientId {} on {}",
        requestId,
        priority,
        this.clientId,
        this.throttlingConfig);

    final Integer numberOfGrantedPermits = this.numberOfGrantedPermits(requestId, priority);

    if (numberOfGrantedPermits == null) {
      this.discardPermitLogExceptionOnFailure(requestId);
      return Optional.empty();
    }

    /*
     * Set created at for new permits to now, which may be a slightly different instant than the one
     * with the throttling service. This is just an indication, and should not be depended on to
     * match the value when inspecting the permit at the throttling service.
     */
    return numberOfGrantedPermits > 0
        ? Optional.of(
            new Permit(
                this.throttlingConfig.getId(), this.clientId, requestId, null, null, Instant.now()))
        : Optional.empty();
  }

  /**
   * Requests a permit for a network segment identified by {@code baseTransceiverStationId} and
   * {@code cellId}
   *
   * <p>The return value contains a request ID that is unique to this client. This permit, with the
   * request, BTS and cell ID is required to release the permit when finished.
   *
   * @param baseTransceiverStationId BTS for which a permit is requested
   * @param cellId Cell of the BTS for which a permit is requested
   * @param priority Priority of request
   * @return a permit granting access to the given network and network segment, containing a locally
   *     (this client) unique request ID, or an empty response if no permit was available.
   */
  public Optional<Permit> requestPermit(
      final int baseTransceiverStationId, final int cellId, final int priority) {
    final int requestId = this.requestIdCounter.incrementAndGet();

    LOGGER.debug(
        "Requesting permit for network segment ({}, {}) using requestId {} with priority {} for clientId {} on {}",
        baseTransceiverStationId,
        cellId,
        requestId,
        priority,
        this.clientId,
        this.throttlingConfig);

    final Integer numberOfGrantedPermits =
        this.numberOfGrantedPermits(requestId, baseTransceiverStationId, cellId, priority);

    if (numberOfGrantedPermits == null) {
      this.discardPermitLogExceptionOnFailure(requestId);
      return Optional.empty();
    }

    /*
     * Set created at for new permits to now, which may be a slightly different instant than the one
     * with the throttling service. This is just an indication, and should not be depended on to
     * match the value when inspecting the permit at the throttling service.
     */
    return numberOfGrantedPermits > 0
        ? Optional.of(
            new Permit(
                this.throttlingConfig.getId(),
                this.clientId,
                requestId,
                baseTransceiverStationId,
                cellId,
                Instant.now()))
        : Optional.empty();
  }

  private Integer numberOfGrantedPermits(final int requestId, final int priority) {
    if (!this.register()) {
      LOGGER.error("Client is not registered when requesting permit using requestId {}", requestId);
      return null;
    }

    try {
      return this.restTemplate.postForObject(
          "/permits/{throttlingConfigId}/{clientId}?priority={priority}",
          requestId,
          Integer.class,
          this.throttlingConfig.getId(),
          this.clientId,
          priority);
    } catch (final Exception e) {
      LOGGER.error(
          "Unexpected exception requesting permit using requestId {} for {} on {}",
          requestId,
          this.clientId,
          this.throttlingConfig);
      return null;
    }
  }

  private Integer numberOfGrantedPermits(
      final int requestId,
      final int baseTransceiverStationId,
      final int cellId,
      final int priority) {
    if (!this.register()) {
      LOGGER.error(
          "Client is not registered when requesting permit for network segment ({}, {}) using requestId {} with priority {}",
          baseTransceiverStationId,
          cellId,
          requestId,
          priority);
      return null;
    }

    try {
      return this.restTemplate.postForObject(
          "/permits/{throttlingConfigId}/{clientId}/{baseTransceiverStationId}/{cellId}?priority={priority}",
          requestId,
          Integer.class,
          this.throttlingConfig.getId(),
          this.clientId,
          baseTransceiverStationId,
          cellId,
          priority);
    } catch (final Exception e) {
      LOGGER.error(
          "Unexpected exception requesting permit for network segment ({}, {}) using requestId {} with priority {} for {} on {}",
          baseTransceiverStationId,
          cellId,
          requestId,
          priority,
          this.clientId,
          this.throttlingConfig,
          e);
      return null;
    }
  }

  /**
   * Releases the given permit, freeing up the network for the next request.
   *
   * @param permit the permit as it was returned by {@code requestPermit} (or variants thereof)
   * @return true if it was successfully released, false otherwise
   */
  public boolean releasePermit(final Permit permit) {

    final boolean released;
    if (permit.getBaseTransceiverStationId() == null || permit.getCellId() == null) {
      released = this.releasePermit(permit.getRequestId());
    } else {
      released =
          this.releasePermit(
              permit.getRequestId(), permit.getBaseTransceiverStationId(), permit.getCellId());
    }
    return released;
  }

  private boolean releasePermit(final Integer requestId) {
    if (!this.register()) {
      LOGGER.error("Client is not registered when releasing permit using requestId {}", requestId);
      return false;
    }
    final ResponseEntity<Void> releaseResponse;
    try {
      releaseResponse =
          this.restTemplate.exchange(
              "/permits/{throttlingConfigId}/{clientId}",
              HttpMethod.DELETE,
              new HttpEntity<>(requestId),
              Void.class,
              this.throttlingConfig.getId(),
              this.clientId);
    } catch (final Exception e) {
      LOGGER.warn(
          "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {} - got unexpected exception: {}",
          this.throttlingConfig.getId(),
          this.clientId,
          requestId,
          e.getMessage());
      return false;
    }
    switch (releaseResponse.getStatusCode()) {
      case NOT_FOUND:
        LOGGER.warn(
            "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {}, because the permit has not been granted",
            this.throttlingConfig.getId(),
            this.clientId,
            requestId);
        return false;
      case OK:
        LOGGER.debug(
            "Released permit with throttlingConfigId {}, clientId {}, requestId {}",
            this.throttlingConfig.getId(),
            this.clientId,
            requestId);
        return true;
      default:
        LOGGER.warn(
            "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {} - got unexpected response status {}",
            this.throttlingConfig.getId(),
            this.clientId,
            requestId,
            releaseResponse.getStatusCode());
        return false;
    }
  }

  private boolean releasePermit(
      final Integer requestId, final int baseTransceiverStationId, final int cellId) {
    if (!this.register()) {
      LOGGER.error(
          "Client is not registered when releasing permit for network segment ({}, {}) using requestId {}",
          baseTransceiverStationId,
          cellId,
          requestId);
      return false;
    }

    final ResponseEntity<Void> releaseResponse;
    try {
      releaseResponse =
          this.restTemplate.exchange(
              "/permits/{throttlingConfigId}/{clientId}/{baseTransceiverStationId}/{cellId}",
              HttpMethod.DELETE,
              new HttpEntity<>(requestId),
              Void.class,
              this.throttlingConfig.getId(),
              this.clientId,
              baseTransceiverStationId,
              cellId);
    } catch (final Exception e) {
      LOGGER.warn(
          "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {}, baseTransceiverStationId {}, cellId {} - got unexpected exception: {}",
          this.throttlingConfig.getId(),
          this.clientId,
          requestId,
          baseTransceiverStationId,
          cellId,
          e.getMessage());
      return false;
    }
    switch (releaseResponse.getStatusCode()) {
      case NOT_FOUND:
        LOGGER.warn(
            "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {}, baseTransceiverStationId {}, cellId {}, because the permit has not been granted",
            this.throttlingConfig.getId(),
            this.clientId,
            requestId,
            baseTransceiverStationId,
            cellId);
        return false;
      case OK:
        LOGGER.debug(
            "Released permit with throttlingConfigId {}, clientId {}, requestId {}, baseTransceiverStationId {}, cellId {}",
            this.throttlingConfig.getId(),
            this.clientId,
            requestId,
            baseTransceiverStationId,
            cellId);
        return true;
      default:
        LOGGER.error(
            "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {}, baseTransceiverStationId {}, cellId {} - got unexpected response status {}",
            this.throttlingConfig.getId(),
            this.clientId,
            requestId,
            baseTransceiverStationId,
            cellId,
            releaseResponse.getStatusCode());
        return false;
    }
  }

  private void discardPermitLogExceptionOnFailure(final int requestId) {
    try {
      this.discardPermit(requestId);
    } catch (final Exception e) {
      LOGGER.error("Failed to discard permit with requestId {}", requestId, e);
    }
  }

  /**
   * Discard the given request, freeing up the network for the next request. This can be used to
   * free a possibly granted permit, which wasn't received by the client.
   *
   * @param requestId ID of the discarded request
   */
  public void discardPermit(final int requestId) {
    if (!this.register()) {
      LOGGER.error("Client is not registered when discarding permit using requestId {}", requestId);
      return;
    }

    final ResponseEntity<Void> discardResponse =
        this.restTemplate.exchange(
            "/permits/discard/{clientId}/{requestId}",
            HttpMethod.DELETE,
            null,
            Void.class,
            this.clientId,
            requestId);

    switch (discardResponse.getStatusCode()) {
      case NOT_FOUND:
        LOGGER.info(
            "Discarded permit for {} with requestId {} - no permit had been granted",
            this.clientId,
            requestId);
        break;
      case OK:
        LOGGER.info(
            "Discarded permit for {} with requestId {} - granted permit has been released",
            this.clientId,
            requestId);
        break;
      default:
        LOGGER.error(
            "Discarded permit for {} with requestId {} - got unexpected response status {}",
            this.clientId,
            requestId,
            discardResponse.getStatusCode());
        break;
    }
  }

  /**
   * Gets the throttling configuration used by this client
   *
   * @return throttling configuration
   */
  public ThrottlingConfig getThrottlingConfig() {
    return this.throttlingConfig;
  }

  /**
   * Gets the client ID as returned by the Throttling REST service.
   *
   * @return Client ID
   */
  public Integer getClientId() {
    return this.clientId;
  }
}
