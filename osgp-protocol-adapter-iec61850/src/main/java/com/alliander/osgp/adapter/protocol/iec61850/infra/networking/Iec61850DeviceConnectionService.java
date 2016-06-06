/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;

@Component
public class Iec61850DeviceConnectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850DeviceConnectionService.class);

    private static ConcurrentHashMap<String, Iec61850Connection> cache = new ConcurrentHashMap<>();

    @Autowired
    private Iec61850Client iec61850Client;

    @Resource
    private int responseTimeout;

    public void connect(final String ipAddress, final String deviceIdentification) {
        LOGGER.info("Trying to find connection in cache for deviceIdentification: {}", deviceIdentification);

        try {
            final Iec61850Connection iec61850Connection = this.fetchIec61850Connection(deviceIdentification);
            if (iec61850Connection != null) {
                // Already connected, check if connection is still usable.
                LOGGER.info("Connection found for deviceIdentification: {}", deviceIdentification);
                final boolean isConnectionAlive = this.iec61850Client.readAllDataValues(iec61850Connection
                        .getClientAssociation());
                if (isConnectionAlive) {
                    LOGGER.info("Connection is still active for deviceIdentification: {}", deviceIdentification);
                    return;
                } else {
                    LOGGER.info(
                            "Connection is no longer active, removing connection from cache for deviceIdentification: {}",
                            deviceIdentification);
                    this.removeIec61850Connection(deviceIdentification);
                }
            }
        } catch (final Exception e) {
            LOGGER.error(String.format(
                    "Unexpected exception while trying to find a cached connection for deviceIdentification: %s",
                    deviceIdentification), e);
        }

        final InetAddress inetAddress = this.convertIpAddress(ipAddress);
        if (inetAddress == null) {
            return;
        }

        // Connect to obtain ClientAssociation, ServerModel and try to read all
        // values.
        try {
            // Try to connect.
            LOGGER.info("Trying to connect to deviceIdentification: {} at ip {}", deviceIdentification, ipAddress);
            final Iec61850ClientAssociation iec61850clientAssociation = this.iec61850Client.connect(
                    deviceIdentification, inetAddress);
            final ClientAssociation clientAssociation = iec61850clientAssociation.getClientAssociation();

            // Set response time-out
            clientAssociation.setResponseTimeout(this.responseTimeout);

            // Read the ServerModel, either from the device or from a SCL file.
            final ServerModel serverModel = this.iec61850Client.readServerModelFromDevice(clientAssociation);

            // Read all data values from the device.
            this.iec61850Client.readAllDataValues(clientAssociation);

            // Cache the connection.
            this.cacheIec61850Connection(deviceIdentification, new Iec61850Connection(iec61850clientAssociation,
                    serverModel));
        } catch (final ServiceError e) {
            LOGGER.error("Unexpected exception when connecting to an IEC61850 device", e);
            return;
        }
    }

    public Iec61850ClientAssociation getIec61850ClientAssociation(final String deviceIdentification)
            throws ProtocolAdapterException {
        final Iec61850Connection iec61850Connection = this.fetchIec61850Connection(deviceIdentification);
        return iec61850Connection.getIec61850ClientAssociation();
    }

    public ClientAssociation getClientAssociation(final String deviceIdentification) throws ProtocolAdapterException {
        final Iec61850Connection iec61850Connection = this.fetchIec61850Connection(deviceIdentification);
        return iec61850Connection.getClientAssociation();
    }

    public ServerModel getServerModel(final String deviceIdentification) throws ProtocolAdapterException {
        final Iec61850Connection iec61850Connection = this.fetchIec61850Connection(deviceIdentification);
        // TODO: add null-check.
        return iec61850Connection.getServerModel();
    }

    public boolean readAllValues(final String deviceIdentification) throws ProtocolAdapterException {
        final Iec61850Connection iec61850Connection = this.fetchIec61850Connection(deviceIdentification);
        if (iec61850Connection == null) {
            return false;
        }
        final ClientAssociation clientAssociation = iec61850Connection.getClientAssociation();
        return this.iec61850Client.readAllDataValues(clientAssociation);
    }

    private void cacheIec61850Connection(final String deviceIdentification, final Iec61850Connection iec61850Connection) {
        cache.put(deviceIdentification, iec61850Connection);
    }

    private Iec61850Connection fetchIec61850Connection(final String deviceIdentification)
            throws ProtocolAdapterException {
        final Iec61850Connection iec61850Connection = cache.get(deviceIdentification);
        // if (iec61850Connection == null) {
        // throw new
        // ProtocolAdapterException(String.format("No connection found for deviceIdentification: %s",
        // deviceIdentification));
        // }
        return iec61850Connection;
    }

    private void removeIec61850Connection(final String deviceIdentification) {
        cache.remove(deviceIdentification);
    }

    private InetAddress convertIpAddress(final String ipAddress) {
        try {
            return InetAddress.getByName(ipAddress);
        } catch (final UnknownHostException e) {
            LOGGER.error("Unexpected exception during convertIpAddress", e);
            return null;
        }
    }
}
