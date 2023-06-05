// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericNotification;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericSendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.SendNotificationRequest;

/**
 * When using the generic notification service, the smart metering specific SendNotificationRequest
 * is mapped to the GenericSendNotificationRequest and back. We test here if that mapping is
 * properly done.
 */
public class NotificationServiceWsTest {

  private static final String MESSAGE = "message";
  private static final String RESULT = "result";
  private static final String DEVICEIDENTIFICATION = "deviceIdentification";
  private static final String CORRELATION_UID = "correlationUid";
  private static final NotificationType NOTIFICATION_TYPE = NotificationType.CLEAR_ALARM_REGISTER;
  private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

  private GenericNotification createGenericNotification() {
    return new GenericNotification(
        MESSAGE, RESULT, DEVICEIDENTIFICATION, CORRELATION_UID, NOTIFICATION_TYPE.toString());
  }

  private Notification createNotification() {
    final Notification notification = new Notification();
    notification.setMessage(MESSAGE);
    notification.setResult(RESULT);
    notification.setDeviceIdentification(DEVICEIDENTIFICATION);
    notification.setCorrelationUid(CORRELATION_UID);
    notification.setNotificationType(NOTIFICATION_TYPE);
    return notification;
  }

  @Test
  public void testMappingFromGenericSendNotificationRequest() {
    final GenericSendNotificationRequest genericRequest =
        new GenericSendNotificationRequest(this.createGenericNotification());

    final SendNotificationRequest sendNotificationRequest =
        this.mapperFactory.getMapperFacade().map(genericRequest, SendNotificationRequest.class);

    final Notification notification = sendNotificationRequest.getNotification();
    assertThat(notification.getMessage()).isEqualTo(MESSAGE);
    assertThat(notification.getResult()).isEqualTo(RESULT);
    assertThat(notification.getDeviceIdentification()).isEqualTo(DEVICEIDENTIFICATION);
    assertThat(notification.getCorrelationUid()).isEqualTo(CORRELATION_UID);
    assertThat(notification.getNotificationType()).isEqualTo(NOTIFICATION_TYPE);
  }

  @Test
  public void testMappingToGenericSendNotificationRequest() {
    final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();
    sendNotificationRequest.setNotification(this.createNotification());

    final GenericSendNotificationRequest genericRequest =
        this.mapperFactory
            .getMapperFacade()
            .map(sendNotificationRequest, GenericSendNotificationRequest.class);

    final GenericNotification genericNotification = genericRequest.getNotification();

    assertThat(genericNotification.getMessage()).isEqualTo(MESSAGE);
    assertThat(genericNotification.getResult()).isEqualTo(RESULT);
    assertThat(genericNotification.getDeviceIdentification()).isEqualTo(DEVICEIDENTIFICATION);
    assertThat(genericNotification.getCorrelationUid()).isEqualTo(CORRELATION_UID);
    assertThat(genericNotification.getNotificationType()).isEqualTo(NOTIFICATION_TYPE.name());
  }
}
