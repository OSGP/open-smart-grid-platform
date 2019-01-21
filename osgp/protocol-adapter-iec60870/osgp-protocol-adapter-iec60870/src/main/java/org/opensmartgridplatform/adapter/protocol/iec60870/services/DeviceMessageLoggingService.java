/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.Iec60870LogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.Iec60870LogItemRequestMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceMessageLoggingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMessageLoggingService.class);

    private static Iec60870LogItemRequestMessageSender iec60870LogItemRequestMessageSender;

    @Autowired
    public DeviceMessageLoggingService(final Iec60870LogItemRequestMessageSender iec60870LogItemRequestMessageSender) {
        DeviceMessageLoggingService.iec60870LogItemRequestMessageSender = iec60870LogItemRequestMessageSender;
    }

    public static void logMessage(final DeviceRequest deviceRequest, final boolean incoming, final boolean valid,
            final String message, final int size) {

        final String deviceIdentification = deviceRequest.getDeviceIdentification();
        final String organisationIdentification = deviceRequest.getOrganisationIdentification();
        final String command = deviceRequest.getClass().getSimpleName();

        final Iec60870LogItemRequestMessage iec60870LogItemRequestMessage = new Iec60870LogItemRequestMessage(
                deviceIdentification, organisationIdentification, incoming, valid, command + " - " + message, size);

        LOGGER.info("Sending iec60870LogItemRequestMessage for device: {}", deviceIdentification);
        iec60870LogItemRequestMessageSender.send(iec60870LogItemRequestMessage);
    }

    public static void logMessage(final DeviceMessageLog deviceMessageLog, final String deviceIdentification,
            final String organisationIdentification, final boolean incoming) {

        final Iec60870LogItemRequestMessage iec60870LogItemRequestMessage = new Iec60870LogItemRequestMessage(
                deviceIdentification, organisationIdentification, incoming, true, deviceMessageLog.getMessage(), 0);

        LOGGER.info("Sending iec60870LogItemRequestMessage for device: {}", deviceIdentification);
        iec60870LogItemRequestMessageSender.send(iec60870LogItemRequestMessage);
    }
}
