/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.exceptions.ConnectionFailureException;
import org.opensmartgridplatform.adapter.protocol.iec60870.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.Iec60870Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870DeviceConnectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870DeviceConnectionService.class);

    private static ConcurrentHashMap<String, Iec60870Connection> cache = new ConcurrentHashMap<>();

    private static final int IEC60870_DEFAULT_PORT = 2404;

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    // @Autowired
    // private Iec60870ClientEventListenerFactory
    // iec60870ClientEventListenerFactory;

    // @Autowired
    // private Iec60870Client iec60870Client;

    @Autowired
    private int iec60870SsldPortServer;

    @Autowired
    private int responseTimeout;

    /* @formatter:off
    public DeviceConnection connect(final DeviceConnectionParameters deviceConnectionParameters,
            final String organisationIdentification) throws ConnectionFailureException {
        return this.connect(deviceConnectionParameters, organisationIdentification, true);
    }
     @formatter:on
    */

    public void closeAllConnections() {
        LOGGER.warn("Closing connections for {} devices", cache.size());
        // TODO: close all connections in the cache
        cache.clear();
    }

    private void logProtocolAdapterException(final String deviceIdentification, final ProtocolAdapterException e) {
        LOGGER.error(
                "ProtocolAdapterException: no Iec60870ClientBaseEventListener instance could be constructed, continue without event listener for deviceIdentification: {}",
                deviceIdentification, e);
    }

    /**
     * Closes the {@link ClientAssociation}, send a disconnect request and close
     * the socket.
     */
    public void disconnect(final String deviceIdentification) {
        // TODO: implement
    }

    // public Iec60870Client getIec60870Client() {
    // return this.iec60870Client;
    // }

    public Iec60870Connection getIec60870Connection(final String deviceIdentification) {
        return this.fetchIec60870Connection(deviceIdentification);
    }

    private Iec60870Connection fetchIec60870Connection(final String deviceIdentification) {
        final Iec60870Connection iec60870Connection = cache.get(deviceIdentification);
        if (iec60870Connection == null) {
            LOGGER.info("No connection found for device: {}", deviceIdentification);
        }
        return iec60870Connection;
    }

    private void removeIec60870Connection(final String deviceIdentification) {
        cache.remove(deviceIdentification);
    }

    private InetAddress convertIpAddress(final String ipAddress) throws ConnectionFailureException {
        try {
            if (StringUtils.isEmpty(ipAddress)) {
                throw new ConnectionFailureException("Ip address is null");
            }

            return InetAddress.getByName(ipAddress);
        } catch (final UnknownHostException e) {
            LOGGER.error("Unexpected exception during convertIpAddress", e);
            throw new ConnectionFailureException(e.getMessage(), e);
        }
    }
}
