/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.io.EOFException;
import java.io.IOException;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.iec60870.exceptions.Iec60870AsduHandlerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing the {@link ConnectionEventListener} interface for receiving incoming ASdus and
 * connection closed events.
 */
public class Iec60870ConnectionEventListener implements ConnectionEventListener {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec60870ConnectionEventListener.class);

  private final Connection connection;
  private final Iec60870ConnectionRegistry iec60870ConnectionRegistry;
  private final Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry;

  public Iec60870ConnectionEventListener(
      final Connection connection,
      final Iec60870ConnectionRegistry iec60870ConnectionRegistry,
      final Iec60870AsduHandlerRegistry iec60870AsduHandlerRegistry) {
    this.connection = connection;
    this.iec60870ConnectionRegistry = iec60870ConnectionRegistry;
    this.iec60870AsduHandlerRegistry = iec60870AsduHandlerRegistry;
  }

  @Override
  public void newASdu(final ASdu asdu) {
    try {
      final ASduType asduType = asdu.getTypeIdentification();
      final Iec60870AsduHandler asduHandler = this.iec60870AsduHandlerRegistry.getHandler(asduType);
      asduHandler.handleAsdu(this.connection, asdu);

    } catch (final Iec60870AsduHandlerNotFoundException e) {
      LOGGER.error("Unknown request received, no handler available for ASDU: {}", asdu, e);
    } catch (final EOFException e) {
      LOGGER.error("Connection closed on connection ({}).", this.connection, e);
    } catch (final Exception e) {
      LOGGER.error("Exception occurred on connection ({}).", this.connection, e);
    }
  }

  @Override
  public void connectionClosed(final IOException e) {
    LOGGER.info("Connection ({}) closed.", this.connection, e);
    this.iec60870ConnectionRegistry.unregisterConnection(this.connection);
  }
}
