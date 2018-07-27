/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.presentation.ws;

import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.SendNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SendNotificationServiceClient {

    private final DefaultWebServiceTemplateFactory webServiceTemplateFactory;

    @Autowired
    public SendNotificationServiceClient(final DefaultWebServiceTemplateFactory webServiceTemplateFactory) {
        this.webServiceTemplateFactory = webServiceTemplateFactory;
    }

    /**
     * Send notification
     *
     * @param organisationIdentification the organisation ID
     * @param notification the notification
     * @param notificationURL the notification URL
     * @param notificationUsername the notification user name
     *
     * @throws WebServiceSecurityException
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public void sendNotification(final String organisationIdentification, final Notification notification, final String notificationURL,
                                 final String notificationUsername) throws WebServiceSecurityException {

        final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        sendNotificationRequest.setNotification(notification);

        this.webServiceTemplateFactory.getTemplate(organisationIdentification, notificationUsername, notificationURL)
                .marshalSendAndReceive(sendNotificationRequest);
    }
}
