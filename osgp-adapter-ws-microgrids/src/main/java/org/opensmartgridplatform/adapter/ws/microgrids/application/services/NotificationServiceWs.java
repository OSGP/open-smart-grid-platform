/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.microgrids.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import org.opensmartgridplatform.adapter.ws.schema.microgrids.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.notification.SendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericSendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.shared.services.AbstractNotificationServiceWs;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.shared.infra.ws.DefaultWebServiceTemplateFactory;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Transactional(value = "transactionManager")
@Validated
public class NotificationServiceWs extends AbstractNotificationServiceWs implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceWs.class);

    private final DefaultWebServiceTemplateFactory webServiceTemplateFactory;
    private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

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

        final GenericSendNotificationRequest genericNotificationRequest = this.genericNotificationRequest(deviceIdentification,
                result, correlationUid, message, ((NotificationType) notificationType).toString());
        final SendNotificationRequest notificationRequest = this.mapperFactory.getMapperFacade()
                .map(genericNotificationRequest, SendNotificationRequest.class);

        this.doSendNotification(this.webServiceTemplateFactory, organisationIdentification, this.notificationUsername,
                this.notificationUrl, notificationRequest);
    }
}
