/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.Collection;

import javax.annotation.PreDestroy;

import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.ClientConnectionAlreadyInCacheException;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientConnectionServiceImpl implements ClientConnectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnectionServiceImpl.class);

    @Autowired
    private ClientConnectionCache connectionCache;

    @Autowired
    private Client iec60870Client;

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Autowired
    private ClientAsduHandlerRegistry clientAsduHandlerRegistry;

    @Override
    public ClientConnection getConnection(final RequestMetadata requestMetadata) throws ConnectionFailureException {
        final String deviceIdentification = requestMetadata.getDeviceIdentification();
        final ClientConnection cachedDeviceConnection = this.connectionCache.getConnection(deviceIdentification);

        if (cachedDeviceConnection != null) {
            LOGGER.info("Connection found in cache for device {}.", deviceIdentification);
            return cachedDeviceConnection;
        } else {
            LOGGER.info("No connection found in cache for device {}, creating new connection.", deviceIdentification);
            return this.createConnection(requestMetadata);
        }
    }

    @Override
    @PreDestroy
    public void closeAllConnections() {
        LOGGER.info("Closing all connections.");
        final Collection<ClientConnection> connections = this.connectionCache.getConnections();
        LOGGER.warn("{} active connections found, closing all.", connections.size());
        connections.forEach(this::disconnect);
    }

    /**
     * Closes the {@link Connection}, sends a disconnect request and closes the
     * socket.
     *
     * @param deviceIdentification
     *            Device for which to close the connection.
     */
    public void disconnect(final String deviceIdentification) {
        final ClientConnection connection = this.connectionCache.getConnection(deviceIdentification);
        this.disconnect(connection);
    }

    /**
     * Closes the {@link Connection}, sends a disconnect request and closes the
     * socket.
     *
     * @param deviceIdentification
     *            Device for which to close the connection.
     */
    public synchronized void disconnect(final ClientConnection connection) {
        final String deviceIdentification = connection.getConnectionParameters().getDeviceIdentification();
        if (connection instanceof DeviceConnection) {

            this.iec60870Client.disconnect(connection);

            this.connectionCache.removeConnection(deviceIdentification);

        } else {
            LOGGER.warn("No connection found for deviceIdentification {}", deviceIdentification);
        }
    }

    private ClientConnection createConnection(final RequestMetadata requestMetadata) throws ConnectionFailureException {
        final String deviceIdentification = requestMetadata.getDeviceIdentification();
        final Iec60870Device device = this.iec60870DeviceRepository.findByDeviceIdentification(deviceIdentification)
                .orElseThrow(
                        () -> new ConnectionFailureException(ComponentType.PROTOCOL_IEC60870, "Device not found."));

        final ConnectionParameters connectionParameters = this.createConnectionParameters(device,
                requestMetadata.getIpAddress());
        final ResponseMetadata responseMetadata = ResponseMetadata.from(requestMetadata, device.getDeviceType());

        final ClientConnectionEventListener eventListener = new ClientConnectionEventListener(
                connectionParameters.getDeviceIdentification(), this.connectionCache, this.clientAsduHandlerRegistry,
                responseMetadata);

        final ClientConnection newDeviceConnection = this.iec60870Client.connect(connectionParameters, eventListener);

        // TODO - refactor
        try {
            this.connectionCache.addConnection(deviceIdentification, newDeviceConnection);
        } catch (final ClientConnectionAlreadyInCacheException e) {
            LOGGER.warn(
                    "Client connection for device {} already exists. Closing new connection and returning existing connection",
                    deviceIdentification);
            LOGGER.debug("Exception: ", e);
            newDeviceConnection.getConnection().close();
            return e.getClientConnection();
        }
        return newDeviceConnection;
    }

    private ConnectionParameters createConnectionParameters(final Iec60870Device device, final String ipAddress) {
        return ConnectionParameters.newBuilder()
                .deviceIdentification(device.getDeviceIdentification())
                .ipAddress(ipAddress)
                .commonAddress(device.getCommonAddress())
                .port(device.getPort())
                .build();
    }
}
