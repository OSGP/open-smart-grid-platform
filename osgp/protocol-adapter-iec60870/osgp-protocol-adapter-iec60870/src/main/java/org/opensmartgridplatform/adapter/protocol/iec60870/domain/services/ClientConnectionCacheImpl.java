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

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.ClientConnectionAlreadyInCacheException;
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

    @Override
    public void addConnection(final String key, final ClientConnection connection)
            throws ClientConnectionAlreadyInCacheException {
        final ClientConnection conn = this.cache.putIfAbsent(key, connection);
        if (conn != null) {
            throw new ClientConnectionAlreadyInCacheException(conn);
        }
    }

    @Override
    public void removeConnection(final String key) {
        this.cache.remove(key);
    }
}
