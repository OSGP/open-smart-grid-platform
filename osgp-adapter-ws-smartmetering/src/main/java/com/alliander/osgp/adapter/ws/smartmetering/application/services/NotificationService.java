/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import com.alliander.osgp.adapter.ws.schema.smartmetering.notification.NotificationType;
import com.alliander.osgp.domain.core.validation.Identification;

public interface NotificationService {

    public abstract void sendNotification(@Identification String organisationIdentification,
            String deviceIdentification, String result, String correlationUid, String message,
            NotificationType notificationType);

}
