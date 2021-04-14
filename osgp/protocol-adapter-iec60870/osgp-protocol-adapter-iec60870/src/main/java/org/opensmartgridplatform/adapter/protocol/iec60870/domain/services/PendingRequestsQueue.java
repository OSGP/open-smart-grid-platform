/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PendingRequestsQueue {

  private final Map<String, LinkedList<String>> correlationUidQueuePerDevice = new HashMap<>();

  public void enqueue(final String deviceIdentification, final String correlationUid) {
    this.correlationUidQueuePerDevice
        .computeIfAbsent(deviceIdentification, key -> new LinkedList<>())
        .add(correlationUid);
  }

  public Optional<String> dequeue(final String deviceIdentification) {
    final String correlationUid =
        this.correlationUidQueuePerDevice
            .getOrDefault(deviceIdentification, new LinkedList<>())
            .poll();
    return Optional.ofNullable(correlationUid);
  }

  public void remove(final String deviceIdentification, final String correlationUid) {
    this.correlationUidQueuePerDevice
        .computeIfAbsent(deviceIdentification, key -> new LinkedList<>())
        .remove(correlationUid);
  }
}
