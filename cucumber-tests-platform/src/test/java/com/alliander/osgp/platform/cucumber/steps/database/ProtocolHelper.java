package com.alliander.osgp.platform.cucumber.steps.database;

import java.util.HashMap;
import java.util.Map;

import com.alliander.osgp.platform.cucumber.steps.database.Protocol.ProtocolType;

public class ProtocolHelper {


    public static Map<Protocol.ProtocolType, Protocol> getAllProtocols() {
        return allProtocols;
    }

    private static Map<Protocol.ProtocolType, Protocol> allProtocols;

    static {
        allProtocols = new HashMap<ProtocolType, Protocol>();
        allProtocols.put(ProtocolType.OSLP, new Protocol(ProtocolType.OSLP, "OSLP", "1.0"));
        allProtocols.put(ProtocolType.DSMR, new Protocol(ProtocolType.DSMR, "DSMR", "4.2.2"));
        allProtocols.put(ProtocolType.DLMS, new Protocol(ProtocolType.DLMS, "DLMS", "1.0"));
    }

    public static Protocol getProtocol(final Protocol.ProtocolType type) {
        return allProtocols.get(type);
    }
}
