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
    final ResponseEntity<Short> throttlingConfigResponse =
        this.restTemplate.postForEntity(
            "/throttling-configs",
            new ThrottlingConfig(this.throttlingIdentity, this.initialMaxConcurrency),
            Short.class);

    if (throttlingConfigResponse.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL
        || throttlingConfigResponse.getBody() == null) {
      throw new IllegalStateException(
          "Could not register throttling config " + this.throttlingIdentity);
    }
    this.throttlingConfigId = throttlingConfigResponse.getBody();
  }

  private void registerThrottlingClient() {
    final ResponseEntity<Integer> clientRegistrationResponse =
        this.restTemplate.postForEntity("/clients", null, Integer.class);

    if (clientRegistrationResponse.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL
        || clientRegistrationResponse.getBody() == null) {
      throw new IllegalStateException("Could not register client " + this.clientIdentity);
    }
    this.clientId = clientRegistrationResponse.getBody();
  }

  private void unregisterThrottlingClient() {
    final ResponseEntity<Void> clientUnregistrationResponse =
        this.restTemplate.exchange(
            "/clients/{clientId}", HttpMethod.DELETE, null, Void.class, this.clientId);

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

    final ResponseEntity<Integer> permitRequestResponse =
        this.restTemplate.postForEntity(
            "/permits/{throttlingConfigId}/{clientId}/{baseTransceiverStationId}/{cellId}",
            requestId,
            Integer.class,
            this.throttlingConfigId,
            this.clientId,
            baseTransceiverStationId,
            cellId);

    return permitRequestResponse.getStatusCode().is2xxSuccessful()
        && permitRequestResponse.getBody() != null
        && permitRequestResponse.getBody() == 1;
  }

  private boolean releasePermit(
      final int baseTransceiverStationId, final int cellId, final int requestId) {

    final ResponseEntity<Void> releasePermitResponse =
        this.restTemplate.exchange(
            "/permits/{throttlingConfigId}/{clientId}/{baseTransceiverStationId}/{cellId}",
            HttpMethod.DELETE,
            new HttpEntity<>(requestId),
            Void.class,
            this.throttlingConfigId,
            this.clientId,
            baseTransceiverStationId,
            cellId);

    return releasePermitResponse.getStatusCode().is2xxSuccessful();
  }
}
