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
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.opensmartgridplatform.throttling.model.ThrottlingSettings;
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.opensmartgridplatform.throttling.repositories.PermitRepository.PermitCountByNetworkSegment;
import org.opensmartgridplatform.throttling.service.PermitReleasedNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermitsPerNetworkSegment {
  private static final Logger LOGGER = LoggerFactory.getLogger(PermitsPerNetworkSegment.class);

  private static final int WAIT_TIME = 1000;

  private final ConcurrentMap<Integer, ConcurrentMap<Integer, AtomicInteger>> permitsPerSegment =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<Integer, ConcurrentMap<Integer, NewConnectionRequestThrottler>>
      newConnectionRequestThrottlerPerSegment = new ConcurrentHashMap<>();

  private final PermitRepository permitRepository;
  private final PermitReleasedNotifier permitReleasedNotifier;
  private final boolean highPrioPoolEnabled;
  private final int maxWaitForHighPrioInMs;

  public PermitsPerNetworkSegment(
      final PermitRepository permitRepository,
      final PermitReleasedNotifier permitReleasedNotifier,
      final boolean highPrioPoolEnabled,
      final int maxWaitForHighPrioInMs) {
    this.permitRepository = permitRepository;
    this.permitReleasedNotifier = permitReleasedNotifier;
    this.highPrioPoolEnabled = highPrioPoolEnabled;
    this.maxWaitForHighPrioInMs = maxWaitForHighPrioInMs;
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

  public ConcurrentMap<Integer, ConcurrentMap<Integer, NewConnectionRequestThrottler>>
      newConnectionRequestThrottlerPerSegment() {
    return this.newConnectionRequestThrottlerPerSegment;
  }

  public boolean requestPermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId,
      final int priority,
      final ThrottlingSettings throttlingSettings) {
    if (!this.isNewConnectionRequestAllowed(
        baseTransceiverStationId, cellId, priority, throttlingSettings)) {
      return false;
    }

    if (!this.isPermitAvailable(
        baseTransceiverStationId, cellId, priority, throttlingSettings.getMaxConcurrency())) {
      return false;
    }

    return this.permitRepository.grantPermit(
        throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);
  }

  private boolean isNewConnectionRequestAllowed(
      final int baseTransceiverStationId,
      final int cellId,
      final int priority,
      final ThrottlingSettings throttlingSettings) {
    if (throttlingSettings.getMaxNewConnections() < 0) {
      return true;
    } else if (throttlingSettings.getMaxNewConnections() == 0) {
      return false;
    }
    final NewConnectionRequestThrottler newConnectionRequestThrottler =
        this.newConnectionRequestThrottlerPerSegment
            .computeIfAbsent(baseTransceiverStationId, key -> new ConcurrentHashMap<>())
            .computeIfAbsent(
                cellId,
                key ->
                    new NewConnectionRequestThrottler(
                        throttlingSettings.getMaxNewConnections(),
                        throttlingSettings.getMaxNewConnectionsResetTimeInMs(),
                        throttlingSettings.getMaxNewConnectionsWaitTimeInMs()));

    return newConnectionRequestThrottler.isNewConnectionRequestAllowed(priority);
  }

  public boolean releasePermit(
      final short throttlingConfigId,
      final int clientId,
      final int baseTransceiverStationId,
      final int cellId,
      final int requestId) {

    final AtomicInteger permitCounter = this.getPermitCounter(baseTransceiverStationId, cellId);

    final int numberOfPermitsIfReleased = permitCounter.decrementAndGet();
    if (numberOfPermitsIfReleased < 0) {
      permitCounter.incrementAndGet();
    }

    final int numberOfReleasedPermits =
        this.permitRepository.releasePermit(
            throttlingConfigId, clientId, baseTransceiverStationId, cellId, requestId);

    if (this.highPrioPoolEnabled) {
      this.permitReleasedNotifier.notifyPermitReleased(baseTransceiverStationId, cellId);
    }

    return numberOfReleasedPermits == 1;
  }

  private boolean isPermitAvailable(
      final int baseTransceiverStationId,
      final int cellId,
      final int priority,
      final int maxConcurrency) {
    if (maxConcurrency < 0) {
      return true;
    } else if (maxConcurrency == 0) {
      return false;
    }
    final AtomicInteger permitCounter = this.getPermitCounter(baseTransceiverStationId, cellId);

    final int numberOfPermitsIfGranted = permitCounter.incrementAndGet();
    if (numberOfPermitsIfGranted > maxConcurrency) {
      permitCounter.decrementAndGet();

      if (!this.highPrioPoolEnabled) {
        return false;
      }

      if (priority <= MessagePriorityEnum.DEFAULT.getPriority()) {
        return false;
      }

      // Wait until permit is released
      return this.waitUntilPermitIsAvailable(
          baseTransceiverStationId, cellId, maxConcurrency, this.maxWaitForHighPrioInMs);
    }
    return true;
  }

  private boolean waitUntilPermitIsAvailable(
      final int baseTransceiverStationId,
      final int cellId,
      final int maxConcurrency,
      final int maxWaitForHighPrioInMs) {

    final long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < maxWaitForHighPrioInMs) {
      final boolean permitAvailable =
          this.permitReleasedNotifier.waitForAvailablePermit(
              baseTransceiverStationId, cellId, WAIT_TIME);
      if (!permitAvailable) {
        continue;
      }
      final AtomicInteger permitCounter = this.getPermitCounter(baseTransceiverStationId, cellId);
      final int numberOfPermitsIfGranted = permitCounter.incrementAndGet();
      if (numberOfPermitsIfGranted > maxConcurrency) {
        permitCounter.decrementAndGet();
      } else {
        return true;
      }
    }
    return false;
  }

  private AtomicInteger getPermitCounter(final int baseTransceiverStationId, final int cellId) {
    return this.permitsPerSegment
        .computeIfAbsent(baseTransceiverStationId, key -> new ConcurrentHashMap<>())
        .computeIfAbsent(cellId, key -> new AtomicInteger(0));
  }

  @Override
  public String toString() {
    return String.format(
        "PermitsPerNetworkSegment[covering %d base transceiver stations]",
        this.permitsPerSegment.size());
  }
}
