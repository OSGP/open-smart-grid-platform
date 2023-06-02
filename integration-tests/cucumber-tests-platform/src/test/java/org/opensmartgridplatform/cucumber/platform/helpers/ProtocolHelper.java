//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.helpers;

import java.util.HashMap;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol.ProtocolType;

public class ProtocolHelper {

  private static Map<Protocol.ProtocolType, Protocol> allProtocols;

  static {
    allProtocols = new HashMap<>();
    allProtocols.put(ProtocolType.OSLP, new Protocol(ProtocolType.OSLP, "OSLP", "1.0"));
    allProtocols.put(ProtocolType.DSMR, new Protocol(ProtocolType.DSMR, "DSMR", "4.2.2"));
    allProtocols.put(ProtocolType.DLMS, new Protocol(ProtocolType.DLMS, "DLMS", "1.0"));
    allProtocols.put(
        ProtocolType.IEC60870, new Protocol(ProtocolType.IEC60870, "60870-5-104", "1.0"));
  }

  public static Map<Protocol.ProtocolType, Protocol> getAllProtocols() {
    return allProtocols;
  }

  public static Protocol getProtocol(final Protocol.ProtocolType type) {
    return allProtocols.get(type);
  }
}
