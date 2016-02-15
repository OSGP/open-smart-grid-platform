/**
 * Copyright 2014-2016 Smart Society Services B.V.
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

import com.alliander.osgp.dto.valueobjects.smartmetering.RetrieveConfigurationObjectsRequest;

@Component("dlmsRetrieveConfigurationObjectsRequestMessageProcessor")
public class RetrieveConfigurationObjectsRequestMessageProcessor extends DeviceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RetrieveConfigurationObjectsRequestMessageProcessor.class);

    @Autowired
    private AdhocService adhocService;

    protected RetrieveConfigurationObjectsRequestMessageProcessor() {
        super(DeviceRequestMessageType.GET_CONFIGURATION_OBJECTS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing retrieve configuration objects request message");
        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();

        try {
            messageMetadata.handleMessage(message);

            final RetrieveConfigurationObjectsRequest synchronizeTimeRequest = (RetrieveConfigurationObjectsRequest) message
                    .getObject();

            this.adhocService.retrieveConfigurationObjects(messageMetadata, synchronizeTimeRequest,
                    this.responseMessageSender);

        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, messageMetadata);
        }

    }

}
