// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
