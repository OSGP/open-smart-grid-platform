//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class InvalidDeviceIdentificationException extends Exception {

  /** Serial Verion UID. */
  private static final long serialVersionUID = 4766085145882763249L;

  private static final String MESSAGE = "Invalid Device Identification";

  public InvalidDeviceIdentificationException() {
    super(MESSAGE);
  }
}
