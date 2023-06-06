// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

public class RecoverKeyException extends RuntimeException {

  private static final long serialVersionUID = -837112027051155414L;

  public RecoverKeyException() {
    super();
  }

  public RecoverKeyException(
      final String message,
      final Throwable cause,
      final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public RecoverKeyException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public RecoverKeyException(final String message) {
    super(message);
  }

  public RecoverKeyException(final Throwable cause) {
    super(cause);
  }
}
