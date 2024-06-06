// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.model.ThrottlingSettings;
import org.opensmartgridplatform.throttling.repositories.PermitRepository;
import org.opensmartgridplatform.throttling.repositories.PermitRepository.PermitCountByNetworkSegment;
import org.opensmartgridplatform.throttling.service.PermitReleasedNotifier;

@ExtendWith(MockitoExtension.class)
class PermitsPerNetworkSegmentTest {
  private static final boolean WAIT_FOR_HIGH_PRIO_ENABLED = true;
  private static final int MAX_WAIT_FOR_HIGH_PRIO = 1000;
  private static final int MAX_WAIT_FOR_NEW_CONNECTIONS = 1200;

  @Mock private PermitRepository permitRepository;
  @Mock private PermitReleasedNotifier permitReleasedNotifier;
  private PermitsPerNetworkSegment permitsPerNetworkSegment;

  @BeforeEach
  void setUp() {
    this.permitsPerNetworkSegment =
        new PermitsPerNetworkSegment(
            this.permitRepository,
            this.permitReleasedNotifier,
            WAIT_FOR_HIGH_PRIO_ENABLED,
            MAX_WAIT_FOR_HIGH_PRIO);
  }

  @Test
  void testInitializeEmpty() {
    final short throttlingConfigId = Short.parseShort("1");

    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(Lists.emptyList());

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).isEmpty();
  }

  @Test
  void testInitialize() {
    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;

    final short throttlingConfigId = Short.parseShort("1");

    final PermitCountByNetworkSegment permitCountByNetworkSegment =
        this.newPermitCountByNetworkSegment(btsId, cellId, numberOfPermits);
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(List.of(permitCountByNetworkSegment));

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).hasSize(1);
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().get(btsId).get(cellId))
        .isEqualTo(numberOfPermits);
  }

  @Test
  void testInitializeUpdate() {
    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();

    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    // Update number of permits in database
    final int newNumberOfPermits = 4;

    final PermitCountByNetworkSegment permitCountByNetworkSegmentUpdate =
        this.newPermitCountByNetworkSegment(btsId, cellId, newNumberOfPermits);
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(List.of(permitCountByNetworkSegmentUpdate));

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).hasSize(1);
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().get(btsId).get(cellId))
        .isEqualTo(newNumberOfPermits);
  }

  @Test
  void testInitializeAdd() {
    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();

    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    // Update number of permits in database
    final int newBtsId = 4;

    final PermitCountByNetworkSegment permitCountByNetworkSegmentUpdate =
        this.newPermitCountByNetworkSegment(newBtsId, cellId, numberOfPermits);
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(List.of(permitCountByNetworkSegmentUpdate));

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).hasSize(1);
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().containsKey(btsId))
        .isFalse();
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().get(newBtsId).get(cellId))
        .isEqualTo(numberOfPermits);
  }

  @Test
  void testInitializeDelete() {
    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();

    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    // No permits in database
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(Lists.emptyList());

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 2000})
  void testHighPrioPoolTime(final int maxWaitForHighPrio) {
    this.permitsPerNetworkSegment =
        new PermitsPerNetworkSegment(
            this.permitRepository,
            this.permitReleasedNotifier,
            WAIT_FOR_HIGH_PRIO_ENABLED,
            maxWaitForHighPrio);

    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();
    final int clientId = 4;
    final int requestId = 5;
    final int priority = 6;
    final int maxConcurrency = numberOfPermits;
    final ThrottlingSettings throttlingSettings =
        this.newThrottlingSettings(maxConcurrency, 1000, 1000, 1000);
    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    final long start = System.currentTimeMillis();
    final boolean permitGranted =
        this.permitsPerNetworkSegment.requestPermit(
            throttlingConfigId, clientId, btsId, cellId, requestId, priority, throttlingSettings);
    assertThat(permitGranted).isFalse();
    assertThat(System.currentTimeMillis() - start).isGreaterThanOrEqualTo(maxWaitForHighPrio);

    verify(this.permitRepository, never())
        .grantPermit(throttlingConfigId, clientId, btsId, cellId, requestId);
  }

  @Test
  void testHighPrioPool() {
    final int maxWaitForHighPrio = 10000;
    final int waitBeforeRelease = 1000;
    this.permitsPerNetworkSegment =
        new PermitsPerNetworkSegment(
            this.permitRepository,
            this.permitReleasedNotifier,
            WAIT_FOR_HIGH_PRIO_ENABLED,
            maxWaitForHighPrio);

    final int btsId = 1;
    final int cellId = 2;
    final int otherCellId = cellId + 1;
    final int numberOfPermits = 3;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();
    final int clientId = 4;
    final int requestId = 5;
    final int priority = 6;
    final int maxConcurrency = numberOfPermits;
    final ThrottlingSettings throttlingSettings =
        this.newThrottlingSettings(maxConcurrency, 1000, 1000, 1000);

    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    when(this.permitRepository.grantPermit(throttlingConfigId, clientId, btsId, cellId, requestId))
        .thenReturn(true);
    when(this.permitRepository.grantPermit(
            throttlingConfigId, clientId, btsId, otherCellId, requestId))
        .thenReturn(true);

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    final long start = System.currentTimeMillis();

    final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    executor.schedule(
        () -> {
          when(this.permitReleasedNotifier.waitForAvailablePermit(btsId, cellId, 1000))
              .thenReturn(false)
              .thenReturn(true);
          this.permitsPerNetworkSegment.releasePermit(
              throttlingConfigId, clientId, btsId, cellId, requestId);

          verify(this.permitReleasedNotifier, times(1)).notifyPermitReleased(btsId, cellId);
        },
        waitBeforeRelease,
        TimeUnit.MILLISECONDS);

    final boolean permitGrantedOtherCell =
        this.permitsPerNetworkSegment.requestPermit(
            throttlingConfigId,
            clientId,
            btsId,
            otherCellId,
            requestId,
            priority,
            throttlingSettings);
    assertThat(permitGrantedOtherCell).isTrue();
    assertThat((int) (System.currentTimeMillis() - start)).isBetween(0, waitBeforeRelease);

    final boolean permitGranted =
        this.permitsPerNetworkSegment.requestPermit(
            throttlingConfigId, clientId, btsId, cellId, requestId, priority, throttlingSettings);
    assertThat(permitGranted).isTrue();
    assertThat((int) (System.currentTimeMillis() - start))
        .isBetween(waitBeforeRelease, maxWaitForHighPrio);
  }

  @Test
  void testHighPrioPoolDisabled() {
    this.permitsPerNetworkSegment =
        new PermitsPerNetworkSegment(
            this.permitRepository, this.permitReleasedNotifier, false, MAX_WAIT_FOR_HIGH_PRIO);

    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 1;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();
    final int clientId = 4;
    final int requestId = 5;
    final int priority = 6;
    final int maxConcurrency = 1;
    final ThrottlingSettings throttlingSettings =
        this.newThrottlingSettings(maxConcurrency, 1000, 1000, 1000);

    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    final boolean permitGranted =
        this.permitsPerNetworkSegment.requestPermit(
            throttlingConfigId, clientId, btsId, cellId, requestId, priority, throttlingSettings);
    assertThat(permitGranted).isFalse();
  }

  @Test
  void testLowPrioPool() {
    this.permitsPerNetworkSegment =
        new PermitsPerNetworkSegment(
            this.permitRepository,
            this.permitReleasedNotifier,
            WAIT_FOR_HIGH_PRIO_ENABLED,
            MAX_WAIT_FOR_HIGH_PRIO);

    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 1;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();
    final int clientId = 4;
    final int requestId = 5;
    final int priority = 1;
    final int maxConcurrency = 1;
    final ThrottlingSettings throttlingSettings =
        this.newThrottlingSettings(maxConcurrency, 1000, 1000, 1000);

    this.preparePermits(btsId, cellId, numberOfPermits, throttlingConfigId);

    final boolean permitGranted =
        this.permitsPerNetworkSegment.requestPermit(
            throttlingConfigId, clientId, btsId, cellId, requestId, priority, throttlingSettings);
    assertThat(permitGranted).isFalse();
  }

  @Test
  void testMaxNewRequestsClearedInTime() {
    final long start = System.currentTimeMillis();

    this.assertMaxNewRequests(MAX_WAIT_FOR_NEW_CONNECTIONS - 200, true);

    assertThat((int) (System.currentTimeMillis() - start))
        .isBetween(0, MAX_WAIT_FOR_NEW_CONNECTIONS);
  }

  @Test
  void testMaxNewRequestsReached() {
    final long start = System.currentTimeMillis();

    this.assertMaxNewRequests(MAX_WAIT_FOR_NEW_CONNECTIONS + 200, false);

    assertThat((int) (System.currentTimeMillis() - start))
        .isGreaterThanOrEqualTo(MAX_WAIT_FOR_NEW_CONNECTIONS);
  }

  @Test
  void tesMaxNewRequestsDisabled() {
    this.assertDisabledFunctions(10000, -1, true);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).isNotEmpty();
    assertThat(this.permitsPerNetworkSegment.newConnectionRequestThrottlerPerSegment()).isEmpty();
  }

  @Test
  void tesMaxConcurrencyDisabled() {
    this.assertDisabledFunctions(-1, 10000, true);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).isEmpty();
    assertThat(this.permitsPerNetworkSegment.newConnectionRequestThrottlerPerSegment())
        .isNotEmpty();
  }

  @Test
  void tesMaxNewRequestsZero() {
    this.assertDisabledFunctions(10000, 0, false);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).isEmpty();
    assertThat(this.permitsPerNetworkSegment.newConnectionRequestThrottlerPerSegment()).isEmpty();
  }

  @Test
  void tesMaxConcurrencyZero() {
    this.assertDisabledFunctions(0, 10000, false);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).isEmpty();
  }

  @Test
  void testToString() {
    this.permitsPerNetworkSegment =
        new PermitsPerNetworkSegment(
            this.permitRepository,
            this.permitReleasedNotifier,
            WAIT_FOR_HIGH_PRIO_ENABLED,
            MAX_WAIT_FOR_HIGH_PRIO);
    assertThat(this.permitsPerNetworkSegment.toString()).isNotNull();
  }

  void assertDisabledFunctions(
      final int maxConcurrency, final int maxNewConnections, final boolean expectGranted) {
    final int numberOfPermits = 2000;

    this.permitsPerNetworkSegment =
        new PermitsPerNetworkSegment(
            this.permitRepository,
            this.permitReleasedNotifier,
            WAIT_FOR_HIGH_PRIO_ENABLED,
            MAX_WAIT_FOR_HIGH_PRIO);

    final int btsId = 1;
    final int cellId = 2;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();
    final int clientId = 4;
    final int requestId = 5;
    final int priority = 6;
    final long maxNewConnectionsResetTimeInMs = 100;

    final ThrottlingSettings throttlingSettings =
        this.newThrottlingSettings(
            maxConcurrency,
            maxNewConnections,
            maxNewConnectionsResetTimeInMs,
            MAX_WAIT_FOR_NEW_CONNECTIONS);

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    for (int i = 0; i < numberOfPermits; i++) {
      final int newRequestId = requestId + i;
      if (expectGranted) {
        when(this.permitRepository.grantPermit(
                throttlingConfigId, clientId, btsId, cellId, newRequestId))
            .thenReturn(true);
      } else {
        verify(this.permitRepository, never())
            .grantPermit(throttlingConfigId, clientId, btsId, cellId, newRequestId);
      }
      final boolean permitGranted =
          this.permitsPerNetworkSegment.requestPermit(
              throttlingConfigId,
              clientId,
              btsId,
              cellId,
              newRequestId,
              priority,
              throttlingSettings);
      assertThat(permitGranted).isEqualTo(expectGranted);
    }
  }

  private void assertMaxNewRequests(
      final int maxNewConnectionsResetTimeInMs, final boolean expectPermitGranted) {
    this.permitsPerNetworkSegment =
        new PermitsPerNetworkSegment(
            this.permitRepository,
            this.permitReleasedNotifier,
            WAIT_FOR_HIGH_PRIO_ENABLED,
            MAX_WAIT_FOR_HIGH_PRIO);

    final int btsId = 1;
    final int cellId = 2;
    final int numberOfPermits = 3;
    final short throttlingConfigId = Integer.valueOf(1).shortValue();
    final int clientId = 4;
    final int requestId = 5;
    final int priority = 6;
    final int maxConcurrency = 99;
    final int maxNewConnections = numberOfPermits;
    final ThrottlingSettings throttlingSettings =
        this.newThrottlingSettings(
            maxConcurrency,
            maxNewConnections,
            maxNewConnectionsResetTimeInMs,
            MAX_WAIT_FOR_NEW_CONNECTIONS);

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    for (int i = 0; i < numberOfPermits; i++) {
      final int newRequestId = requestId + i;
      when(this.permitRepository.grantPermit(
              throttlingConfigId, clientId, btsId, cellId, newRequestId))
          .thenReturn(true);
      final boolean permitGranted =
          this.permitsPerNetworkSegment.requestPermit(
              throttlingConfigId,
              clientId,
              btsId,
              cellId,
              newRequestId,
              priority,
              throttlingSettings);
      assertThat(permitGranted).isTrue();
    }

    final boolean permitGranted =
        this.permitsPerNetworkSegment.requestPermit(
            throttlingConfigId, clientId, btsId, cellId, requestId, priority, throttlingSettings);
    assertThat(permitGranted).isEqualTo(expectPermitGranted);
  }

  private void preparePermits(
      final int btsId,
      final int cellId,
      final int numberOfPermits,
      final short throttlingConfigId) {
    final PermitCountByNetworkSegment permitCountByNetworkSegment =
        this.newPermitCountByNetworkSegment(btsId, cellId, numberOfPermits);
    when(this.permitRepository.permitsByNetworkSegment(throttlingConfigId))
        .thenReturn(List.of(permitCountByNetworkSegment));

    this.permitsPerNetworkSegment.initialize(throttlingConfigId);

    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment()).hasSize(1);
    assertThat(this.permitsPerNetworkSegment.permitsPerNetworkSegment().get(btsId).get(cellId))
        .isEqualTo(numberOfPermits);
  }

  private PermitCountByNetworkSegment newPermitCountByNetworkSegment(
      final int btsId, final int cellId, final int numberOfPermits) {
    final PermitCountByNetworkSegment permitCountByNetworkSegment =
        mock(PermitCountByNetworkSegment.class);
    when(permitCountByNetworkSegment.getBaseTransceiverStationId()).thenReturn(btsId);
    when(permitCountByNetworkSegment.getCellId()).thenReturn(cellId);
    when(permitCountByNetworkSegment.getNumberOfPermits()).thenReturn(numberOfPermits);
    return permitCountByNetworkSegment;
  }

  private ThrottlingSettings newThrottlingSettings(
      final int maxConcurrency,
      final int maxNewConnections,
      final long maxNewConnectionsResetTimeInMs,
      final long maxNewConnectionsWaitTimeInMs) {
    return new ThrottlingSettings() {
      @Override
      public int getMaxConcurrency() {
        return maxConcurrency;
      }

      @Override
      public int getMaxNewConnections() {
        return maxNewConnections;
      }

      @Override
      public long getMaxNewConnectionsResetTimeInMs() {
        return maxNewConnectionsResetTimeInMs;
      }

      @Override
      public long getMaxNewConnectionsWaitTimeInMs() {
        return maxNewConnectionsWaitTimeInMs;
      }
    };
  }
}
