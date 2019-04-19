/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.function.Consumer;

public interface ClientConnectionCache {

    /**
     * Get a connection from the cache.
     *
     * @param key
     *            The key of the connection to be retrieved.
     * @return A {@link ClientConnection} instance.
     */
    public ClientConnection getConnection(String key);

    /**
     * Get size.
     *
     * @return The number of items in the cache.
     */
    public int getSize();

    /**
     * Add connection to the cache.
     *
     * @param key
     *            The key of the connection to be added.
     * @param connection
     *            The {@link ClientConnection} instance to be added.
     */
    public void addConnection(String key, ClientConnection connection);

    /**
     * Remove connection from the cache.
     *
     * @param key
     *            The key of the connection to be removed.
     */
    public void removeConnection(String key);

    /**
     * Apply an action to all connections in the cache.
     * 
     * @param action
     *            The action to apply to all connections.
     */
    public void applyToAll(Consumer<String> action);
}
