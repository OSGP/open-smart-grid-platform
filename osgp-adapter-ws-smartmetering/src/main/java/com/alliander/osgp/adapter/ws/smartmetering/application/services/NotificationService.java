package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;

public interface NotificationService {

    public abstract void sendNotification(String organisationIdentification, String deviceIdentification,
            String result, String correlationUid, String message, NotificationType notificationType);

}
