/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.services;

import javax.annotation.PreDestroy;

import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.Iec60870Client;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870DeviceConnectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870DeviceConnectionService.class);

    @Autowired
    private ClientConnectionCache cache;

    @Autowired
    private Iec60870Client iec60870Client;

    public DeviceConnection connect(final DeviceConnectionParameters deviceConnectionParameters,
            final ConnectionEventListener asduListener) throws ConnectionFailureException {

        final DeviceConnection cachedDeviceConnection = this
                .fetchDeviceConnection(deviceConnectionParameters.getDeviceIdentification());

        if (cachedDeviceConnection != null) {
            return cachedDeviceConnection;
        } else {
            final DeviceConnection newDeviceConnection = this.iec60870Client.connect(deviceConnectionParameters,
                    asduListener);
            this.cache.addConnection(deviceConnectionParameters.getDeviceIdentification(),
                    newDeviceConnection);

            return newDeviceConnection;
        }
    }

    public DeviceConnection connect(final DeviceConnectionParameters deviceConnectionParameters)
            throws ConnectionFailureException {

        final DeviceConnection cachedDeviceConnection = this
                .fetchDeviceConnection(deviceConnectionParameters.getDeviceIdentification());

        if (cachedDeviceConnection != null) {
            return cachedDeviceConnection;
        } else {
            final DeviceConnection newDeviceConnection = this.iec60870Client.connect(deviceConnectionParameters);
            this.cache.addConnection(deviceConnectionParameters.getDeviceIdentification(),
                    newDeviceConnection);

            return newDeviceConnection;
        }
    }

    private DeviceConnection fetchDeviceConnection(final String deviceIdentification) {
        final ClientConnection connection = this.cache.getConnection(deviceIdentification);
        DeviceConnection result = null;
        if (connection instanceof DeviceConnection) {
            LOGGER.info("Connection found for device: {}", deviceIdentification);
            result = (DeviceConnection) connection;
        } else {
            LOGGER.info("No connection found for device: {}", deviceIdentification);
        }
        return result;
    }

    /**
     * Closes the {@link Connection}, send a disconnect request and close the
     * socket.
     *
     * @param deviceIdentification
     *            Device for which to close the connection.
     */
    public void disconnect(final String deviceIdentification) {
        final ClientConnection connection = this.cache.getConnection(deviceIdentification);

        if (connection instanceof DeviceConnection) {
            this.iec60870Client.disconnect((DeviceConnection) connection);

            this.cache.removeConnection(deviceIdentification);
        } else {
            LOGGER.warn("No connection found for deviceIdentification {}", deviceIdentification);
        }
    }

    @PreDestroy
    public void closeAllConnections() {
        LOGGER.warn("Closing connections for {} devices", this.cache.getSize());
        this.cache.applyToAll(this::disconnect);
    }

}
