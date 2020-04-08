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
 * Class for processing get light sensor status requests.
 */
@Component
public class GetLightSensorStatusRequestMessageProcessor extends AbstractMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetLightSensorStatusRequestMessageProcessor.class);

    public GetLightSensorStatusRequestMessageProcessor() {
        super(MessageType.GET_LIGHT_SENSOR_STATUS);
    }

    @Override
    public void process(final ClientConnection deviceConnection, final RequestMetadata requestMetadata)
            throws ProtocolAdapterException {

        final String deviceIdentification = requestMetadata.getDeviceIdentification();
        final String organisationIdentification = requestMetadata.getOrganisationIdentification();

        LOGGER.info("Get light sensor status request for IEC60870 device {} for organisation {}", deviceIdentification,
                organisationIdentification);

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
            final String message = String.format("Requesting the light sensor status for device %s failed",
                    deviceIdentification);
            throw new ProtocolAdapterException(ComponentType.PROTOCOL_IEC60870, message, e);
        }
    }
}
