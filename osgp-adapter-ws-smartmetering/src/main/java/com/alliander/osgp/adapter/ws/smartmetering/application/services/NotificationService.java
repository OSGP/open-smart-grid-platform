package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.domain.core.validation.Identification;

public interface NotificationService {

    public abstract void sendNotification(@Identification String organisationIdentification,
            String deviceIdentification, String result, String correlationUid, String message,
            NotificationType notificationType);

}
