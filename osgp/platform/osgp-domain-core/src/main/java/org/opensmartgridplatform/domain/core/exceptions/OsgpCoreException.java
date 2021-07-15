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
public class OsgpCoreException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 2714612190124325351L;

  public OsgpCoreException(final String message) {
    super(message);
  }

  public OsgpCoreException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
