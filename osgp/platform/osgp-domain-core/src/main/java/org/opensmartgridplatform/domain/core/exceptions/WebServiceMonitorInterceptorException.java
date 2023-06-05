// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.exceptions;

public class WebServiceMonitorInterceptorException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -1849410890203685223L;

  public WebServiceMonitorInterceptorException() {
    super();
  }

  public WebServiceMonitorInterceptorException(final String message) {
    super(message);
  }

  public WebServiceMonitorInterceptorException(final String message, final Throwable t) {
    super(message, t);
  }
}
