//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.db.api.exceptions;

public class CoreDbApiException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -589626721908058277L;

  public CoreDbApiException(final String message) {
    super(message);
  }

  public CoreDbApiException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
