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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import java.util.concurrent.atomic.AtomicInteger;
import org.opensmartgridplatform.throttling.api.ThrottlingConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class NetworkUser {

  private static final AtomicInteger clientNumber = new AtomicInteger(0);

  private final AtomicInteger requestCounter = new AtomicInteger(0);

  private final String throttlingIdentity;

  private final int initialMaxConcurrency;
  private short throttlingConfigId = -1;
  private final String clientIdentity = "client-" + clientNumber.incrementAndGet();
  private int clientId = -1;

  private final FakeConcurrencyRestrictedNetwork network;
  private final RestTemplate restTemplate;

  private final NetworkTaskQueue networkTaskQueue;

  public NetworkUser(
      final String throttlingIdentity,
      final int initialMaxConcurrency,
      final FakeConcurrencyRestrictedNetwork network,
      final RestTemplate restTemplate,
      final NetworkTaskQueue networkTaskQueue) {

    this.throttlingIdentity = throttlingIdentity;
    this.initialMaxConcurrency = initialMaxConcurrency;
    this.network = network;
    this.restTemplate = restTemplate;
    this.networkTaskQueue = networkTaskQueue;
  }

  public void processTasksInQueue() {
    this.registerThrottlingConfig();
    this.registerThrottlingClient();

    new Thread(() -> this.doWork()).start();
  }

  public void doWork() {
    if (this.throttlingConfigId < 1 || this.clientId < 1) {
      return;
    }
    while (this.networkTaskQueue.remainingNetworkTasks() > 0) {
      this.networkTaskQueue.poll().ifPresent(this::executeNetworkTask);
    }

    this.unregisterThrottlingClient();
  }

  public void executeNetworkTask(final NetworkTask networkTask) {
    final Runnable actionIfPermitGranted = () -> this.executeOnNetwork(networkTask);
    final Runnable actionIfPermitDenied = () -> this.networkTaskQueue.add(networkTask);
    try {
      this.executeThrottled(networkTask, actionIfPermitGranted, actionIfPermitDenied);
    } catch (final Exception e) {
      networkTask.throwable = e;
    }
  }

  private void executeOnNetwork(final NetworkTask networkTask) {
    final int baseTransceiverStationId = networkTask.baseTransceiverStationId;
    final int cellId = networkTask.cellId;
    this.network.openConnection(baseTransceiverStationId, cellId);
    try {
      networkTask.execute();
    } finally {
      this.network.closeConnection(baseTransceiverStationId, cellId);
    }
  }

  private void executeThrottled(
      final NetworkTask networkTask,
      final Runnable actionIfPermitGranted,
      final Runnable actionIfPermitDenied) {

    final int baseTransceiverStationId = networkTask.baseTransceiverStationId;
    final int cellId = networkTask.cellId;
    final int clientRequestId = this.requestCounter.incrementAndGet();

    final boolean granted = this.requestPermit(baseTransceiverStationId, cellId, clientRequestId);

    if (!granted) {
      actionIfPermitDenied.run();
      return;
    }

    try {
      actionIfPermitGranted.run();
    } finally {
      final boolean released =
          this.releasePermit(baseTransceiverStationId, cellId, clientRequestId);
      if (!released) {
        throw new IllegalStateException("Release permit was not successfull");
      }
    }
  }

  private void registerThrottlingConfig() {
    final ResponseEntity<JsonNode> throttlingConfigResponse =
        this.restTemplate.postForEntity(
            "/throttling-configs",
            new ThrottlingConfig(this.throttlingIdentity, this.initialMaxConcurrency),
            JsonNode.class);

    if (throttlingConfigResponse.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL) {
      throw new IllegalStateException(
          "Could not register throttling config " + this.throttlingIdentity);
    }
    if (throttlingConfigResponse.getBody() instanceof NumericNode) {
      this.throttlingConfigId = ((NumericNode) throttlingConfigResponse.getBody()).shortValue();
    } else {
      throw new IllegalStateException(
          "Throttling config response did not have a numeric node: " + throttlingConfigResponse);
    }
  }

  private void registerThrottlingClient() {
    final ResponseEntity<JsonNode> clientRegistrationResponse =
        this.restTemplate.postForEntity("/clients", null, JsonNode.class);

    if (clientRegistrationResponse.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL) {
      throw new IllegalStateException("Could not register client " + this.clientIdentity);
    }
    if (clientRegistrationResponse.getBody() instanceof NumericNode) {
      this.clientId = ((NumericNode) clientRegistrationResponse.getBody()).intValue();
    } else {
      throw new IllegalStateException(
          "Client registration response did not have a numeric node: "
              + clientRegistrationResponse);
    }
  }

  private void unregisterThrottlingClient() {
    final ResponseEntity<JsonNode> clientUnregistrationResponse =
        this.restTemplate.exchange(
            "/clients/{clientId}", HttpMethod.DELETE, null, JsonNode.class, this.clientId);

    if (clientUnregistrationResponse.getStatusCode() != HttpStatus.ACCEPTED) {
      throw new IllegalStateException(
          "Could not unregister client "
              + this.clientIdentity
              + " - "
              + clientUnregistrationResponse);
    }
  }

  private boolean requestPermit(
      final int baseTransceiverStationId, final int cellId, final int requestId) {

    final ResponseEntity<JsonNode> permitRequestResponse =
        this.restTemplate.postForEntity(
            "/permits/{throttlingConfigId}/{clientId}/{baseTransceiverStationId}/{cellId}",
            requestId,
            JsonNode.class,
            this.throttlingConfigId,
            this.clientId,
            baseTransceiverStationId,
            cellId);

    return permitRequestResponse.getStatusCode().is2xxSuccessful()
        && permitRequestResponse.getBody() instanceof NumericNode
        && ((NumericNode) permitRequestResponse.getBody()).intValue() == 1;
  }

  private boolean releasePermit(
      final int baseTransceiverStationId, final int cellId, final int requestId) {

    final ResponseEntity<JsonNode> releasePermitResponse =
        this.restTemplate.exchange(
            "/permits/{throttlingConfigId}/{clientId}/{baseTransceiverStationId}/{cellId}",
            HttpMethod.DELETE,
            new HttpEntity<>(requestId),
            JsonNode.class,
            this.throttlingConfigId,
            this.clientId,
            baseTransceiverStationId,
            cellId);

    return releasePermitResponse.getStatusCode().is2xxSuccessful();
  }
}
