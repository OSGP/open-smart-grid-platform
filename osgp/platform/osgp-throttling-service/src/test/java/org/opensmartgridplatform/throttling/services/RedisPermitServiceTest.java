package org.opensmartgridplatform.throttling.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
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
import org.opensmartgridplatform.throttling.model.PermitRequest;
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
  private static final PermitRequest PERMIT_REQUEST = new PermitRequest(CLIENT_ID, REQUEST_ID);

  @Mock private RedissonClient redissonClient;
  @Mock private RLock lock;
  @Mock private RScoredSortedSet<Permit> permits;
  @Mock private RScoredSortedSet<PermitRequest> lobby;
  @Mock private RKeys keys;

  private RedisPermitService service;

  @BeforeEach
  void setup() {
    this.service = new RedisPermitService(this.redissonClient, Duration.ZERO, 100, 10_000);
  }

  @Test
  void testCreatePermit() {
    final int maxConcurrentRequests = 1;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePermitsSet(permitKey, 0, true);
    this.prepareLobby(permitKey);

    final boolean created =
        this.service.createPermit(NETWORK_SEGMENT, PERMIT_REQUEST, maxConcurrentRequests, false);

    assertThat(created).isTrue();
    verify(this.lock, times(1)).unlock();
  }

  @Test
  void testCreateHighPrioPermit() {
    final int maxConcurrentRequests = 1;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePermitsSet(permitKey, 0, true);
    this.prepareLobby(permitKey);

    final boolean created =
        this.service.createPermit(NETWORK_SEGMENT, PERMIT_REQUEST, maxConcurrentRequests, true);

    assertThat(created).isTrue();
    //    verify(this.lobby, times(1)).addIfAbsent(anyDouble(), eq(PERMIT_REQUEST));
    verify(this.lobby, times(1)).remove(PERMIT_REQUEST);
    verify(this.lock, times(1)).unlock();
  }

  @Test
  void testCreateHighPrioPermitWithMaxConcurrencyReached() {
    final int maxConcurrentRequests = 1;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePermitsSet(permitKey, 1, false);
    this.prepareLobby(permitKey);

    final boolean created =
        this.service.createPermit(NETWORK_SEGMENT, PERMIT_REQUEST, maxConcurrentRequests, true);

    assertThat(created).isFalse();
    verify(this.lobby, times(1)).addIfAbsent(anyDouble(), eq(PERMIT_REQUEST));
    verify(this.lock, times(1)).unlock();
  }

  @Test
  void testCreatePermitWhileHighPrioIsInLobby() {
    final int maxConcurrentRequests = 2;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePermitsSet(permitKey, 0, false);
    this.prepareLobby(permitKey, true);

    final boolean created =
        this.service.createPermit(NETWORK_SEGMENT, PERMIT_REQUEST, maxConcurrentRequests, false);

    assertThat(created).isFalse();
    verify(this.lock, times(1)).unlock();
  }

  @Test
  void testCreatePermitMaxConcurrencyReached() {
    final int maxConcurrentRequests = 1;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePermitsSet(permitKey, 1, false);
    this.prepareLobby(permitKey);

    final boolean created =
        this.service.createPermit(NETWORK_SEGMENT, PERMIT_REQUEST, maxConcurrentRequests, false);

    assertThat(created).isFalse();
    verify(this.lock, times(1)).unlock();
  }

  @Test
  void testCreatePermitMaxConcurrencyUnlimited() {
    final int maxConcurrentRequests = -1;

    final PermitKey permitKey = this.createPermitKey();
    this.prepareLock(permitKey);
    this.preparePermitsSet(permitKey, 0, true);
    this.prepareLobby(permitKey);

    final boolean created =
        this.service.createPermit(NETWORK_SEGMENT, PERMIT_REQUEST, maxConcurrentRequests, false);

    assertThat(created).isTrue();
    verify(this.lock, times(1)).unlock();
  }

  @Test
  void testRemovePermit() {
    final PermitKey permitKey = this.createPermitKey();
    when(this.redissonClient.getScoredSortedSet(permitKey.key()))
        .thenAnswer(invocation -> this.permits);
    when(this.permits.stream()).thenReturn(Stream.of(this.createPermit()));
    when(this.permits.remove(this.createPermit())).thenReturn(true);

    final boolean removed = this.service.removePermit(NETWORK_SEGMENT, PERMIT_REQUEST);

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
    assertThat(optionalPermit.get().permitRequest().getClientId()).isEqualTo(CLIENT_ID);
    assertThat(optionalPermit.get().permitRequest().getRequestId()).isEqualTo(REQUEST_ID);
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

  @SneakyThrows
  private void prepareLock(final PermitKey permitKey) {
    when(this.redissonClient.getLock(permitKey.lockId())).thenReturn(this.lock);
    when(this.lock.tryLock(100, TimeUnit.MILLISECONDS)).thenReturn(true);
  }

  private void preparePermitsSet(final PermitKey permitKey, final int size, final boolean stubAdd) {
    when(this.redissonClient.getScoredSortedSet(permitKey.key()))
        .thenAnswer(invocation -> this.permits);
    when(this.permits.size()).thenReturn(size);

    if (stubAdd) {
      when(this.permits.add(anyDouble(), any(Permit.class))).thenReturn(true);
    }
  }

  private void prepareLobby(final PermitKey permitKey) {
    this.prepareLobby(permitKey, false);
  }

  private void prepareLobby(final PermitKey permitKey, final boolean isOccupied) {
    when(this.redissonClient.getScoredSortedSet(permitKey.lobby()))
        .thenAnswer(invocation -> this.lobby);
    when(this.lobby.isEmpty()).thenReturn(!isOccupied);
  }

  private PermitKey createPermitKey() {
    return PermitKey.builder().networkSegment(NETWORK_SEGMENT).build();
  }

  private Permit createPermit() {
    return new Permit(NETWORK_SEGMENT, PERMIT_REQUEST);
  }
}
