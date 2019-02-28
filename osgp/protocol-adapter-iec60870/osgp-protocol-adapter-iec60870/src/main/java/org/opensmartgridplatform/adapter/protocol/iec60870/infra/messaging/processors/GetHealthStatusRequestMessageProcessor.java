/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors;

import java.io.IOException;

import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.IeQualifierOfInterrogation;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.BaseMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class for processing get health status requests.
 */
@Component
public class GetHealthStatusRequestMessageProcessor extends BaseMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetHealthStatusRequestMessageProcessor.class);

    public GetHealthStatusRequestMessageProcessor() {
        super(MessageType.GET_HEALTH_STATUS, GetHealthStatusResponseEventListener.class);
    }

    @Override
    public void process(final MessageMetadata messageMetadata, final DeviceConnection deviceConnection)
            throws ProtocolAdapterException {

        LOGGER.info("getHealthStatus for IEC 60870-5-104 device {}", messageMetadata.getDeviceIdentification());

        try {
            final int ieQualifierOfInterrogationValue = 20;
            final int commonAddress = deviceConnection.getDeviceConnectionParameters().getCommonAddress();
            deviceConnection.getConnection().interrogation(commonAddress, CauseOfTransmission.ACTIVATION,
                    new IeQualifierOfInterrogation(ieQualifierOfInterrogationValue));

            final String interrogationMessage = "Interrogation [CommonAddress: " + commonAddress
                    + ", CauseOfTransmission: " + CauseOfTransmission.ACTIVATION + ", IeQualifierOfInterrogation: "
                    + ieQualifierOfInterrogationValue + "]";

            this.getDeviceMessageLoggingService().logMessage(messageMetadata, false, true, interrogationMessage, 0);

        } catch (final IOException | RuntimeException e) {
            LOGGER.warn(
                    "Requesting the health status for device " + messageMetadata.getDeviceIdentification() + " failed",
                    e);
            throw new ProtocolAdapterException(ComponentType.PROTOCOL_IEC60870, e.getMessage());
        }
    }

}
