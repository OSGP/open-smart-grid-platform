/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.Notification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.adapter.ws.smartmetering.exceptions.WebServiceSecurityException;
import com.alliander.osgp.adapter.ws.smartmetering.infra.ws.SendNotificationServiceClient;

@Transactional(value = "transactionManager")
@Validated
public class NotificationServiceWs implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceWs.class);

    private SendNotificationServiceClient sendNotificationServiceClient;

    private String notificationUrl;

    public NotificationServiceWs(final SendNotificationServiceClient client, final String notificationUrl) {
        this.sendNotificationServiceClient = client;
        this.notificationUrl = notificationUrl;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.alliander.osgp.adapter.ws.smartmetering.application.services.
     * INotificationService#sendNotification(java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String,
     * com.alliander.osgp.adapter
     * .ws.schema.smartmetering.notification.NotificationType)
     */
    @Override
    public void sendNotification(final String organisationIdentification, final String deviceIdentification,
            final String result, final String correlationUid, final String message,
            final NotificationType notificationType) {

        LOGGER.info("sendNotification called with organisation: {}, correlationUid: {}, type: {}",
                organisationIdentification, correlationUid, notificationType);

        final Notification notification = new Notification();
        // message is null, unless an error occurred
        notification.setMessage(message);
        notification.setResult(result);
        notification.setDeviceIdentification(deviceIdentification);
        notification.setCorrelationUid(correlationUid);
        notification.setNotificationType(notificationType);

        try {
            this.sendNotificationServiceClient.sendNotification(organisationIdentification, notification,
                    this.notificationUrl);
        } catch (final WebServiceSecurityException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
