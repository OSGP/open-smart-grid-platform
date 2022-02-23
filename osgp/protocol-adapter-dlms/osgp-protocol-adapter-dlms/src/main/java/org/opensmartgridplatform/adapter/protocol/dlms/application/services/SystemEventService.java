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
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
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
  private static final AttributeAddress ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE =
      new AttributeAddress(1, new ObisCode(new byte[] {0, 0, 43, 1, 0, -1}), 2);

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
    this.sendMessage(systemEventDto, sourceMessageMetadata, device);
  }

  public boolean receivedInvocationCounterIsLowerThanCurrentValue(
      final DlmsDevice device,
      final MessageMetadata sourceMessageMetadata,
      final DlmsConnectionManager conn) {
    final Optional<Long> receivedInvocationCounter = this.getReceivedInvocationCounter(conn);

    if (device.getInvocationCounter() == null
        || !receivedInvocationCounter.isPresent()
        || device.getInvocationCounter() < receivedInvocationCounter.get()) {
      return false;
    }

    final String message =
        String.format(
            "Received invocation counter for device %s, has a lower value %s than the actual invocation counter of the device %s, an event will be published",
            device.getDeviceIdentification(),
            receivedInvocationCounter.get(),
            device.getInvocationCounter());
    log.info(message);

    final SystemEventDto systemEventDto =
        new SystemEventDto(
            device.getDeviceIdentification(),
            SystemEventTypeDto.ATTEMPT_TO_LOWER_INVOCATION_COUNTER,
            new Date(),
            message);
    this.sendMessage(systemEventDto, sourceMessageMetadata, device);
    return true;
  }

  private void sendMessage(
      final SystemEventDto systemEventDto,
      final MessageMetadata sourceMessageMetadata,
      final DlmsDevice device) {
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

    final RequestMessage requestMessage = new RequestMessage(messageMetadata, systemEventDto);

    log.info("Sending system event to GXF with correlation ID: {}", correlationId);
    this.osgpRequestMessageSender.send(
        requestMessage, MessageType.SYSTEM_EVENT.name(), messageMetadata);
  }

  private Optional<Long> getReceivedInvocationCounter(
      final DlmsConnectionManager connectionManager) {
    final DlmsHelper dlmsHelper = new DlmsHelper();
    try {
      final Number invocationCounter =
          dlmsHelper
              .getAttributeValue(connectionManager, ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE)
              .getValue();
      return Optional.of(invocationCounter.longValue());
    } catch (final Exception e) {
      return Optional.empty();
    }
  }
}
