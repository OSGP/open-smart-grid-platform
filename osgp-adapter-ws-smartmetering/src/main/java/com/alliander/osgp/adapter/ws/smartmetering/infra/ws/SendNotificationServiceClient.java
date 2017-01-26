/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.ws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.Notification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.SendNotificationRequest;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

public class SendNotificationServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendNotificationServiceClient.class);

    private final DefaultWebServiceTemplateFactory webServiceTemplateFactory;

    @Autowired
    public SendNotificationServiceClient(final DefaultWebServiceTemplateFactory webServiceTemplateFactory) {
        this.webServiceTemplateFactory = webServiceTemplateFactory;
    }

    /**
     * Add a new device to the platform.
     *
     * @param model
     *            The device to add.
     * @throws WebServiceSecurityException
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void sendNotification(final String organisationIdentification, final Notification notification,
            final String notificationURL, final String notificationUsername) throws WebServiceSecurityException {

        final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        sendNotificationRequest.setNotification(notification);

        try {
            this.webServiceTemplateFactory
                    .getTemplate(organisationIdentification, notificationUsername, new URL(notificationURL))
                    .marshalSendAndReceive(sendNotificationRequest);
        } catch (final MalformedURLException e) {
            LOGGER.error("Unexpected exception by creating notification URL", e);
        }
    }
}
