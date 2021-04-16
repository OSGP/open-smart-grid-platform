/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
