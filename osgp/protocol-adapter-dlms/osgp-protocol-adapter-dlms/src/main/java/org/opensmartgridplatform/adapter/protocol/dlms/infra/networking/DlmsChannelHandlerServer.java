/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetSocketAddress;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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

@Sharable
@Slf4j
public class DlmsChannelHandlerServer extends DlmsChannelHandler {

  private static final String PUSH_ALARM_TRIGGER = "Push alarm monitor";
  private static final String PUSH_SMS_TRIGGER = "Push sms wakeup";

  @Autowired private OsgpRequestMessageSender osgpRequestMessageSender;

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, final DlmsPushNotification message) {

    final String correlationId = UUID.randomUUID().toString().replace("-", "");
    final String deviceIdentification = message.getEquipmentIdentifier();
    final String ipAddress = this.retrieveIpAddress(ctx, deviceIdentification);

    this.processPushedMessage(message, correlationId, deviceIdentification, ipAddress);
  }

  private void processPushedMessage(
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

  private String retrieveIpAddress(
      final ChannelHandlerContext ctx, final String deviceIdentification) {
    String ipAddress = null;
    try {
      ipAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
      log.info(
          "Push notification for device {} received from IP address {}",
          deviceIdentification,
          ipAddress);
    } catch (final Exception ex) {
      log.info("Unable to determine IP address of the meter sending a push notification: ", ex);
    }
    return ipAddress;
  }
}
