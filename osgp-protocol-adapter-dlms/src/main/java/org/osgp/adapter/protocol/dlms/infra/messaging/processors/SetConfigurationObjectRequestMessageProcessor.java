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
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequest;

/**
 * Class for processing Set Configuration Request messages
 */
@Component("dlmsSetConfigurationObjectRequestMessageProcessor")
public class SetConfigurationObjectRequestMessageProcessor extends DeviceRequestMessageProcessor {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationObjectRequestMessageProcessor.class);

    @Autowired
    private ConfigurationService configurationService;

    public SetConfigurationObjectRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_CONFIGURATION_OBJECT);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing special days request message");

        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();
        try {
            messageMetadata.handleMessage(message);

            final SetConfigurationObjectRequest setConfigurationObjectRequest = (SetConfigurationObjectRequest) message
                    .getObject();

            this.configurationService.requestSetConfiguration(messageMetadata.getOrganisationIdentification(),
                    messageMetadata.getDeviceIdentification(), messageMetadata.getCorrelationUid(), setConfigurationObjectRequest,
                    this.responseMessageSender, messageMetadata.getDomain(), messageMetadata.getDomainVersion(), messageMetadata.getMessageType());

        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, messageMetadata);
        }
    }

}
