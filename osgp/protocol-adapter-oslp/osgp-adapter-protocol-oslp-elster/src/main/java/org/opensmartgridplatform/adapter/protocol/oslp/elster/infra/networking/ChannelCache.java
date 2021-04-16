/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import io.netty.channel.Channel;
import java.time.Clock;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChannelCache.class);

  private final ConcurrentMap<String, ChannelCacheEntry> channelMap = new ConcurrentHashMap<>();
  private final Clock clock;
  private final long expirationMillis;

  public ChannelCache(final long expirationMillis) {
    this(expirationMillis, Clock.systemUTC());
  }

  ChannelCache(final long expirationMillis, final Clock clock) {
    this.clock = Objects.requireNonNull(clock, "clock");
    if (expirationMillis < 0) {
      throw new IllegalArgumentException(
          "expirationMillis must not be negative: " + expirationMillis);
    }
    this.expirationMillis = expirationMillis;
  }

  public void cacheChannel(final Channel channel) {
    final String channelIdAsLongText = channel.id().asLongText();
    final int referenceCount;
    synchronized (this) {
      referenceCount =
          this.channelMap
              .computeIfAbsent(channelIdAsLongText, key -> new ChannelCacheEntry(channel))
              .incrementAndGetReferenceCount(this.clock.millis());
    }
    if (referenceCount == 1) {
      LOGGER.debug("Cached channel {}", channelIdAsLongText);
    } else {
      LOGGER.info(
          "Cached channel {} multiple times (reference count: {})",
          channelIdAsLongText,
          referenceCount);
    }
    LOGGER.debug("Number of cached channels after the last cache: {}", this.channelMap.size());
    this.removeExpiredCacheEntries();
  }

  public Channel removeFromCache(final String channelIdAsLongText) {
    final ChannelCacheEntry channelCacheEntry;
    final int referenceCount;
    synchronized (this) {
      channelCacheEntry = this.channelMap.get(channelIdAsLongText);
      if (channelCacheEntry == null) {
        return null;
      }
      referenceCount = channelCacheEntry.decrementAndGetReferenceCount();
      if (referenceCount == 0) {
        this.channelMap.remove(channelIdAsLongText);
      }
    }
    if (referenceCount == 0) {
      LOGGER.debug("Removed channel from cache: {}", channelIdAsLongText);
    } else {
      LOGGER.info(
          "Keep channel {} in cache (remaining reference count: {})",
          channelIdAsLongText,
          referenceCount);
    }
    LOGGER.debug("Number of cached channels after the last removal: {}", this.channelMap.size());
    return channelCacheEntry.getChannel();
  }

  public void removeExpiredCacheEntries() {
    final long thresholdMillis = this.clock.millis() - this.expirationMillis;
    final Set<String> keysForExpiredEntries =
        this.channelMap.entrySet().stream()
            .filter(entry -> entry.getValue().getLastRefreshedMillis() < thresholdMillis)
            .map(Entry::getKey)
            .collect(Collectors.toSet());
    synchronized (this) {
      keysForExpiredEntries.forEach(
          channelIdAsLongText -> {
            final ChannelCacheEntry removed = this.channelMap.remove(channelIdAsLongText);
            if (removed != null && removed.getLastRefreshedMillis() > thresholdMillis) {
              /*
               * The channel cache entry was refreshed since filtering the
               * keys for expired entries, put it back.
               */
              this.channelMap.put(channelIdAsLongText, removed);
            } else {
              LOGGER.warn(
                  "Removed channel {} from cache after expiration: {}",
                  channelIdAsLongText,
                  removed);
            }
          });
    }
    if (!keysForExpiredEntries.isEmpty()) {
      LOGGER.debug(
          "Number of cached channels after removal of expired entries: {}", this.channelMap.size());
    }
  }

  /** @return the number of cached channels */
  public int size() {
    return this.channelMap.size();
  }
}
