// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;
import org.opensmartgridplatform.throttling.model.NetworkSegment;
import org.opensmartgridplatform.throttling.model.Permit;
import org.opensmartgridplatform.throttling.model.PermitKey;
import org.opensmartgridplatform.throttling.model.PermitRequest;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RedisPermitService implements PermitService {

  private static final Logger log = LoggerFactory.getLogger(RedisPermitService.class);

  private final RedissonClient redisson;
  private final Duration timeToLive;
  private final int tryLockTimeMs;
  private final int highPrioTimeToLive;

  public RedisPermitService(
      final RedissonClient redisson,
      @Value("#{T(java.time.Duration).parse('${cleanup.permits.time-to-live:PT1H}')}")
          final Duration timeToLive,
      @Value("${permit.lock.try.max.in.ms:100}") final int tryLockTimeMs,
      @Value("${wait.for.high.prio.max.in.ms:10000}") final int highPrioTimeToLive) {
    this.redisson = redisson;
    this.timeToLive = timeToLive;
    this.tryLockTimeMs = tryLockTimeMs;
    this.highPrioTimeToLive = highPrioTimeToLive;
  }

  @Override
  public boolean createPermit(
      final NetworkSegment networkSegment,
      final PermitRequest permitRequest,
      final int maxConcurrentRequests,
      final boolean highPrio) {

    boolean granted = false;

    final PermitKey permitKey = PermitKey.builder().networkSegment(networkSegment).build();
    this.removeExpiredPermits(permitKey);
    this.removeExpiredRequestsFromLobby(permitKey);

    final RLock lock = this.redisson.getLock(permitKey.lockId());

    try {
      if (lock.tryLock(this.tryLockTimeMs, TimeUnit.MILLISECONDS)) {
        try {
          granted =
              this.tryRegisterPermit(
                  networkSegment, permitRequest, maxConcurrentRequests, highPrio, permitKey);
        } finally {
          lock.unlock();
        }
      }
    } catch (final InterruptedException e) {
      log.error("Interrupted request {} while waiting for lock", permitRequest.getRequestId());
      Thread.currentThread().interrupt();
    }

    return granted;
  }

  private boolean tryRegisterPermit(
      final NetworkSegment networkSegment,
      final PermitRequest permitRequest,
      final int maxConcurrentRequests,
      final boolean highPrio,
      final PermitKey permitKey) {
    boolean granted = false;

    final RScoredSortedSet<PermitRequest> lobby =
        this.redisson.getScoredSortedSet(permitKey.lobby());

    if (highPrio || lobby.isEmpty()) {
      final RScoredSortedSet<Permit> permits = this.redisson.getScoredSortedSet(permitKey.key());
      final int numberOfRegisteredPermits = permits.size();

      log.debug(
          "Trying to register a permit for request[{}] (max-concurrent-requests: {}, number-of-registered-permits: {})",
          permitRequest.getRequestId(),
          maxConcurrentRequests,
          numberOfRegisteredPermits);

      if (this.permitsAvailable(maxConcurrentRequests, numberOfRegisteredPermits)) {
        granted =
            permits.add(Instant.now().toEpochMilli(), new Permit(networkSegment, permitRequest));
      }

      if (highPrio) {
        this.updateLobby(permitRequest, granted, lobby);
      }
    }

    return granted;
  }

  private boolean permitsAvailable(
      final int maxConcurrentRequests, final int numberOfRegisteredPermits) {
    return maxConcurrentRequests < 0 || numberOfRegisteredPermits < maxConcurrentRequests;
  }

  private void updateLobby(
      final PermitRequest permitRequest,
      final boolean granted,
      final RScoredSortedSet<PermitRequest> lobby) {
    if (granted) {
      lobby.remove(permitRequest);
    } else {
      lobby.addIfAbsent(Instant.now().toEpochMilli(), permitRequest);
    }
  }

  @Override
  public boolean removePermit(
      final NetworkSegment networkSegment, final PermitRequest permitRequest) {

    final PermitKey permitKey = PermitKey.builder().networkSegment(networkSegment).build();
    final RScoredSortedSet<Permit> permits = this.redisson.getScoredSortedSet(permitKey.key());

    final boolean released =
        permits.stream()
            .filter(Objects::nonNull)
            .filter(
                p ->
                    p.permitRequest().getClientId() == permitRequest.getClientId()
                        && p.permitRequest().getRequestId() == permitRequest.getRequestId())
            .findFirst()
            .map(permits::remove)
            .orElse(false);

    log.debug(
        "Permit for request [{}] {} removed",
        permitRequest.getRequestId(),
        released ? "is" : " is not");

    return released;
  }

  private void removeExpiredPermits(final PermitKey permitKey) {
    final RScoredSortedSet<Permit> permits = this.redisson.getScoredSortedSet(permitKey.key());

    if (!permits.isEmpty()) {
      final Instant endTime = Instant.now().minusSeconds(this.timeToLive.getSeconds());
      final int removed =
          permits.removeRangeByScore(Double.NEGATIVE_INFINITY, true, endTime.toEpochMilli(), true);
      if (removed > 0) {
        log.debug("Removed {} expired permits", removed);
      }
    }
  }

  private void removeExpiredRequestsFromLobby(final PermitKey permitKey) {
    final RScoredSortedSet<PermitRequest> requests =
        this.redisson.getScoredSortedSet(permitKey.lobby());

    if (!requests.isEmpty()) {
      final Instant endTime = Instant.now().minusMillis(this.highPrioTimeToLive);
      final int removed =
          requests.removeRangeByScore(Double.NEGATIVE_INFINITY, true, endTime.toEpochMilli(), true);
      if (removed > 0) {
        log.debug("Removed {} expired high priority requests", removed);
      }
    }
  }

  @Override
  public Optional<Permit> findByClientIdAndRequestId(final int clientId, final int requestId) {
    final RKeys keys = this.redisson.getKeys();
    final PermitKey permitKey = PermitKey.builder().build();
    final Iterable<String> keysByPattern = keys.getKeysByPattern(permitKey.keyPattern());

    return StreamSupport.stream(keysByPattern.spliterator(), true)
        .map(this.redisson::getScoredSortedSet)
        .flatMap(RScoredSortedSet::stream)
        .map(p -> (Permit) p)
        .filter(
            p ->
                p.permitRequest().getClientId() == clientId
                    && p.permitRequest().getRequestId() == requestId)
        .findFirst();
  }

  @Override
  public long countByClientId(final int clientId) {
    final RKeys keys = this.redisson.getKeys();
    final PermitKey permitKey = PermitKey.builder().build();
    final Iterable<String> keysByPattern = keys.getKeysByPattern(permitKey.keyPattern());

    return StreamSupport.stream(keysByPattern.spliterator(), true)
        .map(this.redisson::getScoredSortedSet)
        .flatMap(RScoredSortedSet::stream)
        .map(p -> (Permit) p)
        .filter(p -> p.permitRequest().getClientId() == clientId)
        .count();
  }
}
