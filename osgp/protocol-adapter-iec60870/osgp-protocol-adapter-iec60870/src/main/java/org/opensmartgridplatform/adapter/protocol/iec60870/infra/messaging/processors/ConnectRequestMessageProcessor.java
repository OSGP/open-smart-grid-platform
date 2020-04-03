/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors;

import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.AbstractMessageProcessor;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class for processing get health status requests.
 */
@Component
public class ConnectRequestMessageProcessor extends AbstractMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectRequestMessageProcessor.class);

    public ConnectRequestMessageProcessor() {
        super(MessageType.CONNECT);
    }

    @Override
    public void process(final ClientConnection deviceConnection, final RequestMetadata requestMetadata)
            throws ProtocolAdapterException {

        final String deviceIdentification = requestMetadata.getDeviceIdentification();
        final String organisationIdentification = requestMetadata.getOrganisationIdentification();

        LOGGER.info("Connect request for IEC60870 device {} for organisation {}", deviceIdentification,
                organisationIdentification);
        LOGGER.info("Starting general interrogation for IEC60870 device {}", deviceIdentification);

        try {
            // Perform general interrogation
            final int ieQualifierOfInterrogationValue = 20;
            final int originatorAddress = 0;
            final int commonAddress = deviceConnection.getConnectionParameters().getCommonAddress();

            deviceConnection.getConnection()
                    .interrogation(commonAddress, CauseOfTransmission.ACTIVATION,
                            new IeQualifierOfInterrogation(ieQualifierOfInterrogationValue));

            // interrogation command creates this asdu internally, however we
            // need it here as well for logging...
            final ASdu asdu = new ASdu(ASduType.C_IC_NA_1, false, CauseOfTransmission.ACTIVATION, false, false,
                    originatorAddress, commonAddress,
                    new InformationObject(0, new IeQualifierOfInterrogation(ieQualifierOfInterrogationValue)));

            final LogItem logItem = new LogItem(deviceIdentification, organisationIdentification, false,
                    asdu.toString());

            this.getLoggingService().log(logItem);

        } catch (final IOException | RuntimeException e) {
            LOGGER.warn("Requesting the health status for device {} failed", deviceIdentification, e);
            throw new ProtocolAdapterException(ComponentType.PROTOCOL_IEC60870, e.getMessage());
        }
    }
}
