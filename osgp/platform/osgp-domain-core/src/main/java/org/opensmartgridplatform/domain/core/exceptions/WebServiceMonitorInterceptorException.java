/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
