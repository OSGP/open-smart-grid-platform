/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.in.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.NotificationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationAlarmDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Class for processing smart metering push notification alarm request messages. */
@Component
public class PushNotificationAlarmRequestMessageProcessor extends BaseRequestMessageProcessor {

  @Autowired private NotificationService notificationService;

  @Autowired
  protected PushNotificationAlarmRequestMessageProcessor(
      @Qualifier("domainSmartMeteringInboundOsgpCoreRequestsMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(messageProcessorMap, MessageType.PUSH_NOTIFICATION_ALARM);
  }

  @Override
  protected void handleMessage(
      final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
      throws FunctionalException {

    this.notificationService.handlePushNotificationAlarm(
        deviceMessageMetadata,
        (PushNotificationAlarmDto) ((RequestMessage) dataObject).getRequest());
  }
}
