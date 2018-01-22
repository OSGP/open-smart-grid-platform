/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.schema.microgrids.notification.Notification;
import com.alliander.osgp.adapter.ws.schema.microgrids.notification.NotificationType;
import com.alliander.osgp.adapter.ws.schema.microgrids.notification.SendNotificationRequest;
import com.alliander.osgp.adapter.ws.shared.services.AbstractNotificationServiceWs;
import com.alliander.osgp.adapter.ws.shared.services.NotificationService;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Transactional(value = "transactionManager")
@Validated
public class NotificationServiceWs extends AbstractNotificationServiceWs implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceWs.class);

    private final DefaultWebServiceTemplateFactory webServiceTemplateFactory;

    public NotificationServiceWs(final DefaultWebServiceTemplateFactory webServiceTemplateFactory,
            final String notificationUrl, final String notificationUsername, final String notificationOrganisation) {
        super(notificationUrl, notificationUsername, notificationOrganisation);
        this.webServiceTemplateFactory = webServiceTemplateFactory;
    }

    @Override
    public void sendNotification(final String organisationIdentification, final String deviceIdentification,
            final String result, final String correlationUid, final String message, final Object notificationType) {

        LOGGER.info("sendNotification called with organisation: {}, correlationUid: {}, type: {}, to organisation: {}",
                this.notificationOrganisation, correlationUid, notificationType, organisationIdentification);

        final SendNotificationRequest notificationRequest = this.notificationRequest(deviceIdentification, result,
                correlationUid, message, notificationType);
        this.doSendNotification(this.webServiceTemplateFactory, organisationIdentification, this.notificationUsername,
                this.notificationUrl, notificationRequest);
    }

    private SendNotificationRequest notificationRequest(final String deviceIdentification, final String result,
            final String correlationUid, final String message, final Object notificationType) {

        final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        final Notification notification = new Notification();
        // message is null, unless an error occurred
        notification.setMessage(message);
        notification.setResult(result);
        notification.setDeviceIdentification(deviceIdentification);
        notification.setCorrelationUid(correlationUid);
        notification.setNotificationType((NotificationType) notificationType);
        sendNotificationRequest.setNotification(notification);
        return sendNotificationRequest;
    }
}
