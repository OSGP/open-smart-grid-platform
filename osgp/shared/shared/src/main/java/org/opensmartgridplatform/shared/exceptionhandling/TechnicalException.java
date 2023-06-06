// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.exceptionhandling;

public class TechnicalException extends OsgpException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 215662983108393459L;

  public TechnicalException() {
    super(ComponentType.UNKNOWN, null, null);
  }

  public TechnicalException(final String message) {
    super(ComponentType.UNKNOWN, message, null);
  }

  public TechnicalException(final String message, final Throwable cause) {
    super(ComponentType.UNKNOWN, message, cause);
  }

  public TechnicalException(final ComponentType componentType, final String message) {
    super(componentType, message, null);
  }

  public TechnicalException(final ComponentType componentType, final Throwable cause) {
    this(componentType, null, cause);
  }

  public TechnicalException(
      final ComponentType componentType, final String message, final Throwable cause) {
    super(componentType, message, cause);
  }
}
