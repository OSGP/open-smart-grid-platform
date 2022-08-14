/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateProtocolRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateProtocolRequestMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private ManagementService managementService;

  public UpdateProtocolRequestMessageProcessor() {
    super(MessageType.UPDATE_PROTOCOL);
  }

  @Override
  protected boolean usesDeviceConnection() {
    return false;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(UpdateProtocolRequestDto.class, requestObject);

    final UpdateProtocolRequestDto updateProtocolRequestDto =
        (UpdateProtocolRequestDto) requestObject;

    return this.managementService.updateProtocol(device, updateProtocolRequestDto);
  }
}
