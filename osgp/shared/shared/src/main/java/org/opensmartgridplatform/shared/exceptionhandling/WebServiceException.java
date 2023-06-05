// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.exceptionhandling;

public class WebServiceException extends TechnicalException {

  /** Serial Version UID */
  private static final long serialVersionUID = 1L;

  public WebServiceException() {
    super();
  }

  public WebServiceException(final String message) {
    super(message);
  }

  public WebServiceException(final String message, final Throwable t) {
    super(message, t);
  }
}
