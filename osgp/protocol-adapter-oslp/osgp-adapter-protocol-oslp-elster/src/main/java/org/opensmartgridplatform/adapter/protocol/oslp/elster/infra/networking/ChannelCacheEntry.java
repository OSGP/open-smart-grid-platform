//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import io.netty.channel.Channel;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelCacheEntry {

  private final Channel channel;
  private int referenceCount;
  private final AtomicLong lastRefreshedMillis = new AtomicLong(0);

  public ChannelCacheEntry(final Channel channel) {
    this.channel = Objects.requireNonNull(channel);
    this.referenceCount = 0;
  }

  public int incrementAndGetReferenceCount(final long updateTimeMillis) {
    this.lastRefreshedMillis.set(updateTimeMillis);
    synchronized (this) {
      this.referenceCount += 1;
      return this.referenceCount;
    }
  }

  public int decrementAndGetReferenceCount() {
    synchronized (this) {
      if (this.referenceCount > 0) {
        this.referenceCount -= 1;
      }
      return this.referenceCount;
    }
  }

  public Channel getChannel() {
    return this.channel;
  }

  public int getReferenceCount() {
    synchronized (this) {
      return this.referenceCount;
    }
  }

  public long getLastRefreshedMillis() {
    return this.lastRefreshedMillis.get();
  }

  public String key() {
    return this.channel.id().asLongText();
  }

  @Override
  public String toString() {
    return String.format(
        "ChannelCacheEntry[channel: %s, referenceCount: %d, lastRefreshed: %s]",
        this.channel.id().asShortText(),
        this.referenceCount,
        ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(this.getLastRefreshedMillis()), ZoneOffset.UTC));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ChannelCacheEntry)) {
      return false;
    }
    final ChannelCacheEntry other = (ChannelCacheEntry) obj;
    return Objects.equals(this.key(), other.key());
  }

  @Override
  public int hashCode() {
    return this.key().hashCode();
  }
}
