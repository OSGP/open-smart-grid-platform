// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestDataDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetDeviceLifecycleStatusByChannelRequestMessageProcessor
    extends DeviceRequestMessageProcessor {

  @Autowired private ManagementService managementService;

  public SetDeviceLifecycleStatusByChannelRequestMessageProcessor() {
    super(MessageType.SET_DEVICE_LIFECYCLE_STATUS_BY_CHANNEL);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(
        SetDeviceLifecycleStatusByChannelRequestDataDto.class, requestObject);
    final SetDeviceLifecycleStatusByChannelRequestDataDto requestDto =
        (SetDeviceLifecycleStatusByChannelRequestDataDto) requestObject;
    return this.managementService.setDeviceLifecycleStatusByChannel(
        conn, device, requestDto, messageMetadata);
  }
}
