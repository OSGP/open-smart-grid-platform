/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.AdhocService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RequestWithMetadata;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing Synchronize Time Request messages */
@Component
public class SynchronizeTimeRequestMessageProcessor
    extends DeviceRequestMessageProcessor<SynchronizeTimeRequestDto> {

  @Autowired private AdhocService adhocService;

  public SynchronizeTimeRequestMessageProcessor() {
    super(MessageType.SYNCHRONIZE_TIME);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final RequestWithMetadata<SynchronizeTimeRequestDto> request)
      throws ProtocolAdapterException {
    this.adhocService.synchronizeTime(conn, device, request.getRequestObject());
    return null;
  }
}
