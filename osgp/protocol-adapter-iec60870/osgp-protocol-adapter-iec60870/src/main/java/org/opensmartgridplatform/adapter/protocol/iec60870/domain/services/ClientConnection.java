/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
