/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu.DaDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu.DaDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu.DaRtuDeviceService;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.Function;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.RequestMessageData;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceResponseHandler;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.RetryHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor implementation should be
 * annotated with @Component. Further the MessageType the MessageProcessor implementation can
 * process should be passed in at construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class DaRtuDeviceRequestMessageProcessor extends BaseMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DaRtuDeviceRequestMessageProcessor.class);

  @Autowired protected DaRtuDeviceService deviceService;

  /**
   * Each MessageProcessor should register it's MessageType at construction.
   *
   * @param messageType The MessageType the MessageProcessor implementation can process.
   */
  protected DaRtuDeviceRequestMessageProcessor(final MessageType messageType) {
    this.messageType = messageType;
  }

  /**
   * Generic function to get the data from the rtu based on the device connection details and the
   * deviceRequest. Must be implemented in each concrete MessageProcessor
   */
  public abstract <T> Function<T> getDataFunction(
      Iec61850Client iec61850Client, DeviceConnection connection, DaDeviceRequest deviceRequest);

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    this.iec61850RequestMessageProcessorMap.addMessageProcessor(this.messageType, this);
  }

  @Override
  public void processMessage(final ObjectMessage message) throws JMSException {
    LOGGER.debug("Processing distribution automation request message");

    MessageMetadata messageMetadata;
    Serializable request;
    try {
      messageMetadata = MessageMetadata.fromMessage(message);
      request = message.getObject();
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
        new DaDeviceRequest(deviceRequestBuilder, request), iec61850DeviceResponseHandler, this);
  }

  /** Override to include the data in the response */
  @Override
  public void handleDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final DomainInformation domainInformation,
      final String messageType,
      final int retryCount,
      final boolean isScheduled) {

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    OsgpException ex = null;
    Serializable dataObject = null;

    try {
      final DaDeviceResponse response = (DaDeviceResponse) deviceResponse;
      this.deviceResponseService.handleDeviceMessageStatus(response.getStatus());
      dataObject = response.getDataResponse();
    } catch (final OsgpException e) {
      LOGGER.error("Device Response Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      ex = e;
    }

    final DeviceMessageMetadata deviceMessageMetadata =
        new DeviceMessageMetadata(
            deviceResponse.getDeviceIdentification(),
            deviceResponse.getOrganisationIdentification(),
            deviceResponse.getCorrelationUid(),
            messageType);
    final ProtocolResponseMessage protocolResponseMessage =
        new ProtocolResponseMessage.Builder()
            .domain(domainInformation.getDomain())
            .domainVersion(domainInformation.getDomainVersion())
            .deviceMessageMetadata(deviceMessageMetadata)
            .result(result)
            .osgpException(ex)
            .dataObject(dataObject)
            .retryCount(retryCount)
            .retryHeader(new RetryHeader())
            .scheduled(isScheduled)
            .build();
    responseMessageSender.send(protocolResponseMessage);
  }
}
