/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

public class FunctionalException extends OsgpException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 2879663396838174171L;

  private final FunctionalExceptionType exceptionType;

  public FunctionalException(
      final FunctionalExceptionType exceptionType, final ComponentType componentType) {
    this(exceptionType, componentType, null);
  }

  public FunctionalException(
      final FunctionalExceptionType exceptionType,
      final ComponentType componentType,
      final Throwable cause) {
    super(componentType, exceptionType.getMessage(), cause);
    this.exceptionType = exceptionType;
  }

  public Integer getCode() {
    return this.exceptionType == null ? null : this.exceptionType.getCode();
  }

  public FunctionalExceptionType getExceptionType() {
    return this.exceptionType;
  }
}
