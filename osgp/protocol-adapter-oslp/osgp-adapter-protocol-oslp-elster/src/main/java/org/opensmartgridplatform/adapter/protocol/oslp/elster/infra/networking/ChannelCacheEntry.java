/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.Channel;

public class ChannelCacheEntry {

    private final Channel channel;
    private int referenceCount;
    private final AtomicLong lastRefreshedMillis = new AtomicLong(0);

    public ChannelCacheEntry(final Channel channel) {
        this.channel = Objects.requireNonNull(channel);
        this.referenceCount = 0;
    }

    public int incrementAndGetReferenceCount() {
        this.lastRefreshedMillis.set(System.currentTimeMillis());
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
        return String.format("ChannelCacheEntry[channel: %s, referenceCount: %d, lastRefreshed: %s]",
                this.channel.id().asShortText(), this.referenceCount,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.getLastRefreshedMillis()), ZoneId.of("UTC")));
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
