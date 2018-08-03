/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.application.services;

import org.apache.commons.lang3.EnumUtils;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.shared.services.AbstractResendNotificationService;

@Service(value = "resendNotificationServiceDistributionAutomation")
@Transactional(value = "transactionManager")
public class ResendNotificationService extends AbstractResendNotificationService {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void resendNotification(final ResponseData responseData) {

        if (!EnumUtils.isValidEnum(NotificationType.class, responseData.getMessageType())) {
            this.logUnknownNotificationTypeError(responseData.getCorrelationUid(), responseData.getMessageType(),
                    this.notificationService.getClass().getName());
            return;
        }

        final NotificationType notificationType = NotificationType.valueOf(responseData.getMessageType());
        this.notificationService.sendNotification(responseData.getOrganisationIdentification(),
                responseData.getDeviceIdentification(), responseData.getResultType().name(),
                responseData.getCorrelationUid(), this.getNotificationMessage(responseData.getMessageType()),
                notificationType);
    }
}
