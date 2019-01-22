/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.tariffswitching.application.services;

import org.opensmartgridplatform.adapter.ws.clients.SendNotificationServiceClient;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class NotificationServiceTariffSwitching implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceTariffSwitching.class);

    @Autowired
    private SendNotificationServiceClient sendNotificationServiceClient;

    @Override
    public void sendNotification(final String organisationIdentification, final String deviceIdentification,
            final String result, final String correlationUid, final String message, final Object notificationType) {
        final Notification notification = new Notification();
        // Required fields.
        notification.setNotificationType((NotificationType) notificationType);
        notification.setDeviceIdentification(deviceIdentification);

        // Optional fields.
        if (!StringUtils.isEmpty(correlationUid)) {
            notification.setCorrelationUid(correlationUid);
        }
        if (!StringUtils.isEmpty(result)) {
            notification.setResult(OsgpResultType.valueOf(result));
        }
        if (!StringUtils.isEmpty(message)) {
            notification.setMessage(message);
        }

        // Try to send notification and catch security exceptions.
        try {
            this.sendNotificationServiceClient.sendNotification(organisationIdentification, notification);
        } catch (final WebServiceSecurityException e) {
            LOGGER.error("Unable to send notification", e);
        }
    }

}
