/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.application.throttling;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.annotation.LocalThrottlingServiceCondition;
import org.opensmartgridplatform.throttling.ThrottlingPermitDeniedException;
import org.opensmartgridplatform.throttling.api.Permit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
@Conditional(LocalThrottlingServiceCondition.class)
public class LocalThrottlingServiceImpl implements ThrottlingService {

  private static final long WAIT_FOR_LOCK = 10;

  private final AtomicInteger requestIdCounter = new AtomicInteger(0);

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalThrottlingServiceImpl.class);

  private final int maxOpenConnections;
  private final int maxNewConnectionRequests;

  @Value("${throttling.max.wait.for.permit}")
  private int maxWaitForPermitInMs;

  @Value("${throttling.max.new.connection.reset.time}")
  private int maxNewConnectionResetTime;

  @Value("${cleanup.permits.interval}")
  private int cleanupExpiredPermitsInterval;

  @Value("#{T(java.time.Duration).parse('${cleanup.permits.time-to-live:PT1H}')}")
  private Duration timeToLive;

  private final Semaphore openConnectionsSemaphore;
  private final Semaphore newConnectionRequestsSemaphore;
  private final Timer resetNewConnectionRequestsTimer;
  private final Timer cleanupExpiredPermitsTimer;
  private final ReentrantLock resetTimerLock;

  private final ConcurrentHashMap<Integer, Permit> permitsByRequestId;

  public LocalThrottlingServiceImpl(
      @Value("${throttling.max.open.connections}") final int maxOpenConnections,
      @Value("${throttling.max.new.connection.requests}") final int maxNewConnectionRequests) {
    this.maxOpenConnections = maxOpenConnections;
    this.maxNewConnectionRequests = maxNewConnectionRequests;
    this.openConnectionsSemaphore = new Semaphore(maxOpenConnections);
    this.newConnectionRequestsSemaphore = new Semaphore(maxNewConnectionRequests);

    this.resetNewConnectionRequestsTimer = new Timer();
    this.cleanupExpiredPermitsTimer = new Timer();

    this.resetTimerLock = new ReentrantLock();
    this.permitsByRequestId = new ConcurrentHashMap<>();
  }

  @PostConstruct
  public void postConstruct() {
    this.resetNewConnectionRequestsTimer.schedule(
        new ResetNewConnectionRequestsTask(),
        this.maxNewConnectionResetTime,
        this.maxNewConnectionResetTime);

    this.cleanupExpiredPermitsTimer.schedule(
        new CleanupExpiredPermitsTask(),
        this.cleanupExpiredPermitsInterval,
        this.cleanupExpiredPermitsInterval);
    LOGGER.info("Initialized ThrottlingService. {}", this);
  }

  @PreDestroy
  public void preDestroy() {
    this.resetNewConnectionRequestsTimer.cancel();
    this.cleanupExpiredPermitsTimer.cancel();
    this.resetNewConnectionRequestsTimer.purge();
    this.cleanupExpiredPermitsTimer.purge();
  }

  @Override
  public Permit requestPermit(
      final Integer baseTransceiverStationId, final Integer cellId, final Integer priority) {

    this.awaitReset();

    // newConnectionRequest will be released by ResetNewConnectionRequestsTask
    this.requestPermit(this.newConnectionRequestsSemaphore, priority, "newConnectionRequest");

    // openConnection will be released releasePermit method or CleanupExpiredPermitsTask
    this.requestPermit(this.openConnectionsSemaphore, priority, "openConnection");

    return this.createPermit();
  }

  @Override
  public void releasePermit(final Permit permit) {
    LOGGER.debug(
        "closeConnection(). available = {}", this.openConnectionsSemaphore.availablePermits());
    if (this.openConnectionsSemaphore.availablePermits() < this.maxOpenConnections) {
      this.openConnectionsSemaphore.release();
    }

    this.permitsByRequestId.remove(permit.getRequestId());
  }

  private void requestPermit(
      final Semaphore semaphore, final int priority, final String permitDescription) {
    LOGGER.debug("{}. available = {} ", permitDescription, semaphore.availablePermits());

    try {
      if (!semaphore.tryAcquire(this.maxWaitForPermitInMs, TimeUnit.MILLISECONDS)) {
        throw new ThrottlingPermitDeniedException(
            permitDescription + ": could not acquire permit for request with priority " + priority,
            priority);
      }
      LOGGER.debug("{} granted. available = {} ", permitDescription, semaphore.availablePermits());
    } catch (final InterruptedException e) {
      LOGGER.warn(permitDescription + ": unable to acquire permit", e);
      Thread.currentThread().interrupt();
    }
  }

  private void awaitReset() {
    LOGGER.debug(
        "Await reset for newConnection. available = {} ",
        this.newConnectionRequestsSemaphore.availablePermits());

    while (this.resetTimerLock.isLocked()) {
      try {
        LOGGER.info("Wait {}ms while reset timer is locked", WAIT_FOR_LOCK);
        synchronized (this.requestIdCounter) {
          this.requestIdCounter.wait(WAIT_FOR_LOCK);
        }
      } catch (final InterruptedException e) {
        LOGGER.warn("Unable to acquire New Connection Request Lock", e);
        Thread.currentThread().interrupt();
      }
    }
  }

  private class ResetNewConnectionRequestsTask extends TimerTask {

    @Override
    public void run() {

      try {
        LocalThrottlingServiceImpl.this.resetTimerLock.lock();

        final int nrOfPermitsToBeReleased =
            LocalThrottlingServiceImpl.this.maxNewConnectionRequests
                - LocalThrottlingServiceImpl.this.newConnectionRequestsSemaphore.availablePermits();

        LOGGER.debug(
            "releasing {} permits on newConnectionRequestsSemaphore", nrOfPermitsToBeReleased);

        LocalThrottlingServiceImpl.this.newConnectionRequestsSemaphore.release(
            nrOfPermitsToBeReleased);

        LOGGER.debug(
            "ThrottlingService - Timer Reset and Unlocking, newConnectionRequests available = {}  ",
            LocalThrottlingServiceImpl.this.newConnectionRequestsSemaphore.availablePermits());
      } finally {
        LocalThrottlingServiceImpl.this.resetTimerLock.unlock();
      }
    }
  }

  private class CleanupExpiredPermitsTask extends TimerTask {

    @Override
    public void run() {
      final Instant createdAtBefore =
          Instant.now().minus(LocalThrottlingServiceImpl.this.timeToLive);
      try {
        LocalThrottlingServiceImpl.this.resetTimerLock.lock();

        for (final Entry<Integer, Permit> permitForRequestId :
            LocalThrottlingServiceImpl.this.permitsByRequestId.entrySet()) {
          final Permit permit = permitForRequestId.getValue();
          if (permit.getCreatedAt().isBefore(createdAtBefore)) {
            LOGGER.warn("releasing expired permit: {}", permit);
            LocalThrottlingServiceImpl.this.releasePermit(permit);
          }
        }

        LOGGER.debug(
            "ThrottlingService - Timer Cleanup and Unlocking, openConnections available = {}  ",
            LocalThrottlingServiceImpl.this.openConnectionsSemaphore.availablePermits());
      } finally {
        LocalThrottlingServiceImpl.this.resetTimerLock.unlock();
      }
    }
  }

  @Override
  public String toString() {
    return String.format(
        "ThrottlingService. maxOpenConnections = %d, maxNewConnectionRequests=%d, maxNewConnectionResetTime=%d",
        this.maxOpenConnections, this.maxNewConnectionRequests, this.maxNewConnectionResetTime);
  }

  private Permit createPermit() {
    final int requestId = this.requestIdCounter.incrementAndGet();

    final Permit permit = new Permit(requestId);
    this.permitsByRequestId.put(permit.getRequestId(), permit);

    return permit;
  }
}
