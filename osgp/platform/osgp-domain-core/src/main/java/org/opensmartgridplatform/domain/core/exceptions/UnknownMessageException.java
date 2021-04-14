/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class UnknownMessageException extends PlatformException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -5443719028089122868L;

  private static final String DEFAULT_MESSAGE = "Unknown message Exception";

  public UnknownMessageException() {
    super(DEFAULT_MESSAGE);
  }

  public UnknownMessageException(final String message) {
    super(message);
  }

  public UnknownMessageException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
