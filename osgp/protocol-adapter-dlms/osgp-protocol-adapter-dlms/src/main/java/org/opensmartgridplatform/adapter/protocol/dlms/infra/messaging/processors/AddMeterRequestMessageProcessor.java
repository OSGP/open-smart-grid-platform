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

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.InstallationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RequestWithMetadata;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class for processing add meter request messages */
@Component
public class AddMeterRequestMessageProcessor
    extends DeviceRequestMessageProcessor<SmartMeteringDeviceDto> {

  @Autowired private InstallationService installationService;

  public AddMeterRequestMessageProcessor() {
    super(MessageType.ADD_METER);
  }

  @Override
  protected boolean usesDeviceConnection() {
    return false;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsDevice device, final RequestWithMetadata<SmartMeteringDeviceDto> request)
      throws OsgpException {
    final String correlationUid = request.getMetadata().getCorrelationUid();
    this.installationService.addMeter(correlationUid, request.getRequestObject());

    // No return object.
    return null;
  }

  @Override
  protected boolean requiresExistingDevice() {
    return false;
  }
}
