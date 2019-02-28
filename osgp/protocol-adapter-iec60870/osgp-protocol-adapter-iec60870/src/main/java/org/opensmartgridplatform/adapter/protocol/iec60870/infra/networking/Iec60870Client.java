/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.openmuc.j60870.ClientConnectionBuilder;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870Client.class);

    private static final int IEC60870_DEFAULT_PORT = 2404;

    @Autowired
    private int connectionTimeout;

    @Autowired
    private int responseTimeout;

    @PostConstruct
    private void init() {
        LOGGER.info("connectionTimeout: {}, responseTimeout: {}", this.connectionTimeout, this.responseTimeout);
    }

    public DeviceConnection connect(final DeviceConnectionParameters deviceConnectionParameters,
            final ConnectionEventListener asduListener) throws ConnectionFailureException {

        final InetAddress address = this.convertIpAddress(deviceConnectionParameters.getIpAddress());
        final String deviceIdentification = deviceConnectionParameters.getDeviceIdentification();
        final int port = deviceConnectionParameters.getPort() == null ? IEC60870_DEFAULT_PORT
                : deviceConnectionParameters.getPort();

        final ClientConnectionBuilder clientConnectionBuilder = new ClientConnectionBuilder(address).setPort(port);

        try {
            LOGGER.info("Connecting to device: {}...", deviceIdentification);
            final Connection connection = clientConnectionBuilder.connect();
            connection.startDataTransfer(asduListener, this.connectionTimeout);
            LOGGER.info("Connected to device: {}", deviceIdentification);

            return new DeviceConnection(connection, deviceConnectionParameters);

        } catch (final IOException | TimeoutException e) {
            final String errorMessage = "Unable to connect to remote host: "
                    + deviceConnectionParameters.getIpAddress();
            LOGGER.error(errorMessage, e);

            throw new ConnectionFailureException(ComponentType.PROTOCOL_IEC60870, errorMessage);
        }

    }

    public DeviceConnection connect(final DeviceConnectionParameters deviceConnectionParameters)
            throws ConnectionFailureException {

        final InetAddress address = this.convertIpAddress(deviceConnectionParameters.getIpAddress());
        final String deviceIdentification = deviceConnectionParameters.getDeviceIdentification();
        final int port = deviceConnectionParameters.getPort() == null ? IEC60870_DEFAULT_PORT
                : deviceConnectionParameters.getPort();

        final ClientConnectionBuilder clientConnectionBuilder = new ClientConnectionBuilder(address).setPort(port);

        try {
            final Connection connection = clientConnectionBuilder.connect();
            LOGGER.info("Created connection without starting data transfer to: {}", deviceIdentification);

            return new DeviceConnection(connection, deviceConnectionParameters);

        } catch (final IOException e) {
            final String errorMessage = "Unable to connect to remote host: "
                    + deviceConnectionParameters.getIpAddress();
            LOGGER.error(errorMessage, e);

            throw new ConnectionFailureException(ComponentType.PROTOCOL_IEC60870, errorMessage);
        }

    }

    private InetAddress convertIpAddress(final String ipAddress) throws ConnectionFailureException {
        try {
            if (StringUtils.isEmpty(ipAddress)) {
                throw new ConnectionFailureException(ComponentType.PROTOCOL_IEC60870, "Ip address is null");
            }

            return InetAddress.getByName(ipAddress);
        } catch (final UnknownHostException e) {
            LOGGER.error("Unexpected exception during convertIpAddress", e);
            throw new ConnectionFailureException(ComponentType.PROTOCOL_IEC60870, e.getMessage());
        }
    }

    /**
     * Disconnect from the device.
     *
     * @param deviceConnection
     *            The {@link DeviceConnection} instance.
     */
    public void disconnect(final DeviceConnection deviceConnection) {
        LOGGER.info("Disconnecting from device: {}...",
                deviceConnection.getDeviceConnectionParameters().getDeviceIdentification());
        deviceConnection.getConnection().close();
        LOGGER.info("Disconnected from device: {}",
                deviceConnection.getDeviceConnectionParameters().getDeviceIdentification());
    }

}
