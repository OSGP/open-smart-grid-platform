/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.IeQualifierOfInterrogation;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.BaseMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.services.Iec60870DeviceService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.ProtocolAdapterException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for processing get health status request messages
 */
@Component
public class GetHealthStatusRequestMessageProcessor extends BaseMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetHealthStatusRequestMessageProcessor.class);

    @Autowired
    private ResponseMessageSender responseMessageSender;

    @Autowired
    private Iec60870DeviceService iec60870DeviceService;

    public GetHealthStatusRequestMessageProcessor() {
        super(MessageType.GET_HEALTH_STATUS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.info("Processing get health status request message in new code...");

        MessageMetadata messageMetadata = null;
        try {
            messageMetadata = MessageMetadata.fromMessage(message);

            final GetHealthStatusResponseMessageProcessor responseProcessor = new GetHealthStatusResponseMessageProcessor(
                    messageMetadata, this.responseMessageSender);

            final DeviceConnection deviceConnection = this.iec60870DeviceService.connectToDevice(messageMetadata,
                    responseProcessor);

            this.getHealthStatus(messageMetadata, deviceConnection);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            return;
        } catch (final ProtocolAdapterException e) {
            this.handleExpectedErrorRuud(messageMetadata, e);
        }

    }

    private void getHealthStatus(final MessageMetadata messageMetadata, final DeviceConnection deviceConnection)
            throws ProtocolAdapterException {

        LOGGER.info("getHealthStatus for IEC 60870-5-104 device {}", messageMetadata.getDeviceIdentification());

        try {
            final int commonAddress = deviceConnection.getDeviceConnectionParameters().getCommonAddress();
            deviceConnection.getConnection().interrogation(commonAddress, CauseOfTransmission.ACTIVATION,
                    new IeQualifierOfInterrogation(20));
        } catch (final Exception e) {
            throw new ProtocolAdapterException(ComponentType.PROTOCOL_IEC60870, e.getMessage());
        }
    }

}
