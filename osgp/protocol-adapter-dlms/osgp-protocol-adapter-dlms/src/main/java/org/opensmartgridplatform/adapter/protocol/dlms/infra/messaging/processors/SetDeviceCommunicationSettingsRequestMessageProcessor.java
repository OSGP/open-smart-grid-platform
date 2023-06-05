// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceCommunicationSettingsRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
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
  protected Serializable handleMessage(
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(SetDeviceCommunicationSettingsRequestDto.class, requestObject);

    final SetDeviceCommunicationSettingsRequestDto deviceCommunicationSettings =
        (SetDeviceCommunicationSettingsRequestDto) requestObject;

    this.managementService.setDeviceCommunicationSettings(device, deviceCommunicationSettings);

    // No response data
    return null;
  }
}
