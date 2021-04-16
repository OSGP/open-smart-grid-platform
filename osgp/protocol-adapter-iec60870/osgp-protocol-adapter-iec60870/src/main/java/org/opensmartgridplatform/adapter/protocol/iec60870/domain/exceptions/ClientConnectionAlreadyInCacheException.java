/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;

public class ClientConnectionAlreadyInCacheException extends Exception {

  private static final long serialVersionUID = 6960473289090660140L;
  private final transient ClientConnection clientConnection;

  public ClientConnectionAlreadyInCacheException(final ClientConnection clientConnection) {
    this.clientConnection = clientConnection;
  }

  public ClientConnection getClientConnection() {
    return this.clientConnection;
  }
}
