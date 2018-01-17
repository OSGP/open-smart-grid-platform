/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.presentation.ws;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.microgrids.notification.Notification;
import com.alliander.osgp.adapter.ws.schema.microgrids.notification.SendNotificationRequest;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

public class SendNotificationServiceClient {

    private final DefaultWebServiceTemplateFactory webServiceTemplateFactory;

    @Autowired
    public SendNotificationServiceClient(final DefaultWebServiceTemplateFactory webServiceTemplateFactory) {
        this.webServiceTemplateFactory = webServiceTemplateFactory;
    }

    public void sendNotification(final String organisationIdentification, final Notification notification,
            final String notificationURL, final String notificationUsername) throws WebServiceSecurityException {

        final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        sendNotificationRequest.setNotification(notification);

        this.webServiceTemplateFactory.getTemplate(organisationIdentification, notificationUsername, notificationURL)
                .marshalSendAndReceive(sendNotificationRequest);

    }
}
