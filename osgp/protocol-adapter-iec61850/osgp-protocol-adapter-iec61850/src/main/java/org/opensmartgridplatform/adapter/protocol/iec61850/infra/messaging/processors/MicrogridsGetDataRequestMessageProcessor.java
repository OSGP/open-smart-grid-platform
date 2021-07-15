/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.requests.GetDataDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetDataDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.RtuDeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataRequestDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Class for processing microgrids get data request messages */
@Component("iec61850MicrogridsGetDataRequestMessageProcessor")
public class MicrogridsGetDataRequestMessageProcessor extends RtuDeviceRequestMessageProcessor {
  /** Logger for this class */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(MicrogridsGetDataRequestMessageProcessor.class);

  public MicrogridsGetDataRequestMessageProcessor() {
    super(MessageType.GET_DATA);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing microgrids get data request message");

    MessageMetadata messageMetadata;
    GetDataRequestDto getDataRequest;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      getDataRequest = (GetDataRequestDto) message.getObject();
    } catch (final JMSException e) {
      LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
      return;
    }

    final RequestMessageData requestMessageData =
        RequestMessageData.newBuilder().messageMetadata(messageMetadata).build();

    this.printDomainInfo(requestMessageData);

    final Iec61850DeviceResponseHandler iec61850DeviceResponseHandler =
        this.createIec61850DeviceResponseHandler(requestMessageData, message);

    final DeviceRequest.Builder deviceRequestBuilder =
        DeviceRequest.newBuilder().messageMetaData(messageMetadata);

    this.deviceService.getData(
        new GetDataDeviceRequest(deviceRequestBuilder, getDataRequest),
        iec61850DeviceResponseHandler);
  }

  @Override
  public void handleDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final DomainInformation domainInformation,
      final String messageType,
      final int retryCount,
      final boolean isScheduled) {
    LOGGER.info("Override for handleDeviceResponse() by MicrogridsGetDataRequestMessageProcessor");
    this.handleGetDataDeviceResponse(
        deviceResponse,
        responseMessageSender,
        domainInformation,
        messageType,
        retryCount,
        isScheduled);
  }

  private void handleGetDataDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final DomainInformation domainInformation,
      final String messageType,
      final int retryCount,
      final boolean isScheduled) {

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    OsgpException osgpException = null;
    GetDataResponseDto dataResponse = null;

    try {
      final GetDataDeviceResponse response = (GetDataDeviceResponse) deviceResponse;
      dataResponse = response.getDataResponse();
    } catch (final Exception e) {
      LOGGER.error("Device Response Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      osgpException =
          new TechnicalException(
              ComponentType.PROTOCOL_IEC61850,
              "Unexpected exception while retrieving response message",
              e);
    }

    final DeviceMessageMetadata deviceMessageMetaData =
        new DeviceMessageMetadata(
            deviceResponse.getDeviceIdentification(),
            deviceResponse.getOrganisationIdentification(),
            deviceResponse.getCorrelationUid(),
            messageType,
            deviceResponse.getMessagePriority());
    final ProtocolResponseMessage responseMessage =
        new ProtocolResponseMessage.Builder()
            .domain(domainInformation.getDomain())
            .domainVersion(domainInformation.getDomainVersion())
            .deviceMessageMetadata(deviceMessageMetaData)
            .result(result)
            .osgpException(osgpException)
            .dataObject(dataResponse)
            .retryCount(retryCount)
            .retryHeader(new RetryHeader())
            .scheduled(isScheduled)
            .build();

    responseMessageSender.send(responseMessage);
  }
}
