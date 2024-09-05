// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.opensmartgridplatform.throttling.model.NetworkSegment;
import org.opensmartgridplatform.throttling.model.ThrottlingSettings;
import org.opensmartgridplatform.throttling.services.PermitService;
import org.opensmartgridplatform.throttling.services.RateLimitService;

@ExtendWith(MockitoExtension.class)
class PermitsPerNetworkSegmentTest {

  private static final int MAX_WAIT_FOR_HIGH_PRIO = 1000;

  private static final int BASE_TRANSCEIVER_STATION_ID = 5;
  private static final int CELL_ID = 2;
  private static final NetworkSegment NETWORK_SEGMENT =
      new NetworkSegment((short) 1, BASE_TRANSCEIVER_STATION_ID, CELL_ID);
  private static final int CLIENT_ID = 1;
  private static final int REQUEST_ID = 1;

  private static final int DEFAULT_PRIORITY = 4;
  private static final int HIGH_PRIORITY = 5;

  private static final int HIGH_PRIORITY_PERMIT_CREATION_WAIT_TIME = 100;

  @Mock private PermitService permitRepository;
  @Mock private RateLimitService rateLimitService;
  private PermitsPerNetworkSegment permitsPerNetworkSegment;

  private void createSubject(final boolean waitForHighPrioEnabled) {
    this.permitsPerNetworkSegment =
        new PermitsPerNetworkSegment(
            this.permitRepository,
            this.rateLimitService,
            waitForHighPrioEnabled,
            MAX_WAIT_FOR_HIGH_PRIO);
  }

  @Test
  void testRequestPermitForDefaultPriority() {
    this.mockRateLimiter(true);
    this.mockPermitCreation(true);

    this.createSubject(true);

    final boolean result =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, DEFAULT_PRIORITY, this.throttlingSettings(1));

    assertThat(result).isTrue();

    verify(this.permitRepository, never())
        .createPermitWithHighPriority(any(NetworkSegment.class), anyInt(), anyInt(), anyInt());
  }

  @Test
  void testRequestPermitWhenMaxConcurrencyDisabled() {
    this.mockRateLimiter(true);

    this.createSubject(true);

    final boolean result =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, DEFAULT_PRIORITY, this.throttlingSettings(0));

    assertThat(result).isFalse();

    verify(this.permitRepository, never())
        .createPermitWithHighPriority(any(NetworkSegment.class), anyInt(), anyInt(), anyInt());
  }

  @Test
  void testRequestPermitForDefaultPriorityNotGranted() {
    this.mockRateLimiter(true);
    this.mockPermitCreation(false);

    this.createSubject(true);

    final boolean result =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, DEFAULT_PRIORITY, this.throttlingSettings(1));

    assertThat(result).isFalse();

    verify(this.permitRepository, never())
        .createPermitWithHighPriority(any(NetworkSegment.class), anyInt(), anyInt(), anyInt());
  }

  @Test
  void testRequestPermitWithRateLimitReached() {
    this.mockRateLimiter(false);

    this.createSubject(true);

    final boolean resultForDefaultPriority =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, DEFAULT_PRIORITY, this.throttlingSettings(1));

    assertThat(resultForDefaultPriority).isFalse();

    final boolean resultForHighPriority =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, HIGH_PRIORITY, this.throttlingSettings(1));

    assertThat(resultForHighPriority).isFalse();

    verify(this.permitRepository, never())
        .createPermit(any(NetworkSegment.class), anyInt(), anyInt(), anyInt());
  }

  @Test
  void testRequestPermitForHighPriorityWithoutUsingHighPriorityPathWay() {
    this.mockRateLimiter(true);
    this.mockPermitCreation(true);

    this.createSubject(true);

    final boolean result =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, HIGH_PRIORITY, this.throttlingSettings(1));

    assertThat(result).isTrue();

    verify(this.permitRepository, never())
        .createPermitWithHighPriority(any(NetworkSegment.class), anyInt(), anyInt(), anyInt());
  }

  @Test
  void testRequestPermitForHighPriority() {
    this.mockRateLimiter(true);
    this.mockPermitCreation(false);
    this.mockHighPriorityPermitCreation(1);

    this.createSubject(true);

    final boolean result =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, HIGH_PRIORITY, this.throttlingSettings(1));

    assertThat(result).isTrue();

    verify(this.permitRepository, times(1))
        .createPermitWithHighPriority(any(NetworkSegment.class), anyInt(), anyInt(), anyInt());
  }

  @Test
  void testRequestPermitForHighPriorityWhenDisabled() {
    this.mockRateLimiter(true);
    this.mockPermitCreation(false);

    this.createSubject(false);

    final boolean result =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, HIGH_PRIORITY, this.throttlingSettings(1));

    assertThat(result).isFalse();

    verify(this.permitRepository, never())
        .createPermitWithHighPriority(any(NetworkSegment.class), anyInt(), anyInt(), anyInt());
  }

  @Test
  void testRequestPermitForHighPriorityWithWait() {
    this.mockRateLimiter(true);
    this.mockPermitCreation(false);
    this.mockHighPriorityPermitCreation(2);

    this.createSubject(true);

    final boolean result =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, HIGH_PRIORITY, this.throttlingSettings(1));

    assertThat(result).isTrue();

    verify(this.permitRepository, times(2))
        .createPermitWithHighPriority(any(NetworkSegment.class), anyInt(), anyInt(), anyInt());
  }

  @Test
  void testRequestPermitForHighPriorityNotGranted() {
    this.mockRateLimiter(true);
    this.mockPermitCreation(false);
    this.mockHighPriorityPermitCreation(Integer.MAX_VALUE);

    this.createSubject(true);

    final boolean result =
        this.permitsPerNetworkSegment.requestPermit(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, HIGH_PRIORITY, this.throttlingSettings(1));

    assertThat(result).isFalse();

    verify(this.permitRepository, atLeast(1))
        .createPermitWithHighPriority(any(NetworkSegment.class), anyInt(), anyInt(), anyInt());
  }

  @Test
  void testReleasePermit() {
    this.createSubject(true);

    this.permitsPerNetworkSegment.releasePermit(NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID);

    verify(this.permitRepository, times(1)).removePermit(NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID);
  }

  private void mockRateLimiter(final boolean isAllowed) {
    when(this.rateLimitService.isNewConnectionRequestAllowed(
            eq(BASE_TRANSCEIVER_STATION_ID), eq(CELL_ID), any(ThrottlingSettings.class)))
        .thenReturn(isAllowed);
  }

  private void mockPermitCreation(final boolean isCreated) {
    when(this.permitRepository.createPermit(
            eq(NETWORK_SEGMENT), eq(CLIENT_ID), eq(REQUEST_ID), anyInt()))
        .thenReturn(isCreated);
  }

  @SuppressWarnings("squid:S2925")
  private void mockHighPriorityPermitCreation(final int countWhenSuccessful) {
    when(this.permitRepository.createPermitWithHighPriority(
            eq(NETWORK_SEGMENT), eq(CLIENT_ID), eq(REQUEST_ID), anyInt()))
        .thenAnswer(
            new Answer<Boolean>() {
              private int count = 1;

              @SneakyThrows
              @Override
              public Boolean answer(final InvocationOnMock invocation) {
                Thread.sleep(HIGH_PRIORITY_PERMIT_CREATION_WAIT_TIME);
                return this.count++ == countWhenSuccessful;
              }
            });
  }

  private ThrottlingSettings throttlingSettings(final int maxConcurrency) {
    return this.createThrottlingSettings(maxConcurrency);
  }

  private ThrottlingSettings createThrottlingSettings(final int maxConcurrency) {
    return new ThrottlingSettings() {
      @Override
      public int getMaxConcurrency() {
        return maxConcurrency;
      }

      @Override
      public int getMaxNewConnections() {
        return 1;
      }

      @Override
      public long getMaxNewConnectionsResetTimeInMs() {
        return 1L;
      }

      @Override
      public long getMaxNewConnectionsWaitTimeInMs() {
        return 1L;
      }
    };
  }
}
