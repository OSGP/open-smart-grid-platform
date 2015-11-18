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

import org.osgp.adapter.protocol.dlms.application.services.ManagementService;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryMessageDataContainer;

/**
 * Class for processing find events request messages
 */
@Component("dlmsFindEventsRequestMessageProcessor")
public class FindEventsRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AddMeterRequestMessageProcessor.class);

    @Autowired
    private ManagementService managementService;

    public FindEventsRequestMessageProcessor() {
        super(DeviceRequestMessageType.FIND_EVENTS);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing find events request message");

        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();
        Object data = null;

        try {
            messageMetadata.handleMessage(message);
            data = message.getObject();
        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, messageMetadata);
            return;
        }

        this.managementService.findEvents(messageMetadata.getOrganisationIdentification(), messageMetadata.getDeviceIdentification(),
                messageMetadata.getCorrelationUid(), this.responseMessageSender, messageMetadata.getDomain(), messageMetadata.getDomainVersion(),
                messageMetadata.getMessageType(), (FindEventsQueryMessageDataContainer) data);
    }
}
