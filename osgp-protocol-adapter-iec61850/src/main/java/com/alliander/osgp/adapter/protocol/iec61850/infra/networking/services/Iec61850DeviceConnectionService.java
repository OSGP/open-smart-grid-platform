/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850ClientAssociation;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Connection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.IED;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientBaseEventListener;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting.Iec61850ClientEventListenerFactory;

@Component
public class Iec61850DeviceConnectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850DeviceConnectionService.class);

    private static ConcurrentHashMap<String, Iec61850Connection> cache = new ConcurrentHashMap<>();

    @Autowired
    private DeviceManagementService deviceManagementService;

    @Autowired
    private Iec61850Client iec61850Client;

    @Autowired
    private int iec61850SsldPortServer;

    @Autowired
    private int iec61850RtuPortServer;

    @Autowired
    private int responseTimeout;

    public synchronized void connect(final String ipAddress, final String deviceIdentification, final IED ied,
            final LogicalDevice logicalDevice) {
        LOGGER.info("Trying to find connection in cache for deviceIdentification: {}", deviceIdentification);

        try {
            final Iec61850Connection iec61850Connection = this.fetchIec61850Connection(deviceIdentification);
            if (iec61850Connection != null) {
                // Already connected, check if connection is still usable.
                LOGGER.info("Connection found for deviceIdentification: {}", deviceIdentification);
                boolean isConnectionAlive = false;

                // Read physical name node (only), which is much faster, but
                // requires manual reads of remote data
                if (ied != null && logicalDevice != null) {
                    isConnectionAlive = this.iec61850Client.readNodeDataValues(
                            iec61850Connection.getClientAssociation(),
                            (FcModelNode) iec61850Connection.getServerModel().findModelNode(
                                    ied.getDescription() + logicalDevice.getDescription() + "/"
                                            + LogicalNode.LOGICAL_NODE_ZERO.getDescription() + "."
                                            + DataAttribute.NAME_PLATE.getDescription(), Fc.DC));
                } else {
                    // Read all data values, which is much slower, but requires
                    // no manual reads of remote data
                    isConnectionAlive = this.iec61850Client
                            .readAllDataValues(iec61850Connection.getClientAssociation());
                }

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
            LOGGER.info("Trying to connect to deviceIdentification: {} at IP address {}", deviceIdentification,
                    ipAddress);

            final Iec61850ClientBaseEventListener eventListener = Iec61850ClientEventListenerFactory.getInstance()
                    .getEventListener(ied, deviceIdentification, this.deviceManagementService);

            Iec61850ClientAssociation iec61850clientAssociation;
            switch (ied) {
            case FLEX_OVL:
                iec61850clientAssociation = this.iec61850Client.connect(deviceIdentification, inetAddress,
                        eventListener, this.iec61850SsldPortServer);
                break;
            case ZOWN_RTU:
                iec61850clientAssociation = this.iec61850Client.connect(deviceIdentification, inetAddress,
                        eventListener, this.iec61850RtuPortServer);
                break;
            default:
                throw new ProtocolAdapterException("Unable to execute Disable Registration for IED "
                        + ied.getDescription());
            }

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
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Unexpected exception when connecting to an IEC61850 device", e);
            return;
        }
    }

    public synchronized void disconnect(final String deviceIdentification) {
        try {
            LOGGER.info("Trying to disconnect from deviceIdentification: {}", deviceIdentification);
            final Iec61850Connection iec61850Connection = this.fetchIec61850Connection(deviceIdentification);
            if (iec61850Connection != null) {
                iec61850Connection.getClientAssociation().disconnect();
                this.removeIec61850Connection(deviceIdentification);
                LOGGER.info("Disconnected from deviceIdentification: {}", deviceIdentification);
            } else {
                LOGGER.info("Unable to disconnect from deviceIdentification: {}, no cached connection was found",
                        deviceIdentification);
            }
        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during disconnect()", e);
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
        if (iec61850Connection == null) {
            return null;
        }

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

    private synchronized Iec61850Connection fetchIec61850Connection(final String deviceIdentification)
            throws ProtocolAdapterException {
        final Iec61850Connection iec61850Connection = cache.get(deviceIdentification);
        if (iec61850Connection == null) {
            LOGGER.info("No connection found for device: {}", deviceIdentification);
        }
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
