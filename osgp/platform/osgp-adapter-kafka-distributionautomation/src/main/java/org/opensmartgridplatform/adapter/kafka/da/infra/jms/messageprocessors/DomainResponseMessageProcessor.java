/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.jms.messageprocessors;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out.MeterReadingProducer;
import org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out.PeakShavingProducer;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class to process incoming domain responses.
 */
@Component(value = "kafkaDistributionAutomationInboundDomainResponsesMessageProcessor")
public class DomainResponseMessageProcessor implements MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainResponseMessageProcessor.class);

    @Autowired
    protected DistributionAutomationMapper mapper;

    @Autowired
    private MeterReadingProducer meterReadingProducer;

    @Autowired
    private PeakShavingProducer peakShavingProducer;

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing distribution automation response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        ResponseMessageResultType resultType;
        String resultDescription;
        ResponseMessage dataObject;

        try {
            correlationUid = message.getJMSCorrelationID();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            messageType = message.getJMSType();

            resultType = ResponseMessageResultType.valueOf(message.getStringProperty(Constants.RESULT));
            resultDescription = message.getStringProperty(Constants.DESCRIPTION);

            dataObject = (ResponseMessage) message.getObject();
        } catch (final IllegalArgumentException e) {
            LOGGER.error("UNRECOVERABLE ERROR, received messageType {} is unknown.", messageType, e);
            logDebugInformation(messageType, correlationUid, organisationIdentification, deviceIdentification);

            return;
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            logDebugInformation(messageType, correlationUid, organisationIdentification, deviceIdentification);

            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            final CorrelationIds ids = new CorrelationIds(organisationIdentification, deviceIdentification,
                    correlationUid);

            LOGGER.info("Handle message of type {} for device {} with result: type {}, description {}.", messageType,
                    ids.getDeviceIdentification(), resultType, resultDescription);
            this.handleMessage(messageType, dataObject);
        } catch (final RuntimeException e) {
            handleError(e, correlationUid);
        }
    }

    private void handleMessage(final String messageType, final ResponseMessage message) {

        final Serializable dataObject = message.getDataObject();
        if (dataObject instanceof MeasurementReport) {
            this.meterReadingProducer.send(this.mapper.map(dataObject, MeasurementReport.class));
        } else if (dataObject instanceof String && "GET_DATA".equals(messageType)) {
            this.peakShavingProducer.send((String) dataObject);
        } else {
            LOGGER.warn(
                    "For this component we only handle measurement reports and MQTT GET_DATA responses. Received message type: {}, message {}",
                    messageType, dataObject);
        }

    }

    private static void handleError(final RuntimeException e, final String correlationUid) {
        LOGGER.error("Error '{}' occurred while trying to send message with correlationUid: {}", e.getMessage(),
                correlationUid, e);
    }

    private static void logDebugInformation(final String messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification) {
        LOGGER.debug("messageType: {}", messageType);
        LOGGER.debug("CorrelationUid: {}", correlationUid);
        LOGGER.debug("organisationIdentification: {}", organisationIdentification);
        LOGGER.debug("deviceIdentification: {}", deviceIdentification);
    }

}
