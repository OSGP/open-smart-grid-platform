/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.adapter.ws.smartmetering.application.services.NotificationService;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.shared.infra.jms.MessageProcessorMap;

/**
 * @author OSGP
 *
 */
public class SmartMeteringResponseMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringResponseMessageListener.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    @Qualifier("wsAdapterSmartMeteringDomainMessageProcessorMap")
    private MessageProcessorMap domainResponseMessageProcessorMap;

    public SmartMeteringResponseMessageListener() {
        // empty constructor
    }

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received message of type: {}", message.getJMSType());

            final ObjectMessage objectMessage = (ObjectMessage) message;

            LOGGER.info("objectMessage CorrelationUID: {}", objectMessage.getJMSCorrelationID());

            final MessageProcessor processor = this.domainResponseMessageProcessorMap
                    .getMessageProcessor(objectMessage);

            processor.processMessage(objectMessage);

            // // TODO error handling
            // final NotificationType notificationType =
            // NotificationType.valueOf(message.getJMSType());
            //
            // // WS call
            // this.notificationService.sendNotification(
            // objectMessage.getStringProperty(Constants.ORGANISATION_IDENTIFICATION),
            // objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION),
            // objectMessage.getStringProperty(Constants.RESULT),
            // objectMessage.getJMSCorrelationID(),
            // objectMessage.getStringProperty(Constants.DESCRIPTION),
            // notificationType);
        } catch (final JMSException ex) {
            LOGGER.error("Exception: {} ", ex.getMessage(), ex);
        }
    }
}