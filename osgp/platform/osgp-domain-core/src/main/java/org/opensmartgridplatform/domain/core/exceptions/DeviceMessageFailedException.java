// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class DeviceMessageFailedException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 2589780904277543380L;

  private static final String MESSAGE = "Device Message Failed";

  public DeviceMessageFailedException() {
    super(MESSAGE);
  }
}
