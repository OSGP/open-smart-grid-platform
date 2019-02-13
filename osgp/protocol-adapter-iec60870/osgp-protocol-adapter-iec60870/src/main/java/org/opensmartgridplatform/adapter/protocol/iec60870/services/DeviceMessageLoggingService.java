/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceMessageLoggingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMessageLoggingService.class);

    private static LogItemRequestMessageSender logItemRequestMessageSender;

    @Autowired
    public DeviceMessageLoggingService(final LogItemRequestMessageSender logItemRequestMessageSender) {
        DeviceMessageLoggingService.logItemRequestMessageSender = logItemRequestMessageSender;
    }

    public void logMessage(final MessageMetadata messageMetadata, final boolean incoming, final boolean valid,
            final String message, final int size) {

        final String deviceIdentification = messageMetadata.getDeviceIdentification();
        final String organisationIdentification = messageMetadata.getOrganisationIdentification();
        final String command = messageMetadata.getClass().getSimpleName();

        final LogItemRequestMessage logItemRequestMessage = new LogItemRequestMessage(deviceIdentification,
                organisationIdentification, incoming, valid, command + " - " + message, size);

        LOGGER.info("Sending LogItemRequestMessage for device: {}", deviceIdentification);
        logItemRequestMessageSender.send(logItemRequestMessage);
    }

    public void logMessage(final DeviceMessageLog deviceMessageLog, final String deviceIdentification,
            final String organisationIdentification, final boolean incoming) {

        final LogItemRequestMessage logItemRequestMessage = new LogItemRequestMessage(deviceIdentification,
                organisationIdentification, incoming, true, deviceMessageLog.getMessage(), 0);

        LOGGER.info("Sending LogItemRequestMessage for device: {}", deviceIdentification);
        logItemRequestMessageSender.send(logItemRequestMessage);
    }
}
