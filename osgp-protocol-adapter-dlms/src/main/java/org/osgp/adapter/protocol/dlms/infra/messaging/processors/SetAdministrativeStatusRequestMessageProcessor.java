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

import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType;

/**
 * Class for processing the Set Administrative Status request message
 */
@Component("dlmsSetAdministrationStatusRequestMessageProcessor")
public class SetAdministrativeStatusRequestMessageProcessor extends DeviceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetAdministrativeStatusRequestMessageProcessor.class);

    @Autowired
    private ConfigurationService configurationService;

    public SetAdministrativeStatusRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_ADMINISTRATIVE_STATUS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing set administration request message");

        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();

        try {
            messageMetadata.handleMessage(message);

            final AdministrativeStatusType administrativeStatusType = (AdministrativeStatusType) message.getObject();

            this.configurationService.requestSetAdministrativeStatus(messageMetadata, administrativeStatusType,
                    this.responseMessageSender);

        } catch (final JMSException e) {
            this.logJmsException(LOGGER, e, messageMetadata);
        }
    }

}
