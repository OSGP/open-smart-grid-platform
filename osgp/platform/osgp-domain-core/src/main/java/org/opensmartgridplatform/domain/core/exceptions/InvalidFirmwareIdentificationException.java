//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
