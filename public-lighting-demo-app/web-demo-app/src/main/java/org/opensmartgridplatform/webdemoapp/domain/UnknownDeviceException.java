// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdemoapp.domain;

/** Custom exception to handle unknown devices. */
public class UnknownDeviceException extends Throwable {

  private static final long serialVersionUID = 1L;

  private final String soapFaultMessage;

  public UnknownDeviceException(final String faultStringOrReason) {
    this.soapFaultMessage = faultStringOrReason;
  }

  public String getSoapFaultMessage() {
    return this.soapFaultMessage;
  }
}
