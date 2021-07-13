/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.SsldDeviceService;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetStatusDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DomainInformation;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
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
public abstract class SsldDeviceRequestMessageProcessor extends BaseMessageProcessor {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SsldDeviceRequestMessageProcessor.class);

  @Autowired protected SsldDeviceService deviceService;

  /**
   * Each MessageProcessor should register it's MessageType at construction.
   *
   * @param messageType The MessageType the MessageProcessor implementation can process.
   */
  protected SsldDeviceRequestMessageProcessor(final MessageType messageType) {
    this.messageType = messageType;
  }

  /**
   * Initialization function executed after dependency injection has finished. The MessageProcessor
   * Singleton is added to the HashMap of MessageProcessors.
   */
  @PostConstruct
  public void init() {
    this.iec61850RequestMessageProcessorMap.addMessageProcessor(this.messageType, this);
  }

  // This function is used in 3 domains.
  protected void handleGetStatusDeviceResponse(
      final DeviceResponse deviceResponse,
      final ResponseMessageSender responseMessageSender,
      final DomainInformation domainInformation,
      final String messageType,
      final int retryCount,
      final boolean isScheduled) {
    LOGGER.info(
        "Handling getStatusDeviceResponse for device: {}",
        deviceResponse.getDeviceIdentification());
    if (StringUtils.isEmpty(deviceResponse.getCorrelationUid())) {
      LOGGER.warn(
          "CorrelationUID is null or empty, not sending GetStatusResponse message for GetStatusRequest message for device: {}",
          deviceResponse.getDeviceIdentification());
      return;
    }

    final GetStatusDeviceResponse response = (GetStatusDeviceResponse) deviceResponse;
    final DeviceStatusDto status = response.getDeviceStatus();

    final MessageMetadata deviceMessageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceResponse.getDeviceIdentification())
            .withOrganisationIdentification(deviceResponse.getOrganisationIdentification())
            .withCorrelationUid(deviceResponse.getCorrelationUid())
            .withMessageType(messageType)
            .withMessagePriority(response.getMessagePriority())
            .build();
    final ProtocolResponseMessage protocolResponseMessage =
        new ProtocolResponseMessage.Builder()
            .domain(domainInformation.getDomain())
            .domainVersion(domainInformation.getDomainVersion())
            .messageMetadata(deviceMessageMetadata)
            .result(ResponseMessageResultType.OK)
            .osgpException(null)
            .retryCount(retryCount)
            .dataObject(status)
            .scheduled(isScheduled)
            .retryHeader(new RetryHeader())
            .build();
    responseMessageSender.send(protocolResponseMessage);
  }
}
