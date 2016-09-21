/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.microgrids.exceptions.WebServiceSecurityException;
import com.alliander.osgp.adapter.ws.microgrids.presentation.ws.SendNotificationServiceClient;
import com.alliander.osgp.adapter.ws.schema.microgrids.notification.Notification;
import com.alliander.osgp.adapter.ws.schema.microgrids.notification.NotificationType;
import com.alliander.osgp.domain.core.validation.Identification;

@Service(value = "wsMicrogridsNotificationService")
@Transactional(value = "wsTransactionManager")
@Validated
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private SendNotificationServiceClient sendNotificationServiceClient;

    @Autowired
    private String notificationURL;

    public NotificationService() {
        // Parameterless constructor required for transactions
    }

    public void sendNotification(@Identification final String organisationIdentification,
            final String deviceIdentification, final String result, final String correlationUid, final String message,
            final NotificationType notificationType) {

        LOGGER.info("sendNotification called with organisation: {}, correlationUid: {}, type: {}",
                organisationIdentification, correlationUid, notificationType);

        final Notification notification = new Notification();
        // Message is null, unless an error occurred.
        notification.setMessage(message);
        notification.setResult(result);
        notification.setDeviceIdentification(deviceIdentification);
        notification.setCorrelationUid(correlationUid);
        notification.setNotificationType(notificationType);

        try {
            this.sendNotificationServiceClient.sendNotification(organisationIdentification, notification,
                    this.notificationURL);
        } catch (final WebServiceSecurityException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
