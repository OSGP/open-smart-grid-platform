/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.EnumMap;
import java.util.Map;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.iec60870.exceptions.Iec60870AsduHandlerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Registry of Client Asdu Handlers, used to look up the corresponding handler for a specific Asdu
 * type. Each Client Asdu Handler should register itself to this registry.
 */
@Component
public class ClientAsduHandlerRegistry {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientAsduHandlerRegistry.class);

  private final Map<ASduType, ClientAsduHandler> handlers = new EnumMap<>(ASduType.class);

  public ClientAsduHandler getHandler(final ASdu asdu) throws Iec60870AsduHandlerNotFoundException {
    final ASduType asduType = asdu.getTypeIdentification();
    final ClientAsduHandler handler = this.handlers.get(asduType);
    if (handler == null) {
      LOGGER.error(
          "Unable to process ASDU {}, no ASDU handler found for ASDU type {}", asdu, asduType);
      throw new Iec60870AsduHandlerNotFoundException(asduType);
    }
    return handler;
  }

  public void registerHandler(final ASduType asduType, final ClientAsduHandler clientAsduHandler) {
    this.handlers.put(asduType, clientAsduHandler);
  }
}
