/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

public class EncrypterException extends RuntimeException {

  private static final long serialVersionUID = 215662983108393459L;

  public EncrypterException(final String message) {
    super(message);
  }

  public EncrypterException(final Throwable cause) {
    super(cause);
  }

  public EncrypterException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
