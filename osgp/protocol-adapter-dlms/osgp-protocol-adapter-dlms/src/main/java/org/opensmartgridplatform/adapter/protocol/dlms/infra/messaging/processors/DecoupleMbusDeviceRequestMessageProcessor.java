// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.InstallationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.stereotype.Component;

@Component
public class DecoupleMbusDeviceRequestMessageProcessor extends DeviceRequestMessageProcessor {

  private final InstallationService installationService;

  protected DecoupleMbusDeviceRequestMessageProcessor(
      final InstallationService installationService) {
    super(MessageType.DECOUPLE_MBUS_DEVICE);
    this.installationService = installationService;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    this.assertRequestObjectType(DecoupleMbusDeviceDto.class, requestObject);

    final DecoupleMbusDeviceDto decoupleMbusDeviceDto = (DecoupleMbusDeviceDto) requestObject;
    return this.installationService.decoupleMbusDevice(
        conn, device, decoupleMbusDeviceDto, messageMetadata);
  }
}
