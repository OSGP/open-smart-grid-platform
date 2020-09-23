/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.domain.core.application.services.FirmwareManagementService;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.DomainCoreDeviceRequestMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonConfirmRegisterDeviceRequestMessageProcessor extends DomainCoreDeviceRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommonConfirmRegisterDeviceRequestMessageProcessor.class);

    @Autowired
    private FirmwareManagementService firmwareManagementService;

    public CommonConfirmRegisterDeviceRequestMessageProcessor() {
        super(MessageType.CONFIRM_REGISTER_DEVICE);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {

        MessageMetadata messageMetadata;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        }

        this.firmwareManagementService.handleSsldPendingFirmwareUpdate(messageMetadata.getDeviceIdentification());
    }

}
