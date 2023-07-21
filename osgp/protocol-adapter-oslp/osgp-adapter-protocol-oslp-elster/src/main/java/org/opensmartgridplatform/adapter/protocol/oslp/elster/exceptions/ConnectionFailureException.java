// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions;

public class ConnectionFailureException extends ProtocolAdapterException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 916943696172403469L;

  public ConnectionFailureException(final String message) {
    super(message);
  }

  public ConnectionFailureException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
