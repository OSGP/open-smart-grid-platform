// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.InstallationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceRequestDataDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class CoupleMbusDeviceRequestMessageProcessor extends DeviceRequestMessageProcessor {

  private final InstallationService installationService;

  protected CoupleMbusDeviceRequestMessageProcessor(final InstallationService installationService) {
    super(MessageType.COUPLE_MBUS_DEVICE);
    this.installationService = installationService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(CoupleMbusDeviceRequestDataDto.class, requestObject);

    final CoupleMbusDeviceRequestDataDto requestDto =
        (CoupleMbusDeviceRequestDataDto) requestObject;
    return this.installationService.coupleMbusDevice(conn, device, requestDto, messageMetadata);
  }
}
