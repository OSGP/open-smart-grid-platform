/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.domain.core.entities.ProtocolInfo;

public class ProtocolInfoTestUtils {

    // PROTOCOL INFOS
    protected static final String OSLP_1_0_PROTOCOL = "OSLP";
    protected static final String OSLP_1_0_PROTOCOL_VERSION = "1.0";
    protected static final String OSLP_1_0_OUTGOING_REQUESTS_QUEUE = "osgp-test.protocol-oslp.1_0.osgp-core.1_0.requests";
    protected static final String OSLP_1_0_INCOMING_RESPONSES_QUEUE = "osgp-test.osgp-core.1_0.protocol-oslp.1_0.responses";
    protected static final String OSLP_1_0_INCOMING_REQUESTS_QUEUE = "osgp-test.osgp-core.1_0.protocol-oslp.1_0.requests";
    protected static final String OSLP_1_0_OUTGOING_RESPONSES_QUEUE = "osgp-test.protocol-oslp.1_0.osgp-core.1_0.responses";

    private static Map<String, ProtocolInfo> protocolInfos = initProtocolInfos();

    public static List<ProtocolInfo> getProtocolInfos() {
        return new ArrayList<ProtocolInfo>(protocolInfos.values());
    }

    public static ProtocolInfo getProtocolInfo(final String protocol, final String protocolVersion) {
        return protocolInfos.get(ProtocolInfo.getKey(protocol, protocolVersion));
    }

    private static Map<String, ProtocolInfo> initProtocolInfos() {
        final Map<String, ProtocolInfo> protocolInfos = new HashMap<>();
        final ProtocolInfo protocolInfo = new ProtocolInfo(OSLP_1_0_PROTOCOL, OSLP_1_0_PROTOCOL_VERSION,
                OSLP_1_0_OUTGOING_REQUESTS_QUEUE, OSLP_1_0_INCOMING_RESPONSES_QUEUE, OSLP_1_0_INCOMING_REQUESTS_QUEUE,
                OSLP_1_0_OUTGOING_RESPONSES_QUEUE);
        protocolInfos.put(protocolInfo.getKey(), protocolInfo);

        return protocolInfos;
    }
}
