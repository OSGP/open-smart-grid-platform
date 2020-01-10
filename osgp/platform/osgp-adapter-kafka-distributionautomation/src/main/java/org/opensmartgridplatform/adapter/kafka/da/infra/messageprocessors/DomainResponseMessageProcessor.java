/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.messageprocessors;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.kafka.da.io.kafka.out.MeasurementReadingProducer;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementReport;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.NotificationType;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
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
    private MeasurementReadingProducer measurementReadingProducer;

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing distribution automation response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        ResponseMessageResultType resultType;
        String resultDescription;
        Serializable dataObject;

        try {
            correlationUid = message.getJMSCorrelationID();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            messageType = message.getJMSType();
            validateMessageType(messageType);

            resultType = ResponseMessageResultType.valueOf(message.getStringProperty(Constants.RESULT));
            resultDescription = message.getStringProperty(Constants.DESCRIPTION);

            dataObject = message.getObject();
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
            this.handleMessage(ids, messageType, resultType, resultDescription, dataObject);

        } catch (final RuntimeException e) {
            handleError(e, correlationUid);
        }
    }

    private void handleMessage(final CorrelationIds ids, final String messageType,
            final ResponseMessageResultType resultType, final String resultDescription, final Serializable dataObject) {

        if (!(dataObject instanceof MeasurementReport)) {
            LOGGER.error("For this component we only handle measurement reports");
        } else {
            this.measurementReadingProducer.send(this.mapper.map(dataObject,
                    org.opensmartgridplatform.domain.da.measurements.MeasurementReport.class));
        }

    }

    /**
     * In case of an error, this function can be used to send a response
     * containing the exception to the web-service-adapter.
     *
     * @param e
     *            The exception.
     * @param correlationUid
     *            The correlation UID.
     * @param notificationType
     *            The message type.
     */
    private static void handleError(final RuntimeException e, final String correlationUid) {

        LOGGER.warn("Error '{}' occurred while trying to send message with correlationUid: {}", e.getMessage(),
                correlationUid, e);
    }

    /**
     * Checks if a given messageType has a known {@link NotificationType}.
     *
     * @param messageType
     *            The messageType to check.
     * @throws IllegalArgumentException,
     *             when no NotificationType is found for the given messageType.
     */
    private static void validateMessageType(final String messageType) {
        final NotificationType notificationType = NotificationType.valueOf(messageType);
        LOGGER.debug("Received message has known notification type: \"{}\"", notificationType);
    }

    private static void logDebugInformation(final String messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification) {
        LOGGER.debug("messageType: {}", messageType);
        LOGGER.debug("CorrelationUid: {}", correlationUid);
        LOGGER.debug("organisationIdentification: {}", organisationIdentification);
        LOGGER.debug("deviceIdentification: {}", deviceIdentification);
    }

}
