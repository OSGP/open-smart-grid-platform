//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
