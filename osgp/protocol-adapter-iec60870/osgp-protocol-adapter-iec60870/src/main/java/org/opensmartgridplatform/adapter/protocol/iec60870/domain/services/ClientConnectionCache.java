/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.Collection;

public interface ClientConnectionCache {

    /**
     * Gets all connections from the cache.
     *
     * @return A {@link Collection} of {@link ClientConnection} instances.
     */
    public Collection<ClientConnection> getConnections();

    /**
     * Gets a connection from the cache.
     *
     * @param key
     *            The key of the connection to be retrieved.
     * @return A {@link ClientConnection} instance or null when no connection is
     *         found.
     */
    public ClientConnection getConnection(String key);

    /**
     * Adds a connection to the cache.
     *
     * @param key
     *            The key of the connection to be added.
     * @param connection
     *            The {@link ClientConnection} instance to be added.
     */
    public void addConnection(String key, ClientConnection connection);

    /**
     * Removes a connection from the cache.
     *
     * @param key
     *            The key of the connection to be removed.
     */
    public void removeConnection(String key);
}
