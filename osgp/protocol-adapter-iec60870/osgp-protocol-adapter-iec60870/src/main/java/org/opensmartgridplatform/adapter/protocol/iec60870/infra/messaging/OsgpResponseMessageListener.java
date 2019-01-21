/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgpResponseMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpResponseMessageListener.class);

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received message of type: {}", message.getJMSType());

            /*
             TODO: provide implementation, when we implement an incoming message for this protocol
              @formatter:off
            final ObjectMessage objectMessage = (ObjectMessage) message;
            final String messageType = objectMessage.getJMSType();
            final String deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            final ResponseMessage responseMessage = (ResponseMessage) objectMessage.getObject();
            final String result = responseMessage == null ? null : responseMessage.getResult().toString();
            final OsgpException osgpException = responseMessage == null ? null : responseMessage.getOsgpException();

            @formatter:on
             */

            LOGGER.info("Handle the incoming message");

        } catch (final JMSException ex) {
            LOGGER.error("Exception: {} ", ex.getMessage(), ex);
        }
    }

}