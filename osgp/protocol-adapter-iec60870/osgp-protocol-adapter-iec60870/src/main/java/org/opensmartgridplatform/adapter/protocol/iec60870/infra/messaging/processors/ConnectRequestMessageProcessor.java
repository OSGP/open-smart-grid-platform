/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors;

import java.io.IOException;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.GeneralInterrogationService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.AbstractMessageProcessor;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing get health status requests. */
@Component
public class ConnectRequestMessageProcessor extends AbstractMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ConnectRequestMessageProcessor.class);

  @Autowired private GeneralInterrogationService generalInterrogationService;

  public ConnectRequestMessageProcessor() {
    super(MessageType.CONNECT);
  }

  @Override
  public void process(
      final ClientConnection deviceConnection, final RequestMetadata requestMetadata)
      throws ProtocolAdapterException {

    final String deviceIdentification = requestMetadata.getDeviceIdentification();
    final String organisationIdentification = requestMetadata.getOrganisationIdentification();

    LOGGER.info(
        "Connect request for IEC60870 device {} for organisation {}",
        deviceIdentification,
        organisationIdentification);
    LOGGER.info("Starting general interrogation for IEC60870 device {}", deviceIdentification);

    try {
      this.generalInterrogationService.sendGeneralInterrogation(deviceConnection, requestMetadata);

    } catch (final IOException | RuntimeException e) {
      final String message =
          String.format(
              "Interrogation after connect request for device %s failed.", deviceIdentification);
      throw new ProtocolAdapterException(ComponentType.PROTOCOL_IEC60870, message, e);
    }
  }
}
