/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.services;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.ws.domain.entities.ResponseData;
import com.alliander.osgp.adapter.ws.domain.repositories.ResponseDataRepository;
import com.alliander.osgp.adapter.ws.schema.microgrids.notification.NotificationType;
import com.alliander.osgp.adapter.ws.shared.services.AbstractResendNotificationService;

@Service(value = "ResendNotificationServiceMicrogrids")
@Transactional(value = "transactionManager")
public class ResendNotificationService extends AbstractResendNotificationService {

    @Autowired
    private NotificationServiceMicrogrids notificationServiceMicrogrids;

    @Autowired
    private ResponseDataRepository responseDataRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendNotificationService.class);

    @Override
    public void executer(final ResponseData responseData) {

        if (EnumUtils.isValidEnum(NotificationType.class, responseData.getMessageType())) {
            LOGGER.info("Found response data for resending notification");
            final NotificationType notificationType = NotificationType.valueOf(responseData.getMessageType());
            this.notificationServiceMicrogrids.sendNotification(responseData.getOrganisationIdentification(),
                    responseData.getDeviceIdentification(), responseData.getResultType().name(),
                    responseData.getCorrelationUid(), this.getNotificationMessage(responseData.getMessageType()),
                    notificationType);
            LOGGER.info("Notification has been resend");
            responseData.setNumberOfNotificationsSent((short) (responseData.getNumberOfNotificationsSent() + 1));
            this.responseDataRepository.save(responseData);
        }
    }
}
