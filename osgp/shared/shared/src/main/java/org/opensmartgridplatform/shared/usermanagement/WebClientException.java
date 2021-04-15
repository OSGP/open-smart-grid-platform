/*
 * Copyright 2014 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
