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

import com.alliander.osgp.dto.valueobjects.smartmetering.SMSDetails;

/**
 * Class for processing Get SMS Details Request messages
 */
@Component("dlmsGetSMSDetailsRequestMessageProcessor")
public class GetSMSDetailsRequestMessageProcessor extends DeviceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetSMSDetailsRequestMessageProcessor.class);

    @Autowired
    private AdhocService adhocService;

    public GetSMSDetailsRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_SMS_DETAILS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing get sms details request message");

        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();

        try {
            messageMetadata.handleMessage(message);

            final SMSDetails smsDetails = (SMSDetails) message.getObject();

            this.adhocService.getSMSDetails(messageMetadata, smsDetails, this.responseMessageSender);

        } catch (final JMSException e) {
            this.logJmsException(LOGGER, e, messageMetadata);
        }
    }

}
