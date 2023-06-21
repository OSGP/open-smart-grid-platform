// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.db.api.iec61850.exceptions;

public class Iec61850CoreDbApiException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -589626721908058277L;

  public Iec61850CoreDbApiException(final String message) {
    super(message);
  }

  public Iec61850CoreDbApiException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
