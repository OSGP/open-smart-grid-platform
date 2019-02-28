/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.helper;

import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnectionParameters;

public class DeviceConnection {

    private final DeviceConnectionParameters deviceConnectionParameters;
    private final Connection connection;

    public DeviceConnection(final Connection connection, final DeviceConnectionParameters deviceConnectionParameters) {
        this.connection = connection;
        this.deviceConnectionParameters = deviceConnectionParameters;
    }

    public DeviceConnectionParameters getDeviceConnectionParameters() {
        return this.deviceConnectionParameters;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
