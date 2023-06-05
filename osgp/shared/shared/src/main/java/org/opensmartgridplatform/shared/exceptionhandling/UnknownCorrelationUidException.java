// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.exceptionhandling;

/** Exception to throw when the correlationUid does not exist. */
public class UnknownCorrelationUidException extends CorrelationUidException {
  private static final long serialVersionUID = -6660221999519254432L;
  public static final String CORRELATION_UID_IS_UNKNOWN = "CorrelationUid is unknown.";

  public UnknownCorrelationUidException(final ComponentType componentType, final Throwable cause) {
    super(FunctionalExceptionType.UNKNOWN_CORRELATION_UID, componentType, cause);
  }

  public UnknownCorrelationUidException(final ComponentType componentType) {
    super(FunctionalExceptionType.UNKNOWN_CORRELATION_UID, componentType);
  }

  @Override
  public String getMessage() {
    return CORRELATION_UID_IS_UNKNOWN;
  }
}
