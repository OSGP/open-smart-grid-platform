// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

public class ConnectionException extends RuntimeException {
  private static final long serialVersionUID = -4527258679522467801L;
  private final FunctionalExceptionType type;

  public ConnectionException(
      final String message,
      final Throwable cause,
      final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.type = FunctionalExceptionType.CONNECTION_ERROR;
  }

  public ConnectionException(
      final String message, final Throwable cause, final FunctionalExceptionType type) {
    super(message, cause);
    this.type = type;
  }

  public ConnectionException(final String message, final Throwable cause) {
    super(message, cause);
    this.type = FunctionalExceptionType.CONNECTION_ERROR;
  }

  public ConnectionException(final String message) {
    super(message);
    this.type = FunctionalExceptionType.CONNECTION_ERROR;
  }

  public ConnectionException(final Throwable cause) {
    super(cause);
    this.type = FunctionalExceptionType.CONNECTION_ERROR;
  }

  public FunctionalExceptionType getType() {
    return this.type;
  }
}
