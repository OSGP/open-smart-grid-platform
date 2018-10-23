/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.DeviceManagementService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for processing common revoke key request messages
 */
@Component("oslpAdminRevokeKeyRequestMessageProcessor")
public class AdminRevokeKeyRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRevokeKeyRequestMessageProcessor.class);

    /**
     * Autowired field device management application service
     */
    @Autowired
    private DeviceManagementService deviceManagementService;

    public AdminRevokeKeyRequestMessageProcessor() {
        super(MessageType.REVOKE_KEY);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing admin revoke key message");

        MessageMetadata messageMetadata;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        this.printDomainInfo(messageMetadata.getMessageType(), messageMetadata.getDomain(),
                messageMetadata.getDomainVersion());

        this.deviceManagementService.revokeKey(messageMetadata, this.responseMessageSender);
    }
}
