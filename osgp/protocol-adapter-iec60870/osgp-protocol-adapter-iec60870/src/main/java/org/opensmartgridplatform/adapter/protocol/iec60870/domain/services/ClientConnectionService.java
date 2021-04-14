/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.PreDestroy;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.ClientConnectionAlreadyInCacheException;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
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
public class ClientConnectionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnectionService.class);

  @Autowired private ClientConnectionCache connectionCache;

  @Autowired private Client iec60870Client;

  @Autowired private Iec60870DeviceRepository iec60870DeviceRepository;

  @Autowired private ClientAsduHandlerRegistry clientAsduHandlerRegistry;

  @Autowired private LoggingService loggingService;

  @Autowired private LogItemFactory logItemFactory;

  @Autowired private ResponseMetadataFactory responseMetadataFactory;

  public ClientConnection getConnection(final RequestMetadata requestMetadata)
      throws ConnectionFailureException {
    final String deviceIdentification = requestMetadata.getDeviceIdentification();
    LOGGER.debug("Get connection called for device {}.", deviceIdentification);

    final Iec60870Device device = this.getIec60870Device(deviceIdentification);
    final String connectionDeviceIdentification = device.getConnectionDeviceIdentification();

    if (device.hasGatewayDevice()) {
      LOGGER.debug(
          "Getting connection for device {} using gateway device {}.",
          deviceIdentification,
          connectionDeviceIdentification);
    }

    final ClientConnection cachedDeviceConnection =
        this.connectionCache.getConnection(connectionDeviceIdentification);

    if (cachedDeviceConnection != null) {
      LOGGER.info("Connection found in cache for device {}.", connectionDeviceIdentification);
      return cachedDeviceConnection;
    } else {
      LOGGER.info(
          "No connection found in cache for device {}, creating new connection.",
          connectionDeviceIdentification);
      return this.createConnection(requestMetadata);
    }
  }

  public void closeConnection(final String deviceIdentification) {
    final ClientConnection connection = this.connectionCache.getConnection(deviceIdentification);
    this.close(connection);
  }

  public synchronized void close(final ClientConnection connection) {
    final String deviceIdentification =
        connection.getConnectionParameters().getDeviceIdentification();
    if (connection instanceof DeviceConnection) {

      this.iec60870Client.disconnect(connection);

      this.connectionCache.removeConnection(deviceIdentification);

    } else {
      LOGGER.warn("No connection found for deviceIdentification {}", deviceIdentification);
    }
  }

  @PreDestroy
  public void closeAllConnections() {
    LOGGER.info("Closing all connections.");
    final Collection<ClientConnection> connections = this.connectionCache.getConnections();
    LOGGER.warn("{} active connections found, closing all.", connections.size());
    connections.forEach(this::close);
  }

  private ClientConnection createConnection(final RequestMetadata requestMetadata)
      throws ConnectionFailureException {
    final String deviceIdentification = requestMetadata.getDeviceIdentification();
    final Iec60870Device device = this.getIec60870Device(deviceIdentification);
    final Iec60870Device connectionDevice = this.getConnectionDevice(device);
    final String connectionDeviceIdentification = device.getConnectionDeviceIdentification();

    final ConnectionParameters connectionParameters =
        this.createConnectionParameters(connectionDevice, requestMetadata.getIpAddress());

    final ResponseMetadata responseMetadata =
        ResponseMetadata.from(
            requestMetadata, connectionDeviceIdentification, connectionDevice.getDeviceType());

    final ClientConnectionEventListener eventListener =
        new ClientConnectionEventListener.Builder()
            .withDeviceIdentification(connectionDeviceIdentification)
            .withClientAsduHandlerRegistry(this.clientAsduHandlerRegistry)
            .withClientConnectionCache(this.connectionCache)
            .withLoggingService(this.loggingService)
            .withLogItemFactory(this.logItemFactory)
            .withResponseMetadata(responseMetadata)
            .withResponseMetadataFactory(this.responseMetadataFactory)
            .build();

    final ClientConnection newDeviceConnection =
        this.iec60870Client.connect(connectionParameters, eventListener);

    try {
      this.connectionCache.addConnection(connectionDeviceIdentification, newDeviceConnection);
    } catch (final ClientConnectionAlreadyInCacheException e) {
      LOGGER.warn(
          "Client connection for device {} already exists. Closing new connection and returning existing connection",
          connectionDeviceIdentification);
      LOGGER.debug("Exception: ", e);
      newDeviceConnection.getConnection().close();
      return e.getClientConnection();
    }
    return newDeviceConnection;
  }

  private ConnectionParameters createConnectionParameters(
      final Iec60870Device device, final String ipAddress) {
    return ConnectionParameters.newBuilder()
        .deviceIdentification(device.getDeviceIdentification())
        .ipAddress(ipAddress)
        .commonAddress(device.getCommonAddress())
        .port(device.getPort())
        .build();
  }

  private Iec60870Device getConnectionDevice(final Iec60870Device device)
      throws ConnectionFailureException {
    if (device.hasGatewayDevice()) {
      final String gatewayDeviceIdentification = device.getGatewayDeviceIdentification();
      return this.getIec60870Device(gatewayDeviceIdentification);
    } else {
      return device;
    }
  }

  private Iec60870Device getIec60870Device(final String deviceIdentification)
      throws ConnectionFailureException {
    return this.iec60870DeviceRepository
        .findByDeviceIdentification(deviceIdentification)
        .orElseThrow(this.connectionFailureException(deviceIdentification));
  }

  private Supplier<ConnectionFailureException> connectionFailureException(
      final String deviceIdentification) {
    final String message = String.format("Device %s not found", deviceIdentification);
    return () -> new ConnectionFailureException(ComponentType.PROTOCOL_IEC60870, message);
  }
}
