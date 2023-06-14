// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class EmptyApplicationNameSoapHeaderException extends EmptySoapHeaderException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 8458784656352710077L;

  public EmptyApplicationNameSoapHeaderException(final String applicationName) {
    super("ApplicationName", applicationName);
  }
}
