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
public class UnregisteredDeviceException extends PlatformException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -8628972116249878312L;

  private static final String MESSAGE = "Device %1$s is not registered";

  public UnregisteredDeviceException(final String deviceIdentification) {
    super(String.format(MESSAGE, deviceIdentification));
  }
}
