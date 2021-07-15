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
public class InvalidFirmwareIdentificationException extends PlatformException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -2033954329227850371L;

  private static final String MESSAGE_FORMAT = "Invalid firmware identification: [%s]";

  public InvalidFirmwareIdentificationException(final String identification) {
    super(String.format(MESSAGE_FORMAT, identification));
  }
}
