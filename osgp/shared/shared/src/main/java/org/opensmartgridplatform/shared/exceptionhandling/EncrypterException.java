// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
