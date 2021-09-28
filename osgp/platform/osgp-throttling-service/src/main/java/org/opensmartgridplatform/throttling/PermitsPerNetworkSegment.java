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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.opensmartgridplatform.throttling.repositories.PermitRepository.PermitCountByNetworkSegment;

public class PermitsPerNetworkSegment {

  private static final ConcurrentMap<Integer, AtomicInteger> NO_PERMITS_FOR_STATION =
      new ConcurrentHashMap<>();
  private static final AtomicInteger NO_PERMITS_FOR_CELL = new AtomicInteger(0);

  private final ConcurrentMap<Integer, ConcurrentMap<Integer, AtomicInteger>> permitsPerSegment =
      new ConcurrentHashMap<>();

  private final PermitRepository permitRepository;

  public PermitsPerNetworkSegment(final PermitRepository permitRepository) {
    this.permitRepository = permitRepository;
  }

  public void initialize(final short throttlingConfigId) {
    final List<PermitCountByNetworkSegment> permitCountByNetworkSegment =
        this.permitRepository.permitsByNetworkSegment(throttlingConfigId);
    permitCountByNetworkSegment.forEach(
        countByNetworkSegment ->
            this.permitsPerSegment
                .computeIfAbsent(
                    countByNetworkSegment.getBaseTransceiverStationId(),
                    key -> new ConcurrentHashMap<>())
                .put(
                    countByNetworkSegment.getCellId(),
                    new AtomicInteger(countByNetworkSegment.getNumberOfPermits())));
  }

  public Map<Integer, Map<Integer, Integer>> permitsPerNetworkSegment() {
    return this.permitsPerSegment.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, // baseTransceiverStationId
                btsIdWithCountByCellId ->
                    btsIdWithCountByCellId.getValue().entrySet().stream()
                        .collect(
                            Collectors.toMap(
                                Map.Entry::getKey, // cellId
                                cellIdWithCount -> cellIdWithCount.getValue().get(),
                                (o1, o2) -> o1,
                                TreeMap::new)),
                (o1, o2) -> o1,
                TreeMap::new));
  }

  public boolean requestPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId,
      final int maxConcurrency) {

    final AtomicInteger permitCounter =
        this.permitsPerSegment
            .computeIfAbsent(baseTransceiverStationId, key -> new ConcurrentHashMap<>())
            .computeIfAbsent(cellId, key -> new AtomicInteger(0));

    final int numberOfPermitsIfGranted = permitCounter.incrementAndGet();
    if (numberOfPermitsIfGranted > maxConcurrency) {
      permitCounter.decrementAndGet();
      return false;
    }

    return this.permitRepository.grantPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);
  }

  public boolean releasePermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    final AtomicInteger permitCounter =
        this.permitsPerSegment
            .getOrDefault(baseTransceiverStationId, NO_PERMITS_FOR_STATION)
            .getOrDefault(cellId, NO_PERMITS_FOR_CELL);

    final int numberOfPermitsIfReleased = permitCounter.decrementAndGet();
    if (numberOfPermitsIfReleased < 0) {
      permitCounter.incrementAndGet();
      return false;
    }

    final int numberOfReleasedPermits =
        this.permitRepository.releasePermit(
            throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);

    return numberOfReleasedPermits == 1;
  }

  @Override
  public String toString() {
    return String.format(
        "PermitsPerNetworkSegment[covering %d base transceiver stations]",
        this.permitsPerSegment.size());
  }
}
