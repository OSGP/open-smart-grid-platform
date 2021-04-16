/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
