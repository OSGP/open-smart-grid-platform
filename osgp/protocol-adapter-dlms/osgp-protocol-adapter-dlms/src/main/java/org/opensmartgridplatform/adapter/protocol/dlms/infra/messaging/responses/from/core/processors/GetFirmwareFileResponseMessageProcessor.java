// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core.processors;

import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.FirmwareService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.SilentException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ThrowingConsumer;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.responses.from.core.OsgpResponseMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.FirmwareFileDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetFirmwareFileResponseMessageProcessor extends OsgpResponseMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetFirmwareFileResponseMessageProcessor.class);

  @Autowired private FirmwareService firmwareService;

  protected GetFirmwareFileResponseMessageProcessor() {
    super(MessageType.GET_FIRMWARE_FILE);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Processing {} response message", this.messageType.name());
    }

    // Get metadata from message and update message type to update
    // firmware
    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder(MessageMetadata.fromMessage(message))
            .withMessageType(MessageType.UPDATE_FIRMWARE.name())
            .build();
    final Serializable messageObject = message.getObject();

    final ThrowingConsumer<DlmsConnectionManager> taskForConnectionManager =
        conn -> this.processMessageTasks(messageObject, messageMetadata, conn);

    try {
      this.createAndHandleConnectionForDevice(
          this.domainHelperService.findDlmsDevice(messageMetadata),
          messageMetadata,
          taskForConnectionManager);
    } catch (final OsgpException e) {
      LOGGER.error("Something went wrong with the DlmsConnection", e);
    }
  }

  @SuppressWarnings(
      "squid:S1193") // SilentException cannot be caught since it does not extend Exception.
  void processMessageTasks(
      final Serializable messageObject,
      final MessageMetadata messageMetadata,
      final DlmsConnectionManager conn)
      throws OsgpException {
    try {
      final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

      LOGGER.info(
          "{} called for device: {} for organisation: {}",
          messageMetadata.getMessageType(),
          messageMetadata.getDeviceIdentification(),
          messageMetadata.getOrganisationIdentification());

      final Serializable response;

      response = this.handleMessage(conn, device, messageObject);

      // Send response
      this.sendResponseMessage(
          messageMetadata,
          ResponseMessageResultType.OK,
          null,
          this.responseMessageSender,
          response);

    } catch (final Exception exception) {
      // Return original request + exception
      if (!(exception instanceof SilentException)) {
        LOGGER.error("Unexpected exception during {}", this.messageType.name(), exception);
      }

      this.sendResponseMessage(
          messageMetadata,
          ResponseMessageResultType.NOT_OK,
          exception,
          this.responseMessageSender,
          this.createUpdateFirmwareRequestDto(messageObject));
    } finally {
      final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);
      this.doConnectionPostProcessing(device, conn, messageMetadata);
    }
  }

  private UpdateFirmwareRequestDto createUpdateFirmwareRequestDto(
      final Serializable messageObject) {

    if (messageObject instanceof ProtocolResponseMessage) {

      final ProtocolResponseMessage protocolResponseMessage =
          (ProtocolResponseMessage) messageObject;
      final Serializable dataObject = protocolResponseMessage.getDataObject();

      if (dataObject instanceof FirmwareFileDto) {

        final FirmwareFileDto firmwareFileDto = (FirmwareFileDto) dataObject;

        return new UpdateFirmwareRequestDto(
            firmwareFileDto.getFirmwareIdentification(),
            protocolResponseMessage.getDeviceIdentification());
      }
    }

    LOGGER.warn("Firmware Identification not present.");
    return null;
  }

  @Override
  protected Serializable handleMessage(
      final DlmsConnectionManager conn, final DlmsDevice device, final Serializable response)
      throws OsgpException {

    if (!(response instanceof ResponseMessage)) {
      throw new ProtocolAdapterException("Invalid response type, expected ResponseMessage object.");
    }

    final ResponseMessage responseMessage = (ResponseMessage) response;

    if (ResponseMessageResultType.OK.equals(responseMessage.getResult())) {
      final FirmwareFileDto firmwareFileDto = (FirmwareFileDto) responseMessage.getDataObject();
      final MessageMetadata messageMetadata =
          this.messageMetadataFromResponseMessage(responseMessage);

      return this.firmwareService.updateFirmware(conn, device, firmwareFileDto, messageMetadata);
    } else {
      throw new ProtocolAdapterException(
          "Get Firmware File failed.", responseMessage.getOsgpException());
    }
  }

  private MessageMetadata messageMetadataFromResponseMessage(
      final ResponseMessage responseMessage) {
    return MessageMetadata.newBuilder()
        .withCorrelationUid(responseMessage.getCorrelationUid())
        .withDeviceIdentification(responseMessage.getDeviceIdentification())
        .withMessageType(responseMessage.getMessageType())
        .withOrganisationIdentification(responseMessage.getOrganisationIdentification())
        .build();
  }
}
