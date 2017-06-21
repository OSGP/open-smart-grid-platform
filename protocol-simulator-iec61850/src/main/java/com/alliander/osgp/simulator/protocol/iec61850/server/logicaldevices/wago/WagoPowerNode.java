/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.wago;

public class WagoPowerNode extends WagoNode {
    public WagoPowerNode(final WagoServerField wagoServerField, final String nodeId, final WagoCurrentNode wagoCurrentNode, final WagoVoltageNode wagoVoltageNode, final double phi) {
        super(wagoServerField, nodeId, generatePowerValue(wagoCurrentNode, wagoVoltageNode, phi));
    }

    private static double generatePowerValue(final WagoCurrentNode wagoCurrentNode, final WagoVoltageNode wagoVoltageNode, final double phi) {
        return wagoCurrentNode.getValue() * wagoVoltageNode.getValue() * Math.cos(phi);
    }
}
