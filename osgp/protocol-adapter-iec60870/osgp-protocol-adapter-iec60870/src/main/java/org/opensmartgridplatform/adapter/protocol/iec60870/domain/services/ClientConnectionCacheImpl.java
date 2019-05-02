/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ClientConnectionCacheImpl implements ClientConnectionCache {
    private ConcurrentHashMap<String, ClientConnection> cache = new ConcurrentHashMap<>();

    @Override
    public Collection<ClientConnection> getConnections() {
        return Collections.unmodifiableCollection(this.cache.values());
    }

    @Override
    public ClientConnection getConnection(final String key) {
        return this.cache.get(key);
    }

    /**
     *
     * Adds a connection to the cache.<br/>
     * <br/>
     * <b><i>Warning: Adding a connection using an already existing key will
     * replace the existing connection!</i></b>
     */
    @Override
    public void addConnection(final String key, final ClientConnection connection) {
        this.cache.put(key, connection);
    }

    @Override
    public void removeConnection(final String key) {
        this.cache.remove(key);
    }
}
