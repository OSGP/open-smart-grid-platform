/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import static org.assertj.core.api.Assertions.assertThat;

import io.netty.channel.Channel;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ChannelCacheTest {

  private static final int EXPIRATION_MILLIS = 100;

  private ControlledClock clock = new ControlledClock(ZoneOffset.UTC, System.currentTimeMillis());
  private ChannelCache channelCache = new ChannelCache(EXPIRATION_MILLIS, this.clock);

  private AtomicInteger channelIdCounter = new AtomicInteger(0);

  @Test
  void cachedChannelsCanBeRetrieved() {
    final List<Channel> channels =
        Arrays.asList(this.newChannel(), this.newChannel(), this.newChannel(), this.newChannel());
    for (final Channel channel : channels) {
      this.channelCache.cacheChannel(channel);
    }
    for (final Channel channel : channels) {
      final Channel fromCache = this.channelCache.removeFromCache(channel.id().asLongText());
      assertThat(fromCache).isEqualTo(channel);
    }
  }

  @Test
  void aChannelCanBeRetrievedAsManyTimesAsItIsCached() {
    final int numberOfTimesCached = 3;
    final Channel channel = this.newChannel();
    final String channelIdAsLongText = channel.id().asLongText();
    for (int i = 0; i < numberOfTimesCached; i++) {
      this.channelCache.cacheChannel(channel);
    }
    for (int i = 0; i < numberOfTimesCached; i++) {
      final Channel fromCache = this.channelCache.removeFromCache(channelIdAsLongText);
      assertThat(fromCache).isEqualTo(channel);
    }
    final Channel fromCache = this.channelCache.removeFromCache(channelIdAsLongText);
    assertThat(fromCache).isNull();
  }

  @Test
  void aCachedChannelCanNoLongerBeRetrievedOnceAnotherChannelIsCachedAfterTheExpirationTime()
      throws Exception {
    final Channel channel1 = this.newChannel();
    final Channel channel2 = this.newChannel();

    this.whenAChannelIsCached(channel1);
    this.whenMoreTimePassesThanTheExpirationTime();
    this.whenAChannelIsCached(channel2);

    final Channel fromCache = this.channelCache.removeFromCache(channel1.id().asLongText());
    assertThat(fromCache).isNull();
  }

  @Test
  void allEarlierCachedChannelsAreRemovedOnceAnotherChannelIsCachedAfterTheExpirationTime()
      throws Exception {
    final List<Channel> channels =
        Arrays.asList(this.newChannel(), this.newChannel(), this.newChannel(), this.newChannel());
    for (final Channel channel : channels) {
      this.whenAChannelIsCached(channel);
    }

    final Channel remainingChannel = this.newChannel();
    this.whenMoreTimePassesThanTheExpirationTime();
    this.whenAChannelIsCached(remainingChannel);

    assertThat(this.channelCache.removeFromCache(remainingChannel.id().asLongText())).isNotNull();
    assertThat(this.channelCache.size()).isZero();
  }

  @Test
  void aChannelCachedMultipleTimesExpiresBasedOnTheLastCacheTime() throws Exception {
    final Channel channel1 = this.newChannel();
    final Channel channel2 = this.newChannel();

    this.whenAChannelIsCached(channel1);
    this.whenAChannelIsCached(channel2);
    this.whenMoreTimePassesThanTheExpirationTime();
    this.whenAChannelIsCached(channel1);

    assertThat(this.channelCache.removeFromCache(channel2.id().asLongText()))
        .as("channel2 should no longer be cached")
        .isNull();
    final String channelId1AsLongText = channel1.id().asLongText();
    assertThat(this.channelCache.removeFromCache(channelId1AsLongText))
        .as("channel1 should be cached")
        .isEqualTo(channel1);
    assertThat(this.channelCache.removeFromCache(channelId1AsLongText))
        .as("channel1 should be cached twice")
        .isEqualTo(channel1);
  }

  @Test
  void sizeReturnsTheNumberOfCachedChannels() {
    final List<Channel> channels =
        Arrays.asList(this.newChannel(), this.newChannel(), this.newChannel(), this.newChannel());

    for (final Channel channel : channels) {
      this.channelCache.cacheChannel(channel);
    }

    assertThat(this.channelCache.size()).isEqualTo(channels.size());
  }

  @Test
  void aChannelThatIsAddedMultipleTimesCountsAsOneForTheSize() {
    final Channel channel = this.newChannel();

    this.channelCache.cacheChannel(channel);
    this.channelCache.cacheChannel(channel);

    assertThat(this.channelCache.size()).isEqualTo(1);
  }

  private Channel newChannel() {
    final int counter = this.channelIdCounter.incrementAndGet();
    return new TestableChannel(
        null, TestableChannel.id(String.valueOf(counter), String.format("channel-%d", counter)));
  }

  private void whenAChannelIsCached(final Channel channel) {
    this.channelCache.cacheChannel(channel);
    this.clock.tick();
  }

  private void whenMoreTimePassesThanTheExpirationTime() {
    this.clock.advanceMillis(EXPIRATION_MILLIS + 1);
  }
}
