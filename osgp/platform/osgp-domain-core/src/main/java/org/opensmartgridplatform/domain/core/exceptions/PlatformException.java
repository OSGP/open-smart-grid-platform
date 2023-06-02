//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class PlatformException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 2879663396838174171L;

  public PlatformException(final String message) {
    super(message);
  }

  public PlatformException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
