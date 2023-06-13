// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.ClientConnectionAlreadyInCacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClientConnectionCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnectionCache.class);

  private ConcurrentHashMap<String, ClientConnection> cache = new ConcurrentHashMap<>();

  public Collection<ClientConnection> getConnections() {
    return Collections.unmodifiableCollection(this.cache.values());
  }

  public ClientConnection getConnection(final String key) {
    LOGGER.debug("Fetching connection for device: {}", key);
    return this.cache.get(key);
  }

  public void addConnection(final String key, final ClientConnection connection)
      throws ClientConnectionAlreadyInCacheException {
    LOGGER.debug("Adding connection for device: {}", key);
    final ClientConnection conn = this.cache.putIfAbsent(key, connection);
    if (conn != null) {
      throw new ClientConnectionAlreadyInCacheException(conn);
    }
  }

  public void removeConnection(final String key) {
    LOGGER.debug("Removing connection for device: {}", key);
    this.cache.remove(key);
  }
}
