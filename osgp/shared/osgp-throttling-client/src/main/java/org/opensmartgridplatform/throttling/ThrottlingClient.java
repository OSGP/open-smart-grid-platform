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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.opensmartgridplatform.throttling.api.Client;
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

  private final AtomicInteger requestIdCounter = new AtomicInteger(0);
  private final Client client;
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

    this(new Client(UUID.randomUUID().toString()), throttlingConfig, throttlingServiceUrl, timeout);
  }

  public ThrottlingClient(
      final Client client,
      final ThrottlingConfig throttlingConfig,
      final String throttlingServiceUrl,
      final Duration timeout) {

    this.client = Objects.requireNonNull(client, "client must not be null");
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
    final Integer clientId =
        this.webClient
            .post()
            .uri("/clients")
            .bodyValue(this.client)
            .retrieve()
            .bodyToMono(Integer.class)
            .block(this.timeout);

    if (clientId == null) {
      throw new IllegalStateException(
          "No client ID available after registration of " + this.client);
    }

    /*
     * Set registered at to now, which may be a slightly different instant than the one with the
     * throttling service. This is just an indication, and should not be depended on to match the
     * value when retrieving the client information from the throttling service API.
     */
    this.client.setRegisteredAt(Instant.now());
    this.client.setId(clientId);

    LOGGER.info("Registered {}", this.client);
  }

  public void unregister() {
    final Long unregisteredAtMillis =
        this.webClient
            .delete()
            .uri("/clients/{clientId}", this.client.getId())
            .retrieve()
            .bodyToMono(Long.class)
            .block(this.timeout);

    if (unregisteredAtMillis == null) {
      throw new IllegalStateException(
          "No unregistered at millis available after unregistration of " + this.client);
    }

    this.client.setUnregisteredAt(Instant.ofEpochMilli(unregisteredAtMillis));

    LOGGER.info("Unregistered {}", this.client);
  }

  public Optional<Permit> requestPermit() {
    final int requestId = this.requestIdCounter.incrementAndGet();

    LOGGER.debug(
        "Requesting permit using requestId {} for {} on {}",
        requestId,
        this.client,
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
                this.throttlingConfig.getId(),
                this.client.getId(),
                requestId,
                null,
                null,
                Instant.now()))
        : Optional.empty();
  }

  public Optional<Permit> requestPermit(final int baseTransceiverStationId, final int cellId) {
    final int requestId = this.requestIdCounter.incrementAndGet();

    LOGGER.debug(
        "Requesting permit for network segment ({}, {}) using requestId {} for {} on {}",
        baseTransceiverStationId,
        cellId,
        requestId,
        this.client,
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
                this.client.getId(),
                requestId,
                baseTransceiverStationId,
                cellId,
                Instant.now()))
        : Optional.empty();
  }

  private Integer numberOfGrantedPermits(final int requestId) {

    try {
      return this.webClient
          .post()
          .uri(
              "/permits/{throttlingConfigId}/{clientId}",
              this.throttlingConfig.getId(),
              this.client.getId())
          .bodyValue(requestId)
          .retrieve()
          .onStatus(status -> HttpStatus.CONFLICT == status, clientResponse -> Mono.empty())
          .bodyToMono(Integer.class)
          .block(this.timeout);
    } catch (final Exception e) {
      LOGGER.error(
          "Unexpected exception requesting permit using requestId {} for {} on {}",
          requestId,
          this.client,
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
              this.client.getId(),
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
          this.client,
          this.throttlingConfig);
      return null;
    }
  }

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
                this.client.getId())
            .bodyValue(requestId)
            .retrieve()
            .onStatus(status -> HttpStatus.NOT_FOUND == status, clientResponse -> Mono.empty())
            .toBodilessEntity()
            .block(this.timeout);

    if (releaseResponse == null) {
      throw new IllegalStateException(
          String.format(
              "No release response available for permit with throttlingConfigId %d, clientId %d, requestId %s",
              this.throttlingConfig.getId(), this.client.getId(), requestId));
    }

    switch (releaseResponse.getStatusCode()) {
      case NOT_FOUND:
        LOGGER.warn(
            "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {}, because the permit has not been granted",
            this.throttlingConfig.getId(),
            this.client.getId(),
            requestId);
        return false;
      case OK:
        LOGGER.debug(
            "Released permit with throttlingConfigId {}, clientId {}, requestId {}",
            this.throttlingConfig.getId(),
            this.client.getId(),
            requestId);
        return true;
      default:
        LOGGER.warn(
            "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {} - got unexpected response status {}",
            this.throttlingConfig.getId(),
            this.client.getId(),
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
                this.client.getId(),
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
              this.client.getId(),
              requestId,
              baseTransceiverStationId,
              cellId));
    }

    switch (releaseResponse.getStatusCode()) {
      case NOT_FOUND:
        LOGGER.warn(
            "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {}, baseTransceiverStationId {}, cellId {}, because the permit has not been granted",
            this.throttlingConfig.getId(),
            this.client.getId(),
            requestId,
            baseTransceiverStationId,
            cellId);
        return false;
      case OK:
        LOGGER.debug(
            "Released permit with throttlingConfigId {}, clientId {}, requestId {}, baseTransceiverStationId {}, cellId {}",
            this.throttlingConfig.getId(),
            this.client.getId(),
            requestId,
            baseTransceiverStationId,
            cellId);
        return true;
      default:
        LOGGER.error(
            "Unable to release permit with throttlingConfigId {}, clientId {}, requestId {}, baseTransceiverStationId {}, cellId {} - got unexpected response status {}",
            this.throttlingConfig.getId(),
            this.client.getId(),
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

  public void discardPermit(final int requestId) {
    final ResponseEntity<Void> discardResponse =
        this.webClient
            .delete()
            .uri("/permits/discard/{clientId}/{requestId}", this.client.getId(), requestId)
            .retrieve()
            .onStatus(status -> HttpStatus.NOT_FOUND == status, clientResponse -> Mono.empty())
            .toBodilessEntity()
            .block(this.timeout);

    if (discardResponse == null) {
      throw new IllegalStateException(
          String.format(
              "No discard response available for requestId %d of %s", requestId, this.client));
    }

    switch (discardResponse.getStatusCode()) {
      case NOT_FOUND:
        LOGGER.info(
            "Discarded permit for {} with requestId {} - no permit had been granted",
            this.client,
            requestId);
        break;
      case OK:
        LOGGER.info(
            "Discarded permit for {} with requestId {} - granted permit has been released",
            this.client,
            requestId);
        break;
      default:
        LOGGER.error(
            "Discarded permit for {} with requestId {} - got unexpected response status {}",
            this.client,
            requestId,
            discardResponse.getStatusCode());
        break;
    }
  }
}
