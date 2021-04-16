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
public class DeviceMessageRejectedException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -5494355964203067084L;

  private static final String MESSAGE = "Device Message Rejected";

  public DeviceMessageRejectedException() {
    super(MESSAGE);
  }
}
