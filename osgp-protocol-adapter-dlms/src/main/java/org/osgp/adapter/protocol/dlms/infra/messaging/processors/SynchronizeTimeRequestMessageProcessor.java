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
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessagingDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequest;

/**
 * Class for processing Synchronize Time Request messages
 */
@Component("dlmsSynchronizeTimeRequestMessageProcessor")
public class SynchronizeTimeRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsRequestMessageProcessor.class);

    @Autowired
    private AdhocService adhocService;

    public SynchronizeTimeRequestMessageProcessor() {
        super(DeviceRequestMessageType.SYNCHRONIZE_TIME);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing synchronize time request message");

        final DlmsMessagingDevice device = new DlmsMessagingDevice();

        try {
            device.handleMessage(message);

            final SynchronizeTimeRequest synchronizeTimeRequest = (SynchronizeTimeRequest) message.getObject();

            this.adhocService.synchronizeTime(device.getOrganisationIdentification(), device.getDeviceIdentification(),
                    device.getCorrelationUid(), synchronizeTimeRequest, this.responseMessageSender, device.getDomain(),
                    device.getDomainVersion(), device.getMessageType());

        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, device);
        }
    }
}
