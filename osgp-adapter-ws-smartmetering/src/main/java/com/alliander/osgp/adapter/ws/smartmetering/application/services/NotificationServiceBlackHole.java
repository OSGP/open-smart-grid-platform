/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;

/**
 * Notification service to be used when no notifications should be sent.
 */
public class NotificationServiceBlackHole implements NotificationService {

    @Override
    public void sendNotification(final String organisationIdentification, final String deviceIdentification,
            final String result, final String correlationUid, final String message,
            final NotificationType notificationType) {
        // This notification service does not send notifications, so this method
        // is empty.
    }
}
