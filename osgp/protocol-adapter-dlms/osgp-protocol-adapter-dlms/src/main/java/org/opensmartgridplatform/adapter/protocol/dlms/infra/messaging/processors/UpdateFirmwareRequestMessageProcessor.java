// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateFirmwareRequestMessageProcessor extends DeviceRequestMessageProcessor {

  @Autowired private ConfigurationService configurationService;

  protected UpdateFirmwareRequestMessageProcessor() {
    super(MessageType.UPDATE_FIRMWARE);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    final String deviceIdentification = messageMetadata.getDeviceIdentification();

    this.assertRequestObjectType(UpdateFirmwareRequestDto.class, requestObject);

    log.info(
        "{} called for device: {} for organisation: {}",
        messageMetadata.getMessageType(),
        deviceIdentification,
        messageMetadata.getOrganisationIdentification());

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        (UpdateFirmwareRequestDto) requestObject;
    final String firmwareIdentification = getFirmwareIdentification(updateFirmwareRequestDto);

    log.info(
        "[{}] - Firmware file [{}] available. Updating firmware on device [{}]",
        messageMetadata.getCorrelationUid(),
        firmwareIdentification,
        deviceIdentification);
    return this.configurationService.updateFirmware(
        conn, device, updateFirmwareRequestDto, messageMetadata);
  }

  private static String getFirmwareIdentification(final UpdateFirmwareRequestDto requestDto) {
    return requestDto.getUpdateFirmwareRequestDataDto().getFirmwareIdentification();
  }
}
