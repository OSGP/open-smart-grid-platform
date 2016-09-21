/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.presentation.ws;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.microgrids.exceptions.WebServiceSecurityException;
import com.alliander.osgp.adapter.ws.schema.microgrids.notification.Notification;
import com.alliander.osgp.adapter.ws.schema.microgrids.notification.SendNotificationRequest;

public class SendNotificationServiceClient {

    private final WebServiceTemplateFactory webServiceTemplateFactory;

    @Autowired
    public SendNotificationServiceClient(final WebServiceTemplateFactory webServiceTemplateFactory) {
        this.webServiceTemplateFactory = webServiceTemplateFactory;
    }

    /**
     * Add a new device to the platform.
     *
     * @param model
     *            The device to add.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void sendNotification(final String organisationIdentification, final Notification notification,
            final String notificationURL) throws WebServiceSecurityException {

        final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        sendNotificationRequest.setNotification(notification);

        this.webServiceTemplateFactory.getTemplate(organisationIdentification, "LianderNetManagement", notificationURL)
                .marshalSendAndReceive(sendNotificationRequest);

    }
}
