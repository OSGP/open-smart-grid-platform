//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.exceptions;

import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ConnectionState;

/** This is a base class for {@link NodeReadException} and {@link NodeWriteException}. */
public class NodeException extends ProtocolAdapterException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 664453423321884118L;

  private final ConnectionState connectionState;

  public NodeException(final String message) {
    super(message);
    this.connectionState = ConnectionState.UNKNOWN;
  }

  public NodeException(final String message, final Throwable throwable) {
    super(message, throwable);
    this.connectionState = ConnectionState.UNKNOWN;
  }

  public NodeException(
      final String message, final Throwable throwable, final ConnectionState connectionState) {
    super(message, throwable);
    this.connectionState = connectionState;
  }

  public ConnectionState getConnectionState() {
    return this.connectionState;
  }
}
