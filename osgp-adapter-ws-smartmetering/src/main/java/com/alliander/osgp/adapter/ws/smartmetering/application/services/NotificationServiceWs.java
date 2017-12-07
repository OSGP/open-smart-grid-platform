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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.Notification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.SendNotificationRequest;
import com.alliander.osgp.adapter.ws.shared.services.AbstractNotificationServiceWs;
import com.alliander.osgp.adapter.ws.shared.services.NotificationService;
import com.alliander.osgp.adapter.ws.shared.services.ResponseUrlService;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Transactional(value = "transactionManager")
@Validated
public class NotificationServiceWs extends AbstractNotificationServiceWs implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceWs.class);

    @Autowired
    private ResponseUrlService responseUrlService;

    private final DefaultWebServiceTemplateFactory webServiceTemplateFactory;

    @Autowired
    public NotificationServiceWs(final DefaultWebServiceTemplateFactory webServiceTemplateFactory,
            final String notificationUrl, final String notificationUsername, final String notificationOrganisation) {
        super(notificationUrl, notificationUsername, notificationOrganisation);
        this.webServiceTemplateFactory = webServiceTemplateFactory;
    }

    @Override
    public void sendNotification(final String organisationIdentification, final String deviceIdentification,
            final String result, final String correlationUid, final String message, final Object notificationType) {

        final String notifyUrl = this.notificationUrl(correlationUid);
        final SendNotificationRequest notificationRequest = this.notificationRequest(organisationIdentification,
                deviceIdentification, result, correlationUid, message, notificationType);
        this.doSendNotification(this.webServiceTemplateFactory, organisationIdentification, this.notificationUsername,
                notifyUrl, notificationRequest);
    }

    private String notificationUrl(final String correlationUid) {
        final String responseUrl = this.responseUrlService.popResponseUrl(correlationUid);
        return (responseUrl == null) ? this.notificationUrl : responseUrl;
    }

    private SendNotificationRequest notificationRequest(final String organisationIdentification,
            final String deviceIdentification, final String result, final String correlationUid, final String message,
            final Object notificationType) {

        LOGGER.debug("creating SendNotificationRequest with {},{},{},{},{},{} ", organisationIdentification,
                deviceIdentification, correlationUid, notificationType, message, result);

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
