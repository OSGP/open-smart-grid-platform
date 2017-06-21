/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.wago;

import org.openmuc.openiec61850.ServerModel;

public class WagoServerField {
    private final ServerModel serverModel;
    private final WagoField wagoField;
    private final String serverName;

    public WagoServerField(final ServerModel serverModel, final WagoField wagoField) {
        this.serverModel = serverModel;
        this.serverName = serverModel.getChildren().stream().findFirst().get().getName();
        this.wagoField = wagoField;
    }

    public ServerModel getServerModel() {
        return this.serverModel;
    }

    public WagoField getWagoField() {
        return this.wagoField;
    }

    public String getServerName() {
        return this.serverName;
    }
}
