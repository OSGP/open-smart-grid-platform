//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.usermanagement;

/**
 * Generic exception class for exceptions constructing or using web clients. Specific clients may
 * subclass this to provide more precise exceptions.
 */
public class WebClientException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 1214838735315997572L;

  public WebClientException(final String message) {
    super(message);
  }

  public WebClientException(final String message, final Throwable t) {
    super(message, t);
  }
}
