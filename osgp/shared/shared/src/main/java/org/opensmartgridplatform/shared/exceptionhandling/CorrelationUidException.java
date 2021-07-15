/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

public abstract class CorrelationUidException extends FunctionalException {

  private static final long serialVersionUID = 6419833632655339760L;

  public CorrelationUidException(
      final FunctionalExceptionType exceptionType,
      final ComponentType componentType,
      final Throwable cause) {
    super(exceptionType, componentType, cause);
  }

  public CorrelationUidException(
      final FunctionalExceptionType exceptionType, final ComponentType componentType) {
    super(exceptionType, componentType);
  }
}
