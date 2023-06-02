//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;

public class DeviceConnection implements ClientConnection {

  private final ConnectionParameters connectionParameters;
  private final Connection connection;

  public DeviceConnection(
      final Connection connection, final ConnectionParameters connectionParameters) {
    this.connection = connection;
    this.connectionParameters = connectionParameters;
  }

  @Override
  public ConnectionParameters getConnectionParameters() {
    return this.connectionParameters;
  }

  @Override
  public Connection getConnection() {
    return this.connection;
  }
}
