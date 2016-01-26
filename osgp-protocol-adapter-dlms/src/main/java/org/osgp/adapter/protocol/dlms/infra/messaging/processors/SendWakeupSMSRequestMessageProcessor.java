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

import org.osgp.adapter.protocol.dlms.application.services.AdhocService;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for processing Send Wakeup SMS Request messages
 */
@Component("dlmsSendWakeupSMSRequestMessageProcessor")
public class SendWakeupSMSRequestMessageProcessor extends DeviceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendWakeupSMSRequestMessageProcessor.class);

    @Autowired
    private AdhocService adhocService;

    public SendWakeupSMSRequestMessageProcessor() {
        super(DeviceRequestMessageType.SEND_WAKEUP_SMS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing send wakeup sms request message");

        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();

        try {
            messageMetadata.handleMessage(message);

            this.adhocService.sendWakeUpSMS(messageMetadata, this.responseMessageSender);

        } catch (final JMSException e) {
            this.logJmsException(LOGGER, e, messageMetadata);
        }
    }

}
