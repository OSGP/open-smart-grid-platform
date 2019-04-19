/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import javax.annotation.PreDestroy;

import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Client;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseInfo;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870DeviceConnectionService implements ClientConnectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870DeviceConnectionService.class);

    @Autowired
    private ClientConnectionCache connectionCache;
    @Autowired
    private ClientAsduHandlerRegistry asduHandlerRegistry;

    @Autowired
    private Client iec60870Client;

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Override
    public ClientConnection getConnection(final RequestInfo requestInfo) throws ConnectionFailureException {
        final String deviceIdentification = requestInfo.getDeviceIdentification();
        final DeviceConnection cachedDeviceConnection = (DeviceConnection) this.connectionCache
                .getConnection(deviceIdentification);

        if (cachedDeviceConnection != null) {
            LOGGER.info("Connection found in cache for device {}.", deviceIdentification);
            return cachedDeviceConnection;
        } else {
            LOGGER.info("No connection found in cache for device {}, creating new connection.", deviceIdentification);
            return this.createConnection(requestInfo);
        }
    }

    private ClientConnection createConnection(final RequestInfo requestInfo) throws ConnectionFailureException {
        final ConnectionInfo connectionInfo = this.createConnectionInfo(requestInfo);
        final ResponseInfo responseInfo = ResponseInfo.from(requestInfo);

        final Iec60870ClientConnectionEventListener eventListener = new Iec60870ClientConnectionEventListener(
                connectionInfo, this.connectionCache, this.asduHandlerRegistry, responseInfo);

        final ClientConnection newDeviceConnection = this.iec60870Client.connect(connectionInfo, eventListener);

        this.connectionCache.addConnection(requestInfo.getDeviceIdentification(), newDeviceConnection);
        return newDeviceConnection;
    }

    private ConnectionInfo createConnectionInfo(final RequestInfo requestInfo) {
        final String deviceIdentification = requestInfo.getDeviceIdentification();
        final Iec60870Device device = this.iec60870DeviceRepository.findByDeviceIdentification(deviceIdentification);
        return ConnectionInfo.newBuilder().deviceIdentification(deviceIdentification)
                .ipAddress(requestInfo.getIpAddress()).commonAddress(device.getCommonAddress()).port(device.getPort())
                .build();
    }

    /**
     * Closes the {@link Connection}, send a disconnect request and close the
     * socket.
     *
     * @param deviceIdentification
     *            Device for which to close the connection.
     */
    public void disconnect(final String deviceIdentification) {
        final ClientConnection connection = this.connectionCache.getConnection(deviceIdentification);

        if (connection instanceof DeviceConnection) {
            this.iec60870Client.disconnect(connection);

            this.connectionCache.removeConnection(deviceIdentification);
        } else {
            LOGGER.warn("No connection found for deviceIdentification {}", deviceIdentification);
        }
    }

    @Override
    @PreDestroy
    public void closeAllConnections() {
        LOGGER.warn("Closing connections for {} devices", this.connectionCache.getSize());
        this.connectionCache.applyToAll(this::disconnect);
    }

}
