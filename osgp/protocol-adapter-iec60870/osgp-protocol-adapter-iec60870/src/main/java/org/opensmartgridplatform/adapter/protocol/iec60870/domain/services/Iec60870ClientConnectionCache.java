/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class Iec60870ClientConnectionCache implements ClientConnectionCache {
    private static final int PARALLELLISM_THRESHOLD = 1;
    private ConcurrentHashMap<String, ClientConnection> cache = new ConcurrentHashMap<>();

    @Override
    public ClientConnection getConnection(final String key) {
        return this.cache.get(key);
    }

    @Override
    public int getSize() {
        return this.cache.size();
    }

    @Override
    public void addConnection(final String key, final ClientConnection connection) {
        this.cache.put(key, connection);
    }

    @Override
    public void removeConnection(final String key) {
        this.cache.remove(key);
    }

    @Override
    public void applyToAll(final Consumer<String> action) {
        this.cache.forEachKey(PARALLELLISM_THRESHOLD, action);
    }
}
