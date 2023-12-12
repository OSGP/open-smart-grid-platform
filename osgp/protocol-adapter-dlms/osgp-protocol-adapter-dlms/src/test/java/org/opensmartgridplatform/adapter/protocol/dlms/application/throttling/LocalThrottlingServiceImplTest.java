// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.throttling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.ThrottlingPermitDeniedException;
import org.opensmartgridplatform.throttling.api.Permit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LocalThrottlingServiceImplTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LocalThrottlingServiceImplTest.class);
  private static final int MINIMAL_HIGH_PRIO = 5;

  private static final Integer MAX_NEW_CONNECTION_REQUESTS = 10;
  private static final Integer MAX_OPEN_CONNECTIONS = MAX_NEW_CONNECTION_REQUESTS * 2;
  private static final Integer MAX_NEW_CONNECTION_RESET_TIME = 200;
  private static final Integer MAX_WAIT_FOR_HIGH_PRIO = 500;
  private static final Integer CLEANUP_PERMITS_INTERVAL = 200;
  private static final Duration PERMIT_TTL = Duration.of(2, ChronoUnit.SECONDS);

  private LocalThrottlingServiceImpl throttlingService;

  @BeforeEach
  void setUp() {
    this.throttlingService =
        new LocalThrottlingServiceImpl(MAX_OPEN_CONNECTIONS, MAX_NEW_CONNECTION_REQUESTS);
    ReflectionTestUtils.setField(
        this.throttlingService, "maxNewConnectionResetTime", MAX_NEW_CONNECTION_RESET_TIME);
    ReflectionTestUtils.setField(
        this.throttlingService, "maxWaitForHighPrioInMs", MAX_WAIT_FOR_HIGH_PRIO);
    ReflectionTestUtils.setField(
        this.throttlingService, "cleanupExpiredPermitsInterval", CLEANUP_PERMITS_INTERVAL);
    ReflectionTestUtils.setField(this.throttlingService, "timeToLive", PERMIT_TTL);
    this.throttlingService.postConstruct();
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 5})
  void testThrottlingOpenConnections(final int priority) throws InterruptedException {
    // Claim 10
    final List<Permit> firstBatch = this.requestPermitLowPrio(MAX_NEW_CONNECTION_REQUESTS);
    // Sleep longer than reset time
    Thread.sleep(MAX_NEW_CONNECTION_RESET_TIME + 100);
    // Next 10
    this.requestPermitLowPrio(MAX_NEW_CONNECTION_REQUESTS);
    // Sleep longer than reset time
    Thread.sleep(MAX_NEW_CONNECTION_RESET_TIME + 100);
    this.assertPermitsInMemory(MAX_OPEN_CONNECTIONS);

    assertThrows(ThrottlingPermitDeniedException.class, () -> this.requestPermit(1, priority));

    // Free one
    this.releasePermit(List.of(firstBatch.get(0)));
    this.assertPermitsInMemory(MAX_OPEN_CONNECTIONS - 1);
    // Claim one
    this.requestPermit(1, priority);
    this.assertPermitsInMemory(MAX_OPEN_CONNECTIONS);
    // Next causes PermitDenied
    assertThrows(ThrottlingPermitDeniedException.class, () -> this.requestPermit(1, priority));

    this.assertPermitsInMemory(MAX_OPEN_CONNECTIONS);
    this.assertAvailablePermits(0);
  }

  @ParameterizedTest
  @ValueSource(ints = {4, 5})
  void testThrottlingMaxNewConnections(final int priority) {
    this.assertAvailableNewConnections(MAX_NEW_CONNECTION_REQUESTS);
    // Claim max new
    final List<Permit> permits = this.requestPermit(MAX_NEW_CONNECTION_REQUESTS, priority);
    this.assertAvailableNewConnections(0);

    this.releasePermitWithDelay(permits, MAX_WAIT_FOR_HIGH_PRIO / 2);

    final int nrOfOpenConnections;
    if (priority < MINIMAL_HIGH_PRIO) {
      assertThrows(
          ThrottlingPermitDeniedException.class,
          () -> this.requestPermit(MAX_NEW_CONNECTION_REQUESTS, priority));
      nrOfOpenConnections = MAX_NEW_CONNECTION_REQUESTS;
    } else {
      // high prio will wait for connection
      this.requestPermit(MAX_NEW_CONNECTION_REQUESTS, priority);
      nrOfOpenConnections = MAX_NEW_CONNECTION_REQUESTS * 2;
    }

    this.assertPermitsInMemory(nrOfOpenConnections);
    this.assertAvailablePermits(MAX_OPEN_CONNECTIONS - nrOfOpenConnections);
    this.assertAvailableNewConnections(0);
  }

  @Test
  void testReleaseUnknownPermit() {
    final Permit unknownPermit = new Permit(666);
    this.releasePermit(List.of(unknownPermit));
    this.assertPermitsInMemory(0);
    this.assertAvailablePermits(MAX_OPEN_CONNECTIONS);
  }

  @Test
  void testPermit() {
    final Permit permit = this.requestPermitLowPrio(1).get(0);
    assertThat(permit.getThrottlingConfigId()).isEqualTo((short) 0);
    assertThat(permit.getClientId()).isEqualTo((short) 0);
    assertThat(permit.getRequestId()).isEqualTo(1);

    final Permit secondPermit = this.requestPermitLowPrio(1).get(0);
    assertThat(secondPermit.getRequestId()).isEqualTo(2);

    this.assertPermitsInMemory(2);
    this.assertAvailablePermits(MAX_OPEN_CONNECTIONS - 2);
  }

  @Test
  void testCleanupPermit() throws InterruptedException {
    final long minCleanupInterval = PERMIT_TTL.toMillis() + CLEANUP_PERMITS_INTERVAL + 10;

    // First 5 permits
    this.requestPermitLowPrio(5);
    this.assertPermitsInMemory(5);
    Thread.sleep(minCleanupInterval / 2);

    // one more permit
    this.assertPermitsInMemory(5);
    this.requestPermitLowPrio(1);
    this.assertPermitsInMemory(6);
    Thread.sleep(minCleanupInterval / 2);

    // First 5 are cleaned
    this.assertPermitsInMemory(1);
    this.assertAvailablePermits(MAX_OPEN_CONNECTIONS - 1);
  }

  private List<Permit> requestPermitLowPrio(final int requests) {
    return this.requestPermit(requests, 3);
  }

  private List<Permit> requestPermitHighPrio(final int requests) {
    return this.requestPermit(requests, 8);
  }

  private List<Permit> requestPermit(final int requests, final int priority) {
    final int btsId = 1;
    final int cellId = 2;

    final List<Permit> permits = new ArrayList<>();
    for (int i = 0; i < requests; i++) {
      LOGGER.info("Incoming request {}", i);
      final Permit permit = this.throttlingService.requestPermit(btsId, cellId, priority);
      permits.add(permit);
    }

    return permits;
  }

  private void releasePermit(final List<Permit> permits) {
    for (final Permit permit : permits) {
      LOGGER.info("Closing Connection for permit {}", permit);
      this.throttlingService.releasePermit(permit);
    }
  }

  private void releasePermitWithDelay(final List<Permit> permits, final long delay) {
    final Timer timer = new Timer();
    final TimerTask task =
        new TimerTask() {
          @Override
          public void run() {
            LocalThrottlingServiceImplTest.this.releasePermit(permits);
          }
        }; // creating timer task
    timer.schedule(task, delay); // scheduling the task after the delay
  }

  private void assertPermitsInMemory(final Integer nrOfPermitsInMemory) {
    final ConcurrentHashMap<Integer, Permit> permitsInMemory =
        (ConcurrentHashMap<Integer, Permit>)
            ReflectionTestUtils.getField(this.throttlingService, "permitsByRequestId");
    assert permitsInMemory != null;
    assertThat(permitsInMemory).hasSize(nrOfPermitsInMemory);
  }

  private void assertAvailablePermits(final Integer availablePermits) {
    final Semaphore openConnectionsSemaphoreInMemory =
        (Semaphore)
            ReflectionTestUtils.getField(this.throttlingService, "openConnectionsSemaphore");
    assert openConnectionsSemaphoreInMemory != null;
    assertThat(openConnectionsSemaphoreInMemory.availablePermits()).isEqualTo(availablePermits);
  }

  private void assertAvailableNewConnections(final Integer availablePermits) {
    final Semaphore newConnectionRequestsSemaphoreInMemory =
        (Semaphore)
            ReflectionTestUtils.getField(this.throttlingService, "newConnectionRequestsSemaphore");
    assert newConnectionRequestsSemaphoreInMemory != null;
    assertThat(newConnectionRequestsSemaphoreInMemory.availablePermits())
        .isEqualTo(availablePermits);
  }
}
