/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.in.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.NotificationService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.OsgpCoreRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushNotificationAlarmDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

/**
 * Class for processing smart metering push notification alarm request messages.
 */
@Component("domainSmartMeteringPushNotificationAlarmRequestMessageProcessor")
public class PushNotificationAlarmRequestMessageProcessor extends OsgpCoreRequestMessageProcessor {

    @Autowired
    private NotificationService notificationService;

    protected PushNotificationAlarmRequestMessageProcessor() {
        super(DeviceFunction.PUSH_NOTIFICATION_ALARM);
    }

    @Override
    protected void handleMessage(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Object dataObject, final String messageType) throws FunctionalException {

        this.notificationService.handlePushNotificationAlarm(deviceIdentification, organisationIdentification,
                correlationUid, messageType, (PushNotificationAlarmDto) ((RequestMessage) dataObject).getRequest());
    }
}
