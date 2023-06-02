//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
