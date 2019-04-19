/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;

public class DeviceConnection implements ClientConnection {

    private final ConnectionInfo connectionInfo;
    private final Connection connection;

    public DeviceConnection(final Connection connection, final ConnectionInfo connectionInfo) {
        this.connection = connection;
        this.connectionInfo = connectionInfo;
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return this.connectionInfo;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }
}
