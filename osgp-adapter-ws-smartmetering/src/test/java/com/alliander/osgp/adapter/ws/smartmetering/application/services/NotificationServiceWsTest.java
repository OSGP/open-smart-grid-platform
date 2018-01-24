/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.shared.notification.GenericNotification;
import com.alliander.osgp.adapter.ws.schema.shared.notification.GenericSendNotificationRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.Notification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.SendNotificationRequest;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * When using the generic notification service, the smart metering specific
 * SendNotificationRequest is mapped to the GenericSendNotificationRequest and
 * back. We test here if that mapping is properly done.
 *
 */
public class NotificationServiceWsTest {

    private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    private static final String MESSAGE = "message";
    private static final String RESULT = "result";
    private static final String DEVICEIDENTIFICATION = "deviceIdentification";
    private static final String CORRELATION_UID = "correlationUid";
    private static final NotificationType NOTIFICATION_TYPE = NotificationType.CLEAR_ALARM_REGISTER;

    @Test
    public void testMappingToGenericSendNotificationRequest() {
        final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();
        sendNotificationRequest.setNotification(this.createNotification());

        final GenericSendNotificationRequest genericRequest = this.mapperFactory.getMapperFacade()
                .map(sendNotificationRequest, GenericSendNotificationRequest.class);

        final GenericNotification genericNotification = genericRequest.getNotification();

        assertEquals(MESSAGE, genericNotification.getMessage());
        assertEquals(RESULT, genericNotification.getResult());
        assertEquals(DEVICEIDENTIFICATION, genericNotification.getDeviceIdentification());
        assertEquals(CORRELATION_UID, genericNotification.getCorrelationUid());
        assertEquals(NOTIFICATION_TYPE.toString(), genericNotification.getNotificationType());
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
        final GenericSendNotificationRequest genericRequest = new GenericSendNotificationRequest();
        genericRequest.setNotification(this.createGenericNotification());

        final SendNotificationRequest sendNotificationRequest = this.mapperFactory.getMapperFacade().map(genericRequest,
                SendNotificationRequest.class);

        final Notification notification = sendNotificationRequest.getNotification();
        assertEquals(MESSAGE, notification.getMessage());
        assertEquals(RESULT, notification.getResult());
        assertEquals(DEVICEIDENTIFICATION, notification.getDeviceIdentification());
        assertEquals(CORRELATION_UID, notification.getCorrelationUid());
        assertEquals(NOTIFICATION_TYPE, notification.getNotificationType());
    }

    private GenericNotification createGenericNotification() {
        final GenericNotification genericNotification = new GenericNotification();
        genericNotification.setMessage(MESSAGE);
        genericNotification.setResult(RESULT);
        genericNotification.setDeviceIdentification(DEVICEIDENTIFICATION);
        genericNotification.setCorrelationUid(CORRELATION_UID);
        genericNotification.setNotificationType(NOTIFICATION_TYPE.toString());
        return genericNotification;
    }

}
