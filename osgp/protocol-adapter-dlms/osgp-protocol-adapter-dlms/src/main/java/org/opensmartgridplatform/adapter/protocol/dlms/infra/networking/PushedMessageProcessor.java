//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.opensmartgridplatform.dlms.DlmsPushNotification;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationSmsDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata.Builder;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushedMessageProcessor {

  private static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";
  private static final String PUSH_SMS_TRIGGER = "Push sms wakeup";

  @Autowired private OsgpRequestMessageSender osgpRequestMessageSender;
  @Autowired private DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender;

  public void process(
      final DlmsPushNotification message,
      final String correlationId,
      final String deviceIdentification,
      final String ipAddress) {
    if (PUSH_SMS_TRIGGER.equals(message.getTriggerType())) {
      this.processPushedSms(message, correlationId, deviceIdentification, ipAddress);

    } else if (PUSH_ALARM_TRIGGER.equals(message.getTriggerType())) {
      this.processPushedAlarm(message, correlationId, deviceIdentification, ipAddress);

    } else {
      log.info("Unknown received message, skip processing");
    }
  }

  private void processPushedAlarm(
      final DlmsPushNotification message,
      final String correlationId,
      final String deviceIdentification,
      final String ipAddress) {
    this.logMessage(message);

    final PushNotificationAlarmDto pushNotificationAlarm =
        new PushNotificationAlarmDto(
            deviceIdentification, message.getAlarms(), message.toByteArray());

    final RequestMessage requestMessage =
        new RequestMessage(
            correlationId,
            "no-organisation",
            deviceIdentification,
            ipAddress,
            null,
            null,
            pushNotificationAlarm);

    final MessageMetadata messageMetadata =
        new Builder().withMessagePriority(MessagePriorityEnum.HIGH.getPriority()).build();

    log.info("Sending push notification alarm to GXF with correlation ID: {}", correlationId);
    this.osgpRequestMessageSender.send(
        requestMessage, MessageType.PUSH_NOTIFICATION_ALARM.name(), messageMetadata);
  }

  private void processPushedSms(
      final DlmsPushNotification message,
      final String correlationId,
      final String deviceIdentification,
      final String ipAddress) {
    this.logMessage(message);

    final PushNotificationSmsDto pushNotificationSms =
        new PushNotificationSmsDto(deviceIdentification, ipAddress);

    final RequestMessage requestMessage =
        new RequestMessage(
            correlationId,
            "no-organisation",
            deviceIdentification,
            ipAddress,
            null,
            null,
            pushNotificationSms);

    log.info("Sending push notification sms wakeup to GXF with correlation ID: {}", correlationId);
    this.osgpRequestMessageSender.send(
        requestMessage, MessageType.PUSH_NOTIFICATION_SMS.name(), null);
  }

  protected void logMessage(final DlmsPushNotification message) {

    final DlmsLogItemRequestMessage dlmsLogItemRequestMessage =
        new DlmsLogItemRequestMessage(
            message.getEquipmentIdentifier(), true, message.isValid(), message, message.getSize());

    this.dlmsLogItemRequestMessageSender.send(dlmsLogItemRequestMessage);
  }
}
