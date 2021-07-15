/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.util.HashSet;
import java.util.Set;
import org.openmuc.j60870.Connection;
import org.springframework.stereotype.Component;

@Component
public class Iec60870ConnectionRegistry {
  private Set<Connection> connections = new HashSet<>();

  public Set<Connection> getAllConnections() {
    return new HashSet<>(this.connections);
  }

  public void registerConnection(final Connection connection) {
    this.connections.add(connection);
  }

  public void unregisterConnection(final Connection connection) {
    this.connections.remove(connection);
  }
}
