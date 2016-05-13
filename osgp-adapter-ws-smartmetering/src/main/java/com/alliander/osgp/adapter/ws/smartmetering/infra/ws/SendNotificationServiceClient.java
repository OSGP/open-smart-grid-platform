/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.ws;

import java.io.IOException;
import java.security.GeneralSecurityException;

import ma.glasnost.orika.MapperFacade;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.Notification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.SendNotificationRequest;

public class SendNotificationServiceClient {

    private final WebServiceTemplateFactory webServiceTemplateFactory;
    private final MapperFacade mapper;

    @Autowired
    public SendNotificationServiceClient(final WebServiceTemplateFactory webServiceTemplateFactory,
            final MapperFacade mapper) {
        this.webServiceTemplateFactory = webServiceTemplateFactory;
        this.mapper = mapper;
    }

    /**
     * Add a new device to the platform.
     *
     * @param model
     *            The device to add.
     * @throws Exception
     */
    public void sendNotification(final String organisationIdentification, final Notification notification,
            final String notificationURL) throws GeneralSecurityException, IOException {

        final SendNotificationRequest sendNotificationRequest = new SendNotificationRequest();

        sendNotificationRequest.setNotification(notification);

        // TODO send username
        this.webServiceTemplateFactory.getTemplate(organisationIdentification, "LianderNetManagement", notificationURL)
                .marshalSendAndReceive(sendNotificationRequest);

        // TODO return something
        // return new SaveDeviceResponse(OsgpResultType.OK, null);

    }
}
