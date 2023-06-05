// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

public class FirmwareImageFactoryException extends Exception {

  private static final long serialVersionUID = 1L;

  public FirmwareImageFactoryException() {
    super();
  }

  public FirmwareImageFactoryException(
      final String message,
      final Throwable cause,
      final boolean enableSuppression,
      final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public FirmwareImageFactoryException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public FirmwareImageFactoryException(final String message) {
    super(message);
  }

  public FirmwareImageFactoryException(final Throwable cause) {
    super(cause);
  }
}
