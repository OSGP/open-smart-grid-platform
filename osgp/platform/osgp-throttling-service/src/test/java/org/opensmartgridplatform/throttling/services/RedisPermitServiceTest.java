package org.opensmartgridplatform.throttling.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.throttling.model.NetworkSegment;
import org.opensmartgridplatform.throttling.model.Permit;
import org.opensmartgridplatform.throttling.model.PermitKey;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
class RedisPermitServiceTest {

  private static final int BASE_TRANSCEIVER_STATION_ID = 15;
  private static final int CELL_ID = 1;
  private static final NetworkSegment NETWORK_SEGMENT =
      new NetworkSegment((short) 7, BASE_TRANSCEIVER_STATION_ID, CELL_ID);
  private static final int CLIENT_ID = 9;
  private static final int REQUEST_ID = 42;

  @Mock private RedissonClient redissonClient;
  @Mock private Sleeper sleeper;
  @Mock private RLock lock;
  @Mock private RScoredSortedSet<Permit> permits;
  @Mock private RKeys keys;

  private RedisPermitService service;

  @BeforeEach
  void setup() {
    this.service = new RedisPermitService(this.redissonClient, this.sleeper, Duration.ZERO);
  }

  @Test
  void testCreatePermit() {
    final int maxConcurrentRequests = 30;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.prepareScoredSetForGranted(permitKey, maxConcurrentRequests - 5);

    final boolean created =
        this.service.createPermit(NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, maxConcurrentRequests);

    assertThat(created).isTrue();
    verify(this.lock, times(1)).unlock();
  }

  @Test
  void testCreatePermitMaxConcurrencyReached() {
    final int maxConcurrentRequests = 30;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.prepareScoredSetForNotGranted(permitKey, maxConcurrentRequests);

    final boolean created =
        this.service.createPermit(NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, maxConcurrentRequests);

    assertThat(created).isFalse();
    verify(this.lock, times(1)).unlock();
  }

  @Test
  void testCreatePermitMaxConcurrencyUnlimited() {
    final int maxConcurrentRequests = -1;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.prepareScoredSetForUnlimitedConcurrency(permitKey);

    final boolean created =
        this.service.createPermit(NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, maxConcurrentRequests);

    assertThat(created).isTrue();
    verify(this.lock, times(1)).unlock();
  }

  @SneakyThrows
  @Test
  void testCreateHighPriorityPermit() {
    final int maxConcurrentRequests = 30;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePrioLock();
    this.prepareScoredSetForGranted(permitKey, maxConcurrentRequests - 5);

    final boolean created =
        this.service.createPermitWithHighPriority(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, maxConcurrentRequests);

    assertThat(created).isTrue();
    verify(this.lock, times(2)).unlock();
    verify(this.sleeper, times(1)).sleep(1000L);
  }

  @SneakyThrows
  @Test
  void testCreateHighPriorityPermitMaxConcurrencyReached() {
    final int maxConcurrentRequests = 30;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePrioLock();
    this.prepareScoredSetForNotGranted(permitKey, maxConcurrentRequests);

    final boolean created =
        this.service.createPermitWithHighPriority(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, maxConcurrentRequests);

    assertThat(created).isFalse();
    verify(this.lock, times(2)).unlock();
    verify(this.sleeper, times(1)).sleep(1000L);
  }

  @SneakyThrows
  @Test
  void testCreateHighPriorityPermitWithInterruption() {
    final int maxConcurrentRequests = 30;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePrioLockForInterruption();

    final boolean created =
        this.service.createPermitWithHighPriority(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, maxConcurrentRequests);

    assertThat(created).isFalse();
    verify(this.lock, times(1)).unlock();
  }

  @SneakyThrows
  @Test
  void testCreateHighPriorityPermitUnableToLock() {
    final int maxConcurrentRequests = 30;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePrioLockForFailure();

    final boolean created =
        this.service.createPermitWithHighPriority(
            NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID, maxConcurrentRequests);

    assertThat(created).isFalse();
    verify(this.lock, never()).unlock();
  }

  @Test
  void testRemovePermit() {
    final PermitKey permitKey = this.createPermitKey();
    when(this.redissonClient.getScoredSortedSet(permitKey.key()))
        .thenAnswer(invocation -> this.permits);
    when(this.permits.stream()).thenReturn(List.of(this.createPermit()).stream());
    when(this.permits.remove(this.createPermit())).thenReturn(true);

    final boolean removed = this.service.removePermit(NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID);

    assertThat(removed).isTrue();
  }

  @Test
  void testFindByClientIdAndRequestId() {
    final Permit permit = this.createPermit();
    final String key = PermitKey.builder().permit(permit).build().key();

    when(this.redissonClient.getKeys()).thenReturn(this.keys);
    when(this.keys.getKeysByPattern(PermitKey.builder().build().keyPattern()))
        .thenAnswer(invocation -> List.of(key));
    when(this.redissonClient.getScoredSortedSet(key)).thenAnswer(invocation -> this.permits);
    when(this.permits.stream()).thenReturn(Stream.of(permit));

    final Optional<Permit> optionalPermit =
        this.service.findByClientIdAndRequestId(CLIENT_ID, REQUEST_ID);

    assertThat(optionalPermit).isPresent();
    assertThat(optionalPermit.get().clientId()).isEqualTo(CLIENT_ID);
    assertThat(optionalPermit.get().requestId()).isEqualTo(REQUEST_ID);
  }

  @Test
  void testFindByClientIdAndRequestIdNotFound() {
    final Permit permit = this.createPermit();
    final String key = PermitKey.builder().permit(permit).build().key();

    when(this.redissonClient.getKeys()).thenReturn(this.keys);
    when(this.keys.getKeysByPattern(PermitKey.builder().build().keyPattern()))
        .thenAnswer(invocation -> List.of(key));
    when(this.redissonClient.getScoredSortedSet(key)).thenAnswer(invocation -> this.permits);
    when(this.permits.stream()).thenReturn(Stream.of(permit));

    final int otherRequestId = REQUEST_ID + 1;
    final Optional<Permit> optionalPermit =
        this.service.findByClientIdAndRequestId(CLIENT_ID, otherRequestId);

    assertThat(optionalPermit).isEmpty();
  }

  @Test
  void testCountByClientId() {
    final Permit permit = this.createPermit();
    final String key = PermitKey.builder().permit(permit).build().key();

    when(this.redissonClient.getKeys()).thenReturn(this.keys);
    when(this.keys.getKeysByPattern(PermitKey.builder().build().keyPattern()))
        .thenAnswer(invocation -> List.of(key));
    when(this.redissonClient.getScoredSortedSet(key)).thenAnswer(invocation -> this.permits);
    when(this.permits.stream()).thenReturn(Stream.of(permit));

    final long count = this.service.countByClientId(CLIENT_ID);

    assertThat(count).isEqualTo(1);
  }

  private void prepareLock(final PermitKey permitKey) {
    when(this.redissonClient.getLock(permitKey.lockId())).thenReturn(this.lock);
  }

  @SneakyThrows
  private void preparePrioLock() {
    when(this.lock.tryLock(100, TimeUnit.MILLISECONDS)).thenReturn(true);
    when(this.lock.isHeldByCurrentThread()).thenReturn(true);
  }

  @SneakyThrows
  private void preparePrioLockForInterruption() {
    when(this.lock.tryLock(100, TimeUnit.MILLISECONDS)).thenThrow(new InterruptedException());
    when(this.lock.isHeldByCurrentThread()).thenReturn(true);
  }

  @SneakyThrows
  private void preparePrioLockForFailure() {
    when(this.lock.tryLock(100, TimeUnit.MILLISECONDS)).thenReturn(false);
    when(this.lock.isHeldByCurrentThread()).thenReturn(false);
  }

  private void prepareScoredSetForGranted(final PermitKey permitKey, final int size) {
    this.prepareScoredSet(permitKey, size, true, true);
  }

  private void prepareScoredSetForNotGranted(final PermitKey permitKey, final int size) {
    this.prepareScoredSet(permitKey, size, true, false);
  }

  private void prepareScoredSetForUnlimitedConcurrency(final PermitKey permitKey) {
    this.prepareScoredSet(permitKey, 42, false, true);
  }

  private void prepareScoredSet(
      final PermitKey permitKey, final int size, final boolean stubSize, final boolean stubAdd) {
    when(this.redissonClient.getScoredSortedSet(permitKey.key()))
        .thenAnswer(invocation -> this.permits);
    if (stubSize) {
      when(this.permits.size()).thenReturn(size);
    }
    if (stubAdd) {
      when(this.permits.add(anyDouble(), any(Permit.class))).thenReturn(true);
    }
  }

  private PermitKey createPermitKey() {
    return PermitKey.builder().networkSegment(NETWORK_SEGMENT).build();
  }

  private Permit createPermit() {
    return new Permit(NETWORK_SEGMENT, CLIENT_ID, REQUEST_ID);
  }
}
