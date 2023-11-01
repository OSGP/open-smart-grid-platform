// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.FirmwareService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateFirmwareRequestMessageProcessor extends DeviceRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(UpdateFirmwareRequestMessageProcessor.class);

  @Autowired private ConfigurationService configurationService;

  @Autowired private FirmwareService firmwareService;

  @Autowired private OsgpRequestMessageSender osgpRequestMessageSender;

  protected UpdateFirmwareRequestMessageProcessor() {
    super(MessageType.UPDATE_FIRMWARE);
  }

  @Override
  protected boolean usesDeviceConnection(final Serializable messageObject) {
    if (messageObject instanceof final UpdateFirmwareRequestDto requestDto) {
      final String firmwareIdentification = requestDto.getFirmwareIdentification();
      final boolean usesDeviceConnection =
          this.firmwareService.isFirmwareFileAvailable(firmwareIdentification);
      if (!usesDeviceConnection) {
        LOGGER.info(
            "Firmware file [{}] not available for device {}. So no device connection required for sending GetFirmwareFile request to core.",
            firmwareIdentification,
            requestDto.getDeviceIdentification());
      }
      return usesDeviceConnection;
    }
    return super.usesDeviceConnection(messageObject);
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Serializable requestObject,
      final MessageMetadata messageMetadata)
      throws OsgpException {
    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    final String organisationIdentification = messageMetadata.getOrganisationIdentification();
    final String correlationUid = messageMetadata.getCorrelationUid();
    final String messageType = messageMetadata.getMessageType();

    this.assertRequestObjectType(UpdateFirmwareRequestDto.class, requestObject);

    LOGGER.info(
        "{} called for device: {} for organisation: {}",
        messageType,
        deviceIdentification,
        organisationIdentification);

    final UpdateFirmwareRequestDto updateFirmwareRequestDto =
        (UpdateFirmwareRequestDto) requestObject;
    final String firmwareIdentification = updateFirmwareRequestDto.getFirmwareIdentification();

    if (this.firmwareService.isFirmwareFileAvailable(firmwareIdentification)) {
      LOGGER.info(
          "[{}] - Firmware file [{}] available. Updating firmware on device [{}]",
          correlationUid,
          firmwareIdentification,
          deviceIdentification);
      return this.configurationService.updateFirmware(
          conn, device, updateFirmwareRequestDto, messageMetadata);
    } else {
      LOGGER.info(
          "[{}] - Firmware file [{}] not available. Sending GetFirmwareFile request to core.",
          correlationUid,
          firmwareIdentification);
      final RequestMessage message =
          new RequestMessage(
              correlationUid,
              organisationIdentification,
              deviceIdentification,
              updateFirmwareRequestDto);
      this.osgpRequestMessageSender.sendWithReplyToThisInstance(
          message, MessageType.GET_FIRMWARE_FILE.name(), messageMetadata);
      return NO_RESPONSE;
    }
  }
}
