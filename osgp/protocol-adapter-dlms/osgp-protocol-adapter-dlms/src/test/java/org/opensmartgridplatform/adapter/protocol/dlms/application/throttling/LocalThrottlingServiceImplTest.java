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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  private static final Integer MAX_NEW_CONNECTION_REQUESTS = 10;
  private static final Integer MAX_OPEN_CONNECTIONS = MAX_NEW_CONNECTION_REQUESTS * 2;
  private static final Integer RESET_TIME = 500;

  private static final Integer CLEANUP_PERMITS_INTERVAL = 1500;

  private static final Duration PERMIT_TTL = Duration.of(2, ChronoUnit.SECONDS);

  private LocalThrottlingServiceImpl throttlingService;

  @BeforeEach
  void setUp() {
    this.throttlingService = new LocalThrottlingServiceImpl();
    ReflectionTestUtils.setField(
        this.throttlingService, "maxOpenConnections", MAX_OPEN_CONNECTIONS);
    ReflectionTestUtils.setField(
        this.throttlingService, "maxNewConnectionRequests", MAX_NEW_CONNECTION_REQUESTS);
    ReflectionTestUtils.setField(this.throttlingService, "resetTime", RESET_TIME);
    ReflectionTestUtils.setField(
        this.throttlingService, "cleanupExpiredPermitsInterval", CLEANUP_PERMITS_INTERVAL);
    ReflectionTestUtils.setField(this.throttlingService, "timeToLive", PERMIT_TTL);
    this.throttlingService.postConstruct();
  }

  @Test
  void testThrottlingOpenConnections() throws InterruptedException {

    // Claim 10
    final List<Permit> firstBatch = this.requestPermit(MAX_NEW_CONNECTION_REQUESTS);
    // Sleep longer than reset time
    Thread.sleep(RESET_TIME + 100);
    // Next 10
    this.requestPermit(MAX_NEW_CONNECTION_REQUESTS);
    // Sleep longer than reset time
    Thread.sleep(RESET_TIME + 100);
    // Next causes PermitDenied
    assertThrows(ThrottlingPermitDeniedException.class, () -> this.requestPermit(1));
    // Free one
    this.releasePermit(List.of(firstBatch.get(0)));
    // Claim one
    this.requestPermit(1);
    // Next causes PermitDenied
    assertThrows(ThrottlingPermitDeniedException.class, () -> this.requestPermit(1));

    this.assertPermitsInMemory(MAX_OPEN_CONNECTIONS);
    this.assertAvailablePermits(0);
  }

  @Test
  void testThrottlingMaxNewConnections() {
    this.assertAvailableNewConnections(MAX_NEW_CONNECTION_REQUESTS);
    // Claim max new
    this.requestPermit(MAX_NEW_CONNECTION_REQUESTS);
    this.assertAvailableNewConnections(0);
    // Next causes PermitDenied
    assertThrows(ThrottlingPermitDeniedException.class, () -> this.requestPermit(1));

    this.assertPermitsInMemory(MAX_NEW_CONNECTION_REQUESTS);
    this.assertAvailablePermits(MAX_OPEN_CONNECTIONS - MAX_NEW_CONNECTION_REQUESTS);
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
    final Permit permit = this.requestPermit(1).get(0);
    assertThat(permit.getThrottlingConfigId()).isEqualTo((short) 0);
    assertThat(permit.getClientId()).isEqualTo((short) 0);
    assertThat(permit.getRequestId()).isEqualTo(1);

    final Permit secondPermit = this.requestPermit(1).get(0);
    assertThat(secondPermit.getRequestId()).isEqualTo(2);

    this.assertPermitsInMemory(2);
    this.assertAvailablePermits(MAX_OPEN_CONNECTIONS - 2);
  }

  @Test
  void testCleanupPermit() throws InterruptedException {
    final long minCleanupInterval = PERMIT_TTL.toMillis() + CLEANUP_PERMITS_INTERVAL + 10;

    // First 5 permits
    this.requestPermit(5).get(0);
    this.assertPermitsInMemory(5);
    Thread.sleep(minCleanupInterval / 2);

    // one more permit
    this.assertPermitsInMemory(5);
    this.requestPermit(1).get(0);
    this.assertPermitsInMemory(6);
    Thread.sleep(minCleanupInterval / 2);

    // First 5 are cleaned
    this.assertPermitsInMemory(1);
    this.assertAvailablePermits(MAX_OPEN_CONNECTIONS - 1);
  }

  private List<Permit> requestPermit(final int requests) {
    final int btsId = 1;
    final int cellId = 2;
    final int priority = 3;

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
