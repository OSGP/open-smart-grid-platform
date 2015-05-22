/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.oslp.exceptions.ProtocolAdapterException;
import com.alliander.osgp.dto.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;
import com.alliander.osgp.shared.infra.jms.UnknownMessageTypeException;

public class OsgpResponseMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpResponseMessageListener.class);

    @Override
    public void onMessage(final Message message) {
        try {
            LOGGER.info("Received message of type: {}", message.getJMSType());

            final ObjectMessage objectMessage = (ObjectMessage) message;
            final String messageType = objectMessage.getJMSType();
            final String deviceIdentifcation = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            final ResponseMessage responseMessage = (ResponseMessage) objectMessage.getObject();
            final String result = responseMessage == null ? null : responseMessage.getResult().toString();
            final OsgpException osgpException = responseMessage == null ? null : responseMessage.getOsgpException();

            switch (DeviceFunction.valueOf(messageType)) {
            case REGISTER_DEVICE:
                if (ResponseMessageResultType.valueOf(result).equals(ResponseMessageResultType.NOT_OK)) {
                    throw new ProtocolAdapterException(String.format(
                            "Response for device: %s for MessageType: %s is: %s, error: %s", deviceIdentifcation,
                            messageType, result, osgpException));
                }
                break;

            default:
                throw new UnknownMessageTypeException("Unknown JMSType: " + messageType);
            }

        } catch (final JMSException ex) {
            LOGGER.error("Exception: {} ", ex.getMessage(), ex);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("ProtocolAdapterException", e);
        } catch (final UnknownMessageTypeException e) {
            LOGGER.error("UnknownMessageTypeException", e);
        }
    }
}
