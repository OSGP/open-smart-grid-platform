//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

public enum CommunicationMethod {
  GPRS("GPRS"),
  CDMA("CDMA"),
  LTE("LTE");

  private final String methodName;

  CommunicationMethod(final String methodName) {
    this.methodName = methodName;
  }

  public String getMethodName() {
    return this.methodName;
  }

  public static CommunicationMethod getCommunicationMethod(final String method)
      throws ProtocolAdapterException {
    switch (method) {
      case "GPRS":
        return GPRS;
      case "CDMA":
        return CDMA;
      case "LTE":
        return LTE;
      default:
        throw new ProtocolAdapterException("Unknown communication method: " + method);
    }
  }
}
