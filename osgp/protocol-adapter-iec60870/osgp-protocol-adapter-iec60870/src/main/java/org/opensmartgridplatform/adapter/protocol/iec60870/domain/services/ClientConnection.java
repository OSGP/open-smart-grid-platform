// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionParameters;

public interface ClientConnection {
  /**
   * Get the connection parameters.
   *
   * @return a {@link ConnectionParameters} instance.
   */
  ConnectionParameters getConnectionParameters();

  /**
   * Get the connection.
   *
   * @return a {@link Connection} instance.
   */
  Connection getConnection();
}
