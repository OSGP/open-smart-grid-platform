/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class InactiveDeviceException extends PlatformException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -8628972116249878313L;

  private static final String MESSAGE = "Device %1$s is not active in the platform";

  public InactiveDeviceException(final String deviceIdentification) {
    super(String.format(MESSAGE, deviceIdentification));
  }
}
