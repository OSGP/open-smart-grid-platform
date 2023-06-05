// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import java.io.Serializable;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetFirmwareVersionDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.SsldDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing common get firmware request messages */
@Component("iec61850CommonGetFirmwareRequestMessageProcessor")
public class CommonGetFirmwareRequestMessageProcessor extends SsldDeviceRequestMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CommonGetFirmwareRequestMessageProcessor.class);

  public CommonGetFirmwareRequestMessageProcessor() {
    super(MessageType.GET_FIRMWARE_VERSION);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing common get firmware request message");

    final MessageMetadata messageMetadata;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    final RequestMessageData requestMessageData =
        RequestMessageData.newBuilder().messageMetadata(messageMetadata).build();

    this.printDomainInfo(requestMessageData);

    final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler =
        this.createIec61850DeviceResponseHandler(requestMessageData, message);

    final DeviceRequest deviceRequest =
        DeviceRequest.newBuilder().messageMetaData(messageMetadata).build();

    this.deviceService.getFirmwareVersion(deviceRequest, iec61850DeviceResponseHandler);
  }

  @Override
  public void handleDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final DomainInformation domainInformation,
      final String messageType,
      final int retryCount,
      final boolean isScheduled) {
    LOGGER.info("Override for handleDeviceResponse() by CommonGetFirmwareRequestMessageProcessor");
    this.handleGetFirmwareVersionDeviceResponse(
        deviceResponse,
        responseMessageSender,
        domainInformation,
        messageType,
        retryCount,
        isScheduled);
  }

  private void handleGetFirmwareVersionDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final DomainInformation domainInformation,
      final String messageType,
      final int retryCount,
      final boolean isScheduled) {

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    OsgpException osgpException = null;
    List<FirmwareVersionDto> firmwareVersions = null;

    try {
      firmwareVersions = ((GetFirmwareVersionDeviceResponse) deviceResponse).getFirmwareVersions();
    } catch (final Exception e) {
      LOGGER.error("Device Response Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      osgpException =
          new TechnicalException(
              ComponentType.UNKNOWN, "Unexpected exception while retrieving response message", e);
    }

    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder()
            .withDeviceIdentification(deviceResponse.getDeviceIdentification())
            .withOrganisationIdentification(deviceResponse.getOrganisationIdentification())
            .withCorrelationUid(deviceResponse.getCorrelationUid())
            .withMessageType(messageType)
            .withDomain(domainInformation.getDomain())
            .withDomainVersion(domainInformation.getDomainVersion())
            .withMessagePriority(deviceResponse.getMessagePriority())
            .withScheduled(isScheduled)
            .withRetryCount(retryCount)
            .build();
    final ProtocolResponseMessage responseMessage =
        ProtocolResponseMessage.newBuilder()
            .messageMetadata(messageMetadata)
            .result(result)
            .osgpException(osgpException)
            .dataObject((Serializable) firmwareVersions)
            .build();

    responseMessageSender.send(responseMessage);
  }
}
