/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.io.IOException;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.iec60870.exceptions.Iec60870AsduHandlerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConnectionEventListener implements ConnectionEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientConnectionEventListener.class);

  private final LoggingService loggingService;
  private final LogItemFactory logItemFactory;
  private final ResponseMetadataFactory responseMetadataFactory;
  private final ResponseMetadata responseMetadata;
  private final ClientConnectionCache connectionCache;
  private final ClientAsduHandlerRegistry asduHandlerRegistry;
  private final String deviceIdentification;

  private ClientConnectionEventListener(final Builder builder) {
    this.asduHandlerRegistry = builder.asduHandlerRegistry;
    this.connectionCache = builder.connectionCache;
    this.deviceIdentification = builder.deviceIdentification;
    this.loggingService = builder.loggingService;
    this.logItemFactory = builder.logItemFactory;
    this.responseMetadata = builder.responseMetadata;
    this.responseMetadataFactory = builder.responseMetadataFactory;
  }

  @Override
  public void newASdu(final ASdu asdu) {
    LOGGER.info("Received incoming ASDU from device {}:\n{}", this.deviceIdentification, asdu);
    try {
      final ResponseMetadata newResponseMetadata =
          this.responseMetadataFactory.createWithNewCorrelationUid(this.responseMetadata);

      final LogItem logItem =
          this.logItemFactory.create(
              asdu,
              this.deviceIdentification,
              newResponseMetadata.getOrganisationIdentification(),
              true);

      this.loggingService.log(logItem);

      final ClientAsduHandler asduHandler = this.asduHandlerRegistry.getHandler(asdu);
      asduHandler.handleAsdu(asdu, newResponseMetadata);

    } catch (final Iec60870AsduHandlerNotFoundException e) {
      LOGGER.error("Unknown request received, no handler available for ASDU: {}", asdu, e);
    } catch (final Exception e) {
      LOGGER.error(
          "Exception occurred while handling an incoming ASDU from device {}.",
          this.deviceIdentification,
          e);
    }
  }

  @Override
  public void connectionClosed(final IOException e) {
    LOGGER.info("Connection with device {} closed.", this.deviceIdentification, e);

    this.connectionCache.removeConnection(this.deviceIdentification);
  }

  public static final class Builder {

    private String deviceIdentification;
    private ClientAsduHandlerRegistry asduHandlerRegistry;
    private ClientConnectionCache connectionCache;
    private LoggingService loggingService;
    private LogItemFactory logItemFactory;
    private ResponseMetadata responseMetadata;
    private ResponseMetadataFactory responseMetadataFactory;

    public Builder withDeviceIdentification(final String deviceIdentification) {
      this.deviceIdentification = deviceIdentification;
      return this;
    }

    public Builder withClientAsduHandlerRegistry(
        final ClientAsduHandlerRegistry asduHandlerRegistry) {
      this.asduHandlerRegistry = asduHandlerRegistry;
      return this;
    }

    public Builder withClientConnectionCache(final ClientConnectionCache connectionCache) {
      this.connectionCache = connectionCache;
      return this;
    }

    public Builder withLoggingService(final LoggingService loggingService) {
      this.loggingService = loggingService;
      return this;
    }

    public Builder withLogItemFactory(final LogItemFactory logItemFactory) {
      this.logItemFactory = logItemFactory;
      return this;
    }

    public Builder withResponseMetadata(final ResponseMetadata responseMetadata) {
      this.responseMetadata = responseMetadata;
      return this;
    }

    public Builder withResponseMetadataFactory(
        final ResponseMetadataFactory responseMetadataFactory) {
      this.responseMetadataFactory = responseMetadataFactory;
      return this;
    }

    public ClientConnectionEventListener build() {
      return new ClientConnectionEventListener(this);
    }
  }
}
