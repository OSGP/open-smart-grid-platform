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

import com.fasterxml.jackson.databind.node.NumericNode;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.opensmartgridplatform.throttling.api.Permit;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** Client for distributed segmented network max concurrency throttling. */
public class ThrottlingClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThrottlingClient.class);

  private Integer clientId;
  private final AtomicInteger requestIdCounter = new AtomicInteger(0);
  private final ThrottlingConfig throttlingConfig;
  private final WebClient webClient;
  private final Duration timeout;

  public ThrottlingClient(
      final ThrottlingConfig throttlingConfig, final String throttlingServiceUrl) {

    this(throttlingConfig, throttlingServiceUrl, Duration.ofSeconds(30));
  }

  public ThrottlingClient(
      final ThrottlingConfig throttlingConfig,
      final String throttlingServiceUrl,
      final Duration timeout) {

    this.throttlingConfig =
        Objects.requireNonNull(throttlingConfig, "throttlingConfig must not be null");
    this.webClient =
        WebClient.builder()
            .baseUrl(
                Objects.requireNonNull(
                    throttlingServiceUrl, "throttlingServiceUrl must not be null"))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    this.timeout = Objects.requireNonNull(timeout, "timeout must not be null");
  }

  /**
   * Have this client register the configured throttling config with the Throttling REST service. It
   * will also register itself and store the client-ID for subsequent calls.
   */
  public void register() {
    this.registerThrottlingConfig();
    this.registerClient();
  }

  private void registerThrottlingConfig() {
    final NumericNode throttlingConfigIdNode =
        this.webClient
            .post()
            .uri("/throttling-configs")
            .bodyValue(this.throttlingConfig)
            .retrieve()
            .bodyToMono(NumericNode.class)
            .block(this.timeout);

    if (throttlingConfigIdNode == null) {
      throw new IllegalStateException(
          "No throttling config ID available after registration of " + this.throttlingConfig);
    }

    this.throttlingConfig.setId(throttlingConfigIdNode.shortValue());

    LOGGER.info("Registered {}", this.throttlingConfig);
  }

  private void registerClient() {
    this.clientId =
        this.webClient
            .post()
            .uri("/clients")
            .retrieve()
            .bodyToMono(Integer.class)
            .block(this.timeout);
    if (this.clientId == null) {
      throw new IllegalStateException("No client ID available after registration of client");
    }

    LOGGER.info("Registered {}", this.clientId);
  }

  /** Lets the Throttling REST service know this client is going away. */
  public void unregister() {
    final ResponseEntity<Void> responseEntity =
        this.webClient
            .delete()
            .uri("/clients/{clientId}", this.clientId)
            .retrieve()
            .toBodilessEntity()
            .block(this.timeout);

    if (responseEntity == null || !responseEntity.getStatusCode().is2xxSuccessful()) {
      throw new IllegalStateException(
          "No HTTP success response on unregistration of " + this.clientId);
    }

    LOGGER.info("Unregistered {}", this.clientId);
    this.clientId = null;
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
  public Optional<Permit> requestPermit() {
    final int requestId = this.requestIdCounter.incrementAndGet();

    LOGGER.debug(
        "Requesting permit using requestId {} for {} on {}",
        requestId,
        this.clientId,
        this.throttlingConfig);

    final Integer numberOfGrantedPermits = this.numberOfGrantedPermits(requestId);

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
   * @return a permit granting access to the given network and network segment, containing a locally
   *     (this client) unique request ID, or an empty response if no permit was available.
   */
  public Optional<Permit> requestPermit(final int baseTransceiverStationId, final int cellId) {
    final int requestId = this.requestIdCounter.incrementAndGet();

    LOGGER.debug(
        "Requesting permit for network segment ({}, {}) using requestId {} for {} on {}",
        baseTransceiverStationId,
        cellId,
        requestId,
        this.clientId,
        this.throttlingConfig);

    final Integer numberOfGrantedPermits =
        this.numberOfGrantedPermits(requestId, baseTransceiverStationId, cellId);

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

  /**
   * Requests a permit for a network segment identified by {@code baseTransceiverStationId} and
   * {@code cellId} or for the entire network if any of the IDs is {@code null}.
   *
   * <p>The return value contains a request ID that is unique to this client. This permit, with at
   * least the request ID is required to release the permit when finished.
   *
   * @param baseTransceiverStationId ID of the BTS (or 'network')
   * @param cellId Cell ID within the BTS (or 'segment')
   * @return a permit granting access to a network or network segment
   * @throws ThrottlingPermitDeniedException if a permit is not granted
   */
  public Permit requestPermitUsingNetworkSegmentIfIdsAreAvailable(
      final Integer baseTransceiverStationId, final Integer cellId) {

    if (baseTransceiverStationId != null && cellId != null) {
      return this.requestPermit(baseTransceiverStationId, cellId)
          .orElseThrow(
              () ->
                  new ThrottlingPermitDeniedException(
                      this.throttlingConfig.getName(), baseTransceiverStationId, cellId));
    }

    return this.requestPermit()
        .orElseThrow(() -> new ThrottlingPermitDeniedException(this.throttlingConfig.getName()));
  }

  private Integer numberOfGrantedPermits(final int requestId) {

    try {
      return this.webClient
          .post()
          .uri(
              "/permits/{throttlingConfigId}/{clientId}",
              this.throttlingConfig.getId(),
              this.clientId)
          .bodyValue(requestId)
          .retrieve()
          .onStatus(status -> HttpStatus.CONFLICT == status, clientResponse -> Mono.empty())
          .bodyToMono(Integer.class)
          .block(this.timeout);
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
      final int requestId, final int baseTransceiverStationId, final int cellId) {

    try {
      return this.webClient
          .post()
          .uri(
              "/permits/{throttlingConfigId}/{clientId}/{baseTransceiverStationId}/{cellId}",
              this.throttlingConfig.getId(),
              this.clientId,
              baseTransceiverStationId,
              cellId)
          .bodyValue(requestId)
          .retrieve()
          .onStatus(status -> HttpStatus.CONFLICT == status, clientResponse -> Mono.empty())
          .bodyToMono(Integer.class)
          .block(this.timeout);
    } catch (final Exception e) {
      LOGGER.error(
          "Unexpected exception requesting permit for network segment ({}, {}) using requestId {} for {} on {}",
          baseTransceiverStationId,
          cellId,
          requestId,
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
    final ResponseEntity<Void> releaseResponse =
        this.webClient
            .method(HttpMethod.DELETE)
            .uri(
                "/permits/{throttlingConfigId}/{clientId}",
                this.throttlingConfig.getId(),
                this.clientId)
            .bodyValue(requestId)
            .retrieve()
            .onStatus(status -> HttpStatus.NOT_FOUND == status, clientResponse -> Mono.empty())
            .toBodilessEntity()
            .block(this.timeout);

    if (releaseResponse == null) {
      throw new IllegalStateException(
          String.format(
              "No release response available for permit with throttlingConfigId %d, clientId %d, requestId %s",
              this.throttlingConfig.getId(), this.clientId, requestId));
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

    final ResponseEntity<Void> releaseResponse =
        this.webClient
            .method(HttpMethod.DELETE)
            .uri(
                "/permits/{throttlingConfigId}/{clientId}/{baseTransceiverStationId}/{cellId}",
                this.throttlingConfig.getId(),
                this.clientId,
                baseTransceiverStationId,
                cellId)
            .bodyValue(requestId)
            .retrieve()
            .onStatus(status -> HttpStatus.NOT_FOUND == status, clientResponse -> Mono.empty())
            .toBodilessEntity()
            .block(this.timeout);

    if (releaseResponse == null) {
      throw new IllegalStateException(
          String.format(
              "No release response available for permit with throttlingConfigId %d, clientId %d, requestId %s, baseTransceiverStationId %s, cellId %s",
              this.throttlingConfig.getId(),
              this.clientId,
              requestId,
              baseTransceiverStationId,
              cellId));
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
   * Discard the given request, freeing up the network for the next request.
   *
   * @param requestId ID of the discarded request
   */
  public void discardPermit(final int requestId) {
    final ResponseEntity<Void> discardResponse =
        this.webClient
            .delete()
            .uri("/permits/discard/{clientId}/{requestId}", this.clientId, requestId)
            .retrieve()
            .onStatus(status -> HttpStatus.NOT_FOUND == status, clientResponse -> Mono.empty())
            .toBodilessEntity()
            .block(this.timeout);

    if (discardResponse == null) {
      throw new IllegalStateException(
          String.format(
              "No discard response available for requestId %d of %s", requestId, this.clientId));
    }

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
