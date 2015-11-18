/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.osgp.adapter.protocol.dlms.application.services.ConfigurationService;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessagingDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotifications;

/**
 * Class for processing set alarm notifications request messages
 */
@Component("dlmsSetAlarmNotificationsRequestMessageProcessor")
public class SetAlarmNotificationsRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SetAlarmNotificationsRequestMessageProcessor.class);

    @Autowired
    private ConfigurationService configurationService;

    public SetAlarmNotificationsRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_ALARM_NOTIFICATIONS);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing set alarm notifications request message");

        final DlmsMessagingDevice device = new DlmsMessagingDevice();

        try {
            device.handleMessage(message);

            final AlarmNotifications alarmNotifications = (AlarmNotifications) message.getObject();

            this.configurationService.setAlarmNotifications(device.getOrganisationIdentification(),
                    device.getDeviceIdentification(), device.getCorrelationUid(), alarmNotifications,
                    this.responseMessageSender, device.getDomain(), device.getDomainVersion(), device.getMessageType());

        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, device);
        }
    }
}
