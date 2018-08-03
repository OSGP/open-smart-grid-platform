/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.application.services;

import org.opensmartgridplatform.domain.core.validation.Identification;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.NotificationType;

public interface NotificationService {

    void sendNotification(@Identification String organisationIdentification, String deviceIdentification, String result, String correlationUid,
                          String message, NotificationType notificationType);

}
