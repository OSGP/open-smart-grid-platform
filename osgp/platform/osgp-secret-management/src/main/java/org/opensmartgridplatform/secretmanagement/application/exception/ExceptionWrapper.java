//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.secretmanagement.application.exception;

/**
 * Unchecked exception that wraps another (checked) exception. Can be used to handle checked
 * exception in streams.
 */
public class ExceptionWrapper extends RuntimeException {
  private static final long serialVersionUID = -1239332310446200862L;

  public ExceptionWrapper(final Exception exc) {
    super(exc);
  }
}
