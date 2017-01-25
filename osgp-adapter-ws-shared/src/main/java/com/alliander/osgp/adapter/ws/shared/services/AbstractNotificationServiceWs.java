/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.shared.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

public abstract class AbstractNotificationServiceWs {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNotificationServiceWs.class);

    protected final String notificationUsername;
    protected final String notificationUrl;

    protected AbstractNotificationServiceWs(final String notificationUrl, final String notificationUsername) {
        this.notificationUrl = notificationUrl;
        this.notificationUsername = notificationUsername;
    }

    protected void doSendNotification(final IWebserviceTemplateFactory wsTemplateFactory,
            final String organisationIdentification, final String userName, final String notificationURL,
            final Object notification) {

        try {
            WebServiceTemplate wsTemplate = wsTemplateFactory.getTemplate(organisationIdentification, userName,
                    notificationURL);
            wsTemplate.marshalSendAndReceive(notification);
        } catch (WebServiceTransportException | WebServiceSecurityException e) {
            final String msg = String.format(
                    "error sending notification message org=%s, user=%s, notifyUrl=%s, errmsg=%s",
                    organisationIdentification, userName, notificationURL, e.getMessage());
            LOGGER.error(msg, e);
        }

    }

}
