// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class EmptyUserNameSoapHeaderException extends EmptySoapHeaderException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -1189166045760569232L;

  public EmptyUserNameSoapHeaderException(final String userName) {
    super("UserName", userName);
  }
}
