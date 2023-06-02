//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.signing.server.domain.exceptions;

public class SigningServerException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -1461900304884232355L;

  public SigningServerException() {
    super();
  }

  public SigningServerException(final String message) {
    super(message);
  }

  public SigningServerException(final String message, final Throwable t) {
    super(message, t);
  }
}
