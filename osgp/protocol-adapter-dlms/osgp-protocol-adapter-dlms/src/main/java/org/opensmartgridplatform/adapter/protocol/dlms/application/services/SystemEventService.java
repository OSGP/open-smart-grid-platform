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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SystemEventTypeDto;
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

  private final ExecutorService systemEventExecutorService;
  private final OsgpRequestMessageSender osgpRequestMessageSender;
  private final long maxFramecounterThreshold;

  @Autowired
  public SystemEventService(
      final ExecutorService systemEventExecutorService,
      final OsgpRequestMessageSender osgpRequestMessageSender,
      @Value("${framecounter.max.threshold}") final long maxFramecounterThreshold) {
    this.systemEventExecutorService = systemEventExecutorService;
    this.osgpRequestMessageSender = osgpRequestMessageSender;
    this.maxFramecounterThreshold = maxFramecounterThreshold;
  }

  public void verifyMaxValueReachedEvent(final DlmsDevice device) {
    if (device.getInvocationCounter() <= this.maxFramecounterThreshold) {
      return;
    }

    this.systemEventExecutorService.submit(
        () -> {
          final String message =
              String.format(
                  "Framecounter for device %s, has a higher value %s then the max value threshold configured %s, a event will be published",
                  device.getDeviceIdentification(),
                  device.getInvocationCounter(),
                  this.maxFramecounterThreshold);
          log.info(message);

          final SystemEventDto systemEventDto =
              new SystemEventDto(
                  device.getDeviceIdentification(),
                  SystemEventTypeDto.MAX_FRAMECOUNTER,
                  new Date(),
                  message);

          final String correlationId = UUID.randomUUID().toString().replace("-", "");

          final RequestMessage requestMessage =
              new RequestMessage(
                  correlationId,
                  "no-organisation",
                  device.getDeviceIdentification(),
                  null,
                  systemEventDto);

          final MessageMetadata messageMetadata =
              new Builder().withMessagePriority(MessagePriorityEnum.HIGH.getPriority()).build();

          log.info("Sending system event to GXF with correlation ID: {}", correlationId);
          this.osgpRequestMessageSender.send(
              requestMessage, MessageType.SYSTEM_EVENT.name(), messageMetadata);
        });
  }
}
