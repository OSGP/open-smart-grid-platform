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

import com.alliander.osgp.adapter.ws.smartmetering.infra.ws.SendNotificationServiceClient;
import com.alliander.osgp.adapter.ws.smartmetering.redis.AddMeterPublisher;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * @author OSGP
 *
 */
public class SmartMeteringResponseMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmartMeteringResponseMessageListener.class);

    @Autowired
    private AddMeterPublisher addMeterPublisher;

    @Autowired
    private SendNotificationServiceClient sendNotificationServiceClient;

    public SmartMeteringResponseMessageListener() {
        // empty constructor
    }

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received message of type: {}", message.getJMSType());

            final ObjectMessage objectMessage = (ObjectMessage) message;

            LOGGER.info("objectMessage CorrelationUID: {}", objectMessage.getJMSCorrelationID());

            final String feedback = "METER: " + objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION)
                    + " - RESULT: " + objectMessage.getStringProperty(Constants.RESULT);

            // This Listener only gets addMeter calls for now, a check will be
            // needed in the future
            // Redis call
            this.addMeterPublisher.publish(feedback);

            // WS call
            try {
                this.sendNotificationServiceClient.sendNotification("LianderNetManagement",
                        "Message from the ws adapter");
            } catch (final Exception e) {
                LOGGER.error("Add device exception", e);

                // TODO put Serive in here?
                // throw new OsgpException(ComponentType.WS_SMART_METERING,
                // e.getMessage(), e.getCause());
            }

        } catch (final JMSException ex) {
            LOGGER.error("Exception: {} ", ex.getMessage(), ex);
        }
    }
}