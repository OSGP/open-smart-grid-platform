//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class EmptySoapHeaderException extends PlatformException {
  private static final long serialVersionUID = 2268481456629745898L;

  public EmptySoapHeaderException(final String name, final String value) {
    super(messageFor(name, value));
  }

  private static String messageFor(final String name, final String value) {
    if (value == null) {
      return name + " Soap Header is missing";
    }
    return String.format(name + " Soap Header is \"%s\"", value);
  }
}
