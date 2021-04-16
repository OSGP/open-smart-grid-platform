/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

/**
 * Exception to be thrown when the correlationUid does exist, but is not coupled to the expected
 * (type of) data.
 */
public class CorrelationUidMismatchException extends CorrelationUidException {

  private static final String MESSAGE =
      "CorrelationUid does not match any data of the requested type.";
  private static final long serialVersionUID = -2029350937138219841L;

  public CorrelationUidMismatchException(final ComponentType componentType, final Throwable cause) {
    super(FunctionalExceptionType.VALIDATION_ERROR, componentType, cause);
  }

  public CorrelationUidMismatchException(final ComponentType componentType) {
    super(FunctionalExceptionType.VALIDATION_ERROR, componentType);
  }

  @Override
  public String getMessage() {
    return MESSAGE;
  }
}
