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
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.BaseMessageProcessor;
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
public class GetHealthStatusRequestMessageProcessor extends BaseMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetHealthStatusRequestMessageProcessor.class);

    public GetHealthStatusRequestMessageProcessor() {
        super(MessageType.GET_HEALTH_STATUS);
    }

    @Override
    public void process(final ClientConnection deviceConnection, final RequestInfo requestInfo)
            throws ProtocolAdapterException {

        final String deviceIdentification = requestInfo.getDeviceIdentification();
        final String organisationIdentification = requestInfo.getOrganisationIdentification();

        LOGGER.info("getHealthStatus for IEC60870 device {} for organisation {}", deviceIdentification,
                organisationIdentification);

        try {
            final int ieQualifierOfInterrogationValue = 20;
            final int commonAddress = deviceConnection.getConnectionInfo().getCommonAddress();
            deviceConnection.getConnection().interrogation(commonAddress, CauseOfTransmission.ACTIVATION,
                    new IeQualifierOfInterrogation(ieQualifierOfInterrogationValue));

            final String interrogationMessage = "Interrogation [CommonAddress: " + commonAddress
                    + ", CauseOfTransmission: " + CauseOfTransmission.ACTIVATION + ", IeQualifierOfInterrogation: "
                    + ieQualifierOfInterrogationValue + "]";

            final LogItem logItem = new LogItem(deviceIdentification, organisationIdentification, false, true,
                    interrogationMessage, 0);
            this.getLoggingService().log(logItem);

        } catch (final IOException | RuntimeException e) {
            LOGGER.warn("Requesting the health status for device {} failed", deviceIdentification, e);
            throw new ProtocolAdapterException(ComponentType.PROTOCOL_IEC60870, e.getMessage());
        }
    }

}
