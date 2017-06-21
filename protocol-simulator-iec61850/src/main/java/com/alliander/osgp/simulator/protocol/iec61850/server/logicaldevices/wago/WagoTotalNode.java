/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.wago;

public class WagoTotalNode extends WagoNode {
    private static final String MAGNITUDE_TOTAL_NODE = ".mag.f";

    public WagoTotalNode(final WagoServerField wagoServerField, final String nodeId, final WagoNode node1, final WagoNode node2, final WagoNode node3) {
        super(wagoServerField, nodeId, node1.getValue() + node2.getValue() + node3.getValue());
    }

    @Override
    protected String getMagnitudeNodeName() {
        return this.getServerName() + "/" + this.getFieldId() + "." + this.getNodeId() + MAGNITUDE_TOTAL_NODE;
    }
}
