// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
