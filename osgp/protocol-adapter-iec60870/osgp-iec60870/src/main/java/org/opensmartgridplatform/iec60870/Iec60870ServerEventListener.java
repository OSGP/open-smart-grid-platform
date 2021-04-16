/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ServerEventListener;
import org.openmuc.j60870.ie.IeCauseOfInitialization;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing the {@link ServerEventListener} interface for incoming connection attempts.
 */
public class Iec60870ServerEventListener implements ServerEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870ServerEventListener.class);

  private static final ASdu END_OF_INITIALIZATION =
      new ASdu(
          ASduType.M_EI_NA_1,
          false,
          CauseOfTransmission.INITIALIZED,
          false,
          false,
          0,
          0,
          new InformationObject(0, new IeCauseOfInitialization(0, false)));

  private final Iec60870ConnectionRegistry iec60870ConnectionRegistry;
  private final Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry;
  private final int connectionTimeout;
  private final boolean sendEndOfInitialization;
  private final AtomicBoolean initializationComplete = new AtomicBoolean(false);

  public Iec60870ServerEventListener(
      final Iec60870ConnectionRegistry iec60870ConnectionRegistry,
      final Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry,
      final int connectionTimeout) {
    this(iec60870ConnectionRegistry, iec60870AsduHandlerRegistry, connectionTimeout, false);
  }

  public Iec60870ServerEventListener(
      final Iec60870ConnectionRegistry iec60870ConnectionRegistry,
      final Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry,
      final int connectionTimeout,
      final boolean sendEndOfInitialization) {
    this.iec60870ConnectionRegistry = iec60870ConnectionRegistry;
    this.iec60870AsduHandlerRegistry = iec60870AsduHandlerRegistry;
    this.connectionTimeout = connectionTimeout;
    this.sendEndOfInitialization = sendEndOfInitialization;
  }

  @Override
  public void connectionIndication(final Connection connection) {
    LOGGER.info("Client connected on connection ({}).", connection);

    try {
      LOGGER.info(
          "Waiting for StartDT on connection ({}) for {} ms.", connection, this.connectionTimeout);
      connection.waitForStartDT(
          new Iec60870ConnectionEventListener(
              connection, this.iec60870ConnectionRegistry, this.iec60870AsduHandlerRegistry),
          this.connectionTimeout);
    } catch (final IOException e) {
      LOGGER.error(
          "Exception occurred while connection ({}) was waiting for StartDT.", connection, e);
      return;
    }
    this.sendEndOfInitializationOnFirstConnection(connection);
    this.iec60870ConnectionRegistry.registerConnection(connection);
    LOGGER.info("Connection ({}) listening for incoming commands.", connection);
  }

  private void sendEndOfInitializationOnFirstConnection(final Connection connection) {
    if (this.sendEndOfInitialization && this.initializationComplete.compareAndSet(false, true)) {
      try {
        LOGGER.info("Sending end of initialization ASDU on first connection");
        connection.send(END_OF_INITIALIZATION);
      } catch (final IOException e) {
        LOGGER.error("Sending end of initialization ASDU on first connection failed", e);
      }
    } else {
      LOGGER.debug("Not sending end of initialization ASDU as initialization was already complete");
    }
  }

  public void sendInformationUpdateEvent(
      final int informationObjectAddress, final InformationElement[][] informationElements) {

    final ASdu event =
        new ASdu(
            ASduType.M_SP_TB_1,
            false,
            CauseOfTransmission.SPONTANEOUS,
            false,
            false,
            0,
            0,
            new InformationObject(informationObjectAddress, informationElements));

    this.iec60870ConnectionRegistry
        .getAllConnections()
        .forEach(connection -> this.sendEvent(connection, event));
  }

  private void sendEvent(final Connection connection, final ASdu event) {
    try {
      LOGGER.info("Sending event {}", event);
      connection.send(event);
    } catch (final IOException e) {
      LOGGER.error("Sending event {} failed", event, e);
    }
  }

  public void stopListening() {
    LOGGER.info("Stop listening, closing connections.");
    this.iec60870ConnectionRegistry.getAllConnections().forEach(this::closeConnection);
  }

  private void closeConnection(final Connection connection) {
    LOGGER.info("Closing connection {}.", connection);
    connection.close();
  }

  @Override
  public void serverStoppedListeningIndication(final IOException e) {
    LOGGER.info("Server has stopped listening: {}.", e.getMessage());
  }

  @Override
  public void connectionAttemptFailed(final IOException e) {
    LOGGER.warn("Connection attempt failed: {}", e.getMessage());
  }
}
