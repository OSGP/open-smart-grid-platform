//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.exceptionhandling;

public class WebServiceSecurityException extends WebServiceException {

  /** Serial Version UID */
  private static final long serialVersionUID = 3924185256176233897L;

  public WebServiceSecurityException(final String message, final Throwable t) {
    super(message, t);
  }

  public WebServiceSecurityException(final String message) {
    super(message);
  }
}
