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

  private static final int TRY_LOCK_TIME_MS = 100;
  private static final int WAIT_TIME_MS = 1000;

  private final RedissonClient redisson;
  private final Sleeper sleeper;
  private final Duration timeToLive;

  public RedisPermitService(
      final RedissonClient redisson,
      final Sleeper sleeper,
      @Value("#{T(java.time.Duration).parse('${cleanup.permits.time-to-live:PT1H}')}")
          final Duration timeToLive) {
    this.redisson = redisson;
    this.sleeper = sleeper;
    this.timeToLive = timeToLive;
  }

  @Override
  public boolean createPermit(
      final NetworkSegment networkSegment,
      final int clientId,
      final int requestId,
      final int maxConcurrentRequests) {

    boolean granted = false;

    final PermitKey permitKey = PermitKey.builder().networkSegment(networkSegment).build();
    final RLock lock = this.redisson.getLock(permitKey.lockId());

    try {
      lock.lock();

      final RScoredSortedSet<Permit> permits = this.redisson.getScoredSortedSet(permitKey.key());

      if (maxConcurrentRequests < 0 || permits.size() < maxConcurrentRequests) {
        granted =
            permits.add(
                Instant.now().toEpochMilli(), new Permit(networkSegment, clientId, requestId));
      }
    } finally {
      lock.unlock();
    }

    log.trace("Permit for request {} {} granted", requestId, granted ? "is" : " is not");

    return granted;
  }

  @SuppressWarnings("squid:S2222")
  @Override
  public boolean createPermitWithHighPriority(
      final NetworkSegment networkSegment,
      final int clientId,
      final int requestId,
      final int maxConcurrentRequests) {

    final PermitKey permitKey = PermitKey.builder().networkSegment(networkSegment).build();
    final RLock lock = this.redisson.getLock(permitKey.lockId());

    boolean granted = false;

    try {

      if (lock.tryLock(TRY_LOCK_TIME_MS, TimeUnit.MILLISECONDS)) {
        this.sleeper.sleep(WAIT_TIME_MS);
        log.trace("High priority request {} is waiting for lock", requestId);
        granted = this.createPermit(networkSegment, clientId, requestId, maxConcurrentRequests);
      }
    } catch (final InterruptedException e) {
      log.error("Interrupted request {} while waiting for lock", requestId);
      Thread.currentThread().interrupt();
      return false;
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }

    return granted;
  }

  @Override
  public boolean removePermit(
      final NetworkSegment networkSegment, final int clientId, final int requestId) {

    final PermitKey permitKey = PermitKey.builder().networkSegment(networkSegment).build();
    final RScoredSortedSet<Permit> permits = this.redisson.getScoredSortedSet(permitKey.key());

    final boolean released =
        permits.stream()
            .filter(Objects::nonNull)
            .filter(p -> p.clientId() == clientId && p.requestId() == requestId)
            .findFirst()
            .map(permits::remove)
            .orElse(false);

    log.trace("Permit for request {} {} removed", requestId, released ? "is" : " is not");

    this.removeExpiredPermits(permitKey);
    return released;
  }

  private void removeExpiredPermits(final PermitKey permitKey) {
    final RScoredSortedSet<Permit> permits = this.redisson.getScoredSortedSet(permitKey.key());
    final Instant endTime = Instant.now().minusSeconds(this.timeToLive.getSeconds());
    final int removed =
        permits.removeRangeByScore(Double.NEGATIVE_INFINITY, true, endTime.toEpochMilli(), true);
    log.trace("Removed {} expired permits", removed);
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
        .filter(p -> p.clientId() == clientId && p.requestId() == requestId)
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
        .filter(p -> p.clientId() == clientId)
        .count();
  }
}
