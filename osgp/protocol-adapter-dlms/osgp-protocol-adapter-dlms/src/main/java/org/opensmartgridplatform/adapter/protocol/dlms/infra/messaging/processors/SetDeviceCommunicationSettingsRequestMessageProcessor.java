/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceCommunicationSettingsRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetDeviceCommunicationSettingsRequestMessageProcessor
    extends DeviceRequestMessageProcessor {

  @Autowired private ManagementService managementService;

  public SetDeviceCommunicationSettingsRequestMessageProcessor() {
    super(MessageType.SET_DEVICE_COMMUNICATION_SETTINGS);
  }

  @Override
  protected boolean usesDeviceConnection() {
    return false;
  }

  @Override
  protected Serializable handleMessage(final DlmsDevice device, final Serializable requestObject)
      throws OsgpException {

    this.assertRequestObjectType(SetDeviceCommunicationSettingsRequestDto.class, requestObject);

    final SetDeviceCommunicationSettingsRequestDto deviceCommunicationSettings =
        (SetDeviceCommunicationSettingsRequestDto) requestObject;

    this.managementService.setDeviceCommunicationSettings(device, deviceCommunicationSettings);

    // No response data
    return null;
  }
}
