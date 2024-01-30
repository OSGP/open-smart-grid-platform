// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.jasper.exceptions;

import org.opensmartgridplatform.jasper.rest.JasperError;

public class OsgpJasperException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 4296034756159213906L;

  private final JasperError jasperError;

  public OsgpJasperException(final JasperError jasperError) {
    super(jasperError.getCode() + ":" + jasperError.getMessage());
    this.jasperError = jasperError;
  }

  public OsgpJasperException(final String message, final Throwable throwable) {
    super(message, throwable);
    this.jasperError = null;
  }

  public OsgpJasperException(final String message) {
    super(message);
    this.jasperError = null;
  }

  public JasperError getJasperError() {
    return this.jasperError;
  }

  /**
   * Returns the error message from the JasperError if it is not null, otherwise the message from
   * this exception.
   *
   * @return the error message
   */
  public String getErrorMessage() {
    return this.jasperError != null ? this.jasperError.getMessage() : this.getMessage();
  }
}
