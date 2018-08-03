/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.logging.infra.jms;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.logging.domain.entities.WebServiceMonitorLogItem;
import org.opensmartgridplatform.logging.domain.repositories.WebServiceMonitorLogRepository;
import org.opensmartgridplatform.shared.infra.jms.Constants;

// Fetch incoming log messages from the logging requests queue.
@Component
public class LoggingMessageListener implements SessionAwareMessageListener<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingMessageListener.class);

    @Autowired
    private WebServiceMonitorLogRepository webServiceMonitorLogRepository;

    public LoggingMessageListener() {
        // empty constructor
    }

    @Override
    public void onMessage(final Message message, final Session session) throws JMSException {
        try {
            LOGGER.info("Received logging message");
            final ObjectMessage objectMessage = (ObjectMessage) message;

            // Create a log item.
            final Date timestamp = new Date(objectMessage.getLongProperty(Constants.TIME_STAMP));
            final WebServiceMonitorLogItem webServiceMonitorLogItem = new WebServiceMonitorLogItem(timestamp,
                    objectMessage.getStringProperty(Constants.ORGANISATION_IDENTIFICATION),
                    objectMessage.getStringProperty(Constants.USER_NAME),
                    objectMessage.getStringProperty(Constants.APPLICATION_NAME),
                    objectMessage.getStringProperty(Constants.CLASS_NAME),
                    objectMessage.getStringProperty(Constants.METHOD_NAME),
                    objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION),
                    objectMessage.getJMSCorrelationID(), objectMessage.getStringProperty(Constants.RESPONSE_RESULT),
                    objectMessage.getIntProperty(Constants.RESPONSE_DATA_SIZE));

            // Save the log item in the data base.
            this.webServiceMonitorLogRepository.save(webServiceMonitorLogItem);

        } catch (final JMSException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        }
    }
}
