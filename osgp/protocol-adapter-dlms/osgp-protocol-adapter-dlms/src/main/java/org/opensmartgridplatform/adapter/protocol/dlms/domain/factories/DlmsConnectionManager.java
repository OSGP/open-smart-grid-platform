/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.RawMessageData;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.throttling.api.Permit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Object that manages and exposes a single DLMS connection. */
public class DlmsConnectionManager implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(DlmsConnectionManager.class);
  private static final DlmsMessageListener DO_NOTHING_LISTENER =
      new DlmsMessageListener() {

        @Override
        public void messageCaptured(final RawMessageData rawMessageData) {
          // Do nothing.
        }

        @Override
        public void setMessageMetadata(final MessageMetadata messageMetadata) {
          // Do nothing.
        }

        @Override
        public void setDescription(final String description) {
          // Do nothing.
        }
      };

  private final MessageMetadata messageMetadata;
  private final DlmsConnector connector;
  private final DlmsDevice device;
  private final DlmsMessageListener dlmsMessageListener;
  private final DomainHelperService domainHelperService;
  private final Permit permit;

  private DlmsConnection dlmsConnection;

  public DlmsConnectionManager(
      final DlmsConnector connector,
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener,
      final DomainHelperService domainHelperService) {

    this(connector, messageMetadata, device, dlmsMessageListener, domainHelperService, null);
  }

  public DlmsConnectionManager(
      final DlmsConnector connector,
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener,
      final DomainHelperService domainHelperService,
      final Permit permit) {
    this.connector = connector;
    this.messageMetadata = messageMetadata;
    this.device = device;
    if (dlmsMessageListener == null) {
      this.dlmsMessageListener = DO_NOTHING_LISTENER;
    } else {
      this.dlmsMessageListener = dlmsMessageListener;
    }
    this.domainHelperService = domainHelperService;
    this.permit = permit;
  }

  /**
   * @return the current connection, obtained by calling {@link #connect() connect}.
   * @throws IllegalStateException when there is no connection available.
   */
  public DlmsConnection getConnection() {
    if (!this.isConnected()) {
      throw new IllegalStateException("There is no connection available.");
    }
    return this.dlmsConnection;
  }

  public boolean hasDlmsMessageListener() {
    return DO_NOTHING_LISTENER != this.dlmsMessageListener;
  }

  public DlmsMessageListener getDlmsMessageListener() {
    return this.dlmsMessageListener;
  }

  public Permit getPermit() {
    return this.permit;
  }

  /**
   * Disconnects from the device, and releases the internal connection reference.
   *
   * @throws IOException When an exception occurs while disconnecting.
   */
  public void disconnect() throws IOException {
    if (this.dlmsConnection != null) {
      this.dlmsConnection.disconnect();
      this.dlmsConnection = null;
    }
  }

  public boolean isConnected() {
    return this.dlmsConnection != null;
  }

  /**
   * Obtains a connection with a device. A connection should be obtained before {@link
   * #getConnection() getConnection} is called.
   *
   * @throws IllegalStateException When there is already a connection set.
   * @throws OsgpException in case of a TechnicalException (When an exceptions occurs while creating
   *     the exception) or a FunctionalException
   */
  public void connect() throws OsgpException {
    if (this.dlmsConnection != null) {
      throw new IllegalStateException(
          "Cannot create a new connection because a connection already exists.");
    }

    this.dlmsConnection =
        this.connector.connect(this.messageMetadata, this.device, this.dlmsMessageListener);
  }

  /**
   * Obtains a new connection with a device. A connection should be obtained before {@link
   * #getConnection() getConnection} is called.
   *
   * @throws OsgpException in case of a TechnicalException (When an exceptions occurs while creating
   *     the exception), a FunctionalException or a ProtocolAdapterException
   */
  public void reconnect() throws OsgpException {
    if (this.dlmsConnection != null) {
      throw new IllegalStateException(
          "Cannot create a new connection because a connection already exists.");
    }

    if (!this.device.isIpAddressIsStatic()) {
      this.device.setIpAddress(
          this.domainHelperService.getDeviceIpAddressFromSessionProvider(this.device));
    }
    this.dlmsConnection =
        this.connector.connect(this.messageMetadata, this.device, this.dlmsMessageListener);
  }

  /**
   * Closes the connection with the device and releases the internal connection reference. The
   * connection will be closed, but no disconnection message will be sent to the device.
   */
  @Override
  public void close() {
    try {
      this.dlmsConnection.close();
    } catch (final IOException e) {
      LOGGER.warn("Failure while trying to close a DLMS connection", e);
    }
    this.dlmsConnection = null;
  }
}
