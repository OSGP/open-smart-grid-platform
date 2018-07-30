/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

import org.openmuc.openiec61850.ServerModel;

public class LogicalDeviceNode {
    private final ServerModel serverModel;
    private final String serverName;

    public LogicalDeviceNode(final ServerModel serverModel) {
        this.serverModel = serverModel;
        this.serverName = serverModel.getChildren().stream().findFirst().get().getName();
    }

    public ServerModel getServerModel() {
        return this.serverModel;
    }

    public String getServerName() {
        return this.serverName;
    }
}
