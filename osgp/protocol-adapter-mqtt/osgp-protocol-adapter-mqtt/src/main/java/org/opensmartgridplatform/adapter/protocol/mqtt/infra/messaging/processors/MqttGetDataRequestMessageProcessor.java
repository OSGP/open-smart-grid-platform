/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.protocol.mqtt.application.services.SubcriptionService;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MqttGetDataRequestMessageProcessor implements MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttGetDataRequestMessageProcessor.class);

    private final SubcriptionService subcriptionService;

    public MqttGetDataRequestMessageProcessor(final SubcriptionService subcriptionService) {
        this.subcriptionService = subcriptionService;
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        try {
            final MessageMetadata messageMetadata = MessageMetadata.fromMessage(message);
            LOGGER.info("Calling DeviceService function: {} correlationUid: {} organisationIdentification: {} domain:"
                            + " {} {} Device: {} IP: {}", messageMetadata.getMessageType(),
                    messageMetadata.getCorrelationUid(),
                    messageMetadata.getOrganisationIdentification(), messageMetadata.getDomain(),
                    messageMetadata.getDomainVersion(), messageMetadata.getDeviceIdentification(),
                    messageMetadata.getIpAddress());
            this.subcriptionService.subscribe(messageMetadata);
        } catch (final Exception e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
        }
    }
}
