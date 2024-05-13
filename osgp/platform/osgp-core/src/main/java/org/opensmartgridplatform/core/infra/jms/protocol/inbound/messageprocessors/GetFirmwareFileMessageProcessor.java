// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.core.domain.model.protocol.ProtocolResponseService;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.AbstractProtocolRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.valueobjects.FirmwareFileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("getFirmwareFileMessageProcessor")
public class GetFirmwareFileMessageProcessor extends AbstractProtocolRequestMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetFirmwareFileMessageProcessor.class);

  @Autowired private ProtocolResponseService protocolResponseMessageSender;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private FirmwareFileRepository firmwareFileRepository;

  protected GetFirmwareFileMessageProcessor() {
    super(MessageType.GET_FIRMWARE_FILE);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {

    MessageMetadata metadata = null;
    Device device = null;
    String firmwareFileIdentification = StringUtils.EMPTY;

    try {
      metadata = MessageMetadata.fromMessage(message);
      LOGGER.info(
          "[{}] - Received message of messageType: {}, organisationIdentification: {}, deviceIdentification: {}",
          metadata.getCorrelationUid(),
          metadata.getMessageType(),
          metadata.getOrganisationIdentification(),
          metadata.getDeviceIdentification());

      device = this.deviceRepository.findByDeviceIdentification(metadata.getDeviceIdentification());

      final RequestMessage requestMessage = (RequestMessage) message.getObject();

      final UpdateFirmwareRequestDto updateFirmwareRequestDto =
          (UpdateFirmwareRequestDto) requestMessage.getRequest();
      firmwareFileIdentification =
          updateFirmwareRequestDto.getUpdateFirmwareRequestDataDto().getFirmwareIdentification();

      final FirmwareFile firmwareFile =
          this.firmwareFileRepository.findByIdentificationOnly(firmwareFileIdentification);

      final FirmwareFileDto firmwareFileDto =
          new FirmwareFileDto(
              firmwareFileIdentification,
              updateFirmwareRequestDto.getDeviceIdentification(),
              firmwareFile.getFile(),
              firmwareFile.getImageIdentifier());

      this.sendSuccessResponse(
          metadata, device.getProtocolInfo(), firmwareFileDto, message.getJMSReplyTo());

    } catch (final Exception e) {
      LOGGER.error("Exception while retrieving firmware file: {}", firmwareFileIdentification);
      final OsgpException osgpException =
          new OsgpException(
              ComponentType.OSGP_CORE, "Exception while retrieving firmware file.", e);
      if (device != null) {
        this.sendFailureResponse(
            metadata, device.getProtocolInfo(), osgpException, message.getJMSReplyTo());
      } else {
        LOGGER.error("Unable to send failure response because device is null", osgpException);
      }
    }
  }

  private void sendSuccessResponse(
      final MessageMetadata metadata,
      final ProtocolInfo protocolInfo,
      final FirmwareFileDto firmwareFileDto,
      final Destination destination) {

    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(metadata)
            .result(ResponseMessageResultType.OK)
            .osgpException(null)
            .dataObject(firmwareFileDto)
            .build();

    this.protocolResponseMessageSender.sendWithDestination(
        responseMessage,
        DeviceFunction.GET_FIRMWARE_FILE.name(),
        protocolInfo,
        metadata,
        destination);
  }

  private void sendFailureResponse(
      final MessageMetadata metadata,
      final ProtocolInfo protocolInfo,
      final OsgpException exception,
      final Destination destination) {

    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(metadata)
            .result(ResponseMessageResultType.NOT_OK)
            .osgpException(exception)
            .dataObject(null)
            .build();

    this.protocolResponseMessageSender.sendWithDestination(
        responseMessage,
        DeviceFunction.GET_FIRMWARE_FILE.name(),
        protocolInfo,
        metadata,
        destination);
  }
}
