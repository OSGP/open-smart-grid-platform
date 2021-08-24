/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventTypeDto;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata.Builder;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SystemEventService {

  private final OsgpRequestMessageSender osgpRequestMessageSender;
  private final CorrelationIdProviderService correlationIdProviderService;
  private final long invocationCounterEventThreshold;

  @Autowired
  public SystemEventService(
      final OsgpRequestMessageSender osgpRequestMessageSender,
      final CorrelationIdProviderService correlationIdProviderService,
      @Value("${invocation_counter.event.threshold}") final long maxInvocationCounterThreshold) {
    this.osgpRequestMessageSender = osgpRequestMessageSender;
    this.correlationIdProviderService = correlationIdProviderService;
    this.invocationCounterEventThreshold = maxInvocationCounterThreshold;
  }

  public void verifySystemEventThresholdReachedEvent(
      final DlmsDevice device, final MessageMetadata sourceMessageMetadata) {
    if (device.getInvocationCounter() == null
        || device.getInvocationCounter() < this.invocationCounterEventThreshold) {
      return;
    }

    final String message =
        String.format(
            "Invocation counter for device %s, has a higher value %s than the threshold configured %s, an event will be published",
            device.getDeviceIdentification(),
            device.getInvocationCounter(),
            this.invocationCounterEventThreshold);
    log.info(message);

    final SystemEventDto systemEventDto =
        new SystemEventDto(
            device.getDeviceIdentification(),
            SystemEventTypeDto.INVOCATION_COUNTER_THRESHOLD_REACHED,
            new Date(),
            message);

    final String correlationId =
        this.correlationIdProviderService.getCorrelationId(
            sourceMessageMetadata.getOrganisationIdentification(),
            device.getDeviceIdentification());

    final MessageMetadata messageMetadata =
        new Builder()
            .withDeviceIdentification(device.getDeviceIdentification())
            .withCorrelationUid(correlationId)
            .withOrganisationIdentification(sourceMessageMetadata.getOrganisationIdentification())
            .withIpAddress(sourceMessageMetadata.getIpAddress())
            .withMessagePriority(MessagePriorityEnum.HIGH.getPriority())
            .withMessageType(MessageType.SYSTEM_EVENT.name())
            .withDomain(sourceMessageMetadata.getDomain())
            .withDomainVersion(sourceMessageMetadata.getDomainVersion())
            .build();

    final RequestMessage requestMessage =
        new RequestMessage(
            messageMetadata.getCorrelationUid(),
            messageMetadata.getOrganisationIdentification(),
            messageMetadata.getDeviceIdentification(),
            messageMetadata.getIpAddress(),
            systemEventDto);

    log.info("Sending system event to GXF with correlation ID: {}", correlationId);
    this.osgpRequestMessageSender.send(
        requestMessage, MessageType.SYSTEM_EVENT.name(), messageMetadata);
  }
}
