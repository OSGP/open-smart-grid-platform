// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.exceptions;

public class SessionProviderUnsupportedException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -5449632007365870329L;

  public SessionProviderUnsupportedException(final String message) {
    super(message);
  }

  public SessionProviderUnsupportedException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
