/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
    }

    public static Map<Protocol.ProtocolType, Protocol> getAllProtocols() {
        return allProtocols;
    }

    public static Protocol getProtocol(final Protocol.ProtocolType type) {
        return allProtocols.get(type);
    }
}
