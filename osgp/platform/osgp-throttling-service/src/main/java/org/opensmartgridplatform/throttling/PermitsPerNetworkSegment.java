// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.opensmartgridplatform.throttling.repositories.PermitRepository.PermitCountByNetworkSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermitsPerNetworkSegment {
  private static final Logger LOGGER = LoggerFactory.getLogger(PermitsPerNetworkSegment.class);

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
    final ConcurrentMap<Integer, ConcurrentMap<Integer, AtomicInteger>> permitsPerSegmentFromDb =
        new ConcurrentHashMap<>();
    this.permitRepository
        .permitsByNetworkSegment(throttlingConfigId)
        .forEach(
            countByNetworkSegment -> {
              this.logIfUpdated(countByNetworkSegment);

              permitsPerSegmentFromDb
                  .computeIfAbsent(
                      countByNetworkSegment.getBaseTransceiverStationId(),
                      key -> new ConcurrentHashMap<>())
                  .put(
                      countByNetworkSegment.getCellId(),
                      new AtomicInteger(countByNetworkSegment.getNumberOfPermits()));
            });
    this.permitsPerSegment.clear();
    this.permitsPerSegment.putAll(permitsPerSegmentFromDb);
  }

  private void logIfUpdated(final PermitCountByNetworkSegment countByNetworkSegment) {
    final int btsId = countByNetworkSegment.getBaseTransceiverStationId();
    final int cellId = countByNetworkSegment.getCellId();
    final int numberOfPermitsInDb = countByNetworkSegment.getNumberOfPermits();

    if (!this.permitsPerSegment.containsKey(btsId)
        || !this.permitsPerSegment.get(btsId).containsKey(cellId)) {
      return;
    }

    final int numberOfPermitsInMemory = this.permitsPerSegment.get(btsId).get(cellId).get();
    if (numberOfPermitsInMemory != numberOfPermitsInDb) {
      LOGGER.info(
          "PermitsPerSegment in memory will be updated from {} to {} for bts/cell ({}, {})",
          numberOfPermitsInMemory,
          numberOfPermitsInDb,
          btsId,
          cellId);
    }
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
      final int priority,
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
