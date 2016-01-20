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

/**
 * Class for processing set Activity Calendar request messages
 */
@Component("dlmsSetEncryptionKeyExchangeOnGMeterRequestMessageProcessor")
public class SetEncryptionKeyExchangeOnGMeterRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SetEncryptionKeyExchangeOnGMeterRequestMessageProcessor.class);

    @Autowired
    private ConfigurationService configurationService;

    public SetEncryptionKeyExchangeOnGMeterRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing Set Encryption Key Exchange On G-Meter request message");

        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();
        try {
            messageMetadata.handleMessage(message);

            this.configurationService.setEncryptionKeyExchangeOnGMeter(messageMetadata, this.responseMessageSender);

        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, messageMetadata);
        }
    }
}
