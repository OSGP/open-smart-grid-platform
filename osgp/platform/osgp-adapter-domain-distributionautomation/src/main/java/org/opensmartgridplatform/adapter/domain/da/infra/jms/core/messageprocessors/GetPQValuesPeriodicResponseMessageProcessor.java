/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.domain.da.application.services.MonitoringService;
import org.opensmartgridplatform.dto.da.GetPQValuesResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.BaseNotificationMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class for processing da get pq values periodic response messages
 */
@Component("domainDistributionAutomationGetPQValuesPeriodicResponseMessageProcessor")
public class GetPQValuesPeriodicResponseMessageProcessor extends BaseNotificationMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GetPQValuesPeriodicResponseMessageProcessor.class);

    @Autowired
    @Qualifier("domainDistributionAutomationMonitoringService")
    private MonitoringService monitoringService;

    @Autowired
    protected GetPQValuesPeriodicResponseMessageProcessor(
            @Qualifier("domainDistributionAutomationOutboundResponseMessageRouter") final NotificationResponseMessageSender responseMessageSender,
            @Qualifier("domainDistributionAutomationInboundOsgpCoreResponsesMessageProcessorMap") final MessageProcessorMap messageProcessorMap) {
        super(responseMessageSender, messageProcessorMap, MessageType.GET_POWER_QUALITY_VALUES_PERIODIC);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing DA Power Quality Periodic response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        ResponseMessage responseMessage = null;
        ResponseMessageResultType responseMessageResultType = null;
        OsgpException osgpException = null;
        Object dataObject = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            responseMessage = (ResponseMessage) message.getObject();
            responseMessageResultType = responseMessage.getResult();
            osgpException = responseMessage.getOsgpException();
            dataObject = responseMessage.getDataObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("responseMessageResultType: {}", responseMessageResultType);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("osgpException: {}", osgpException);
            return;
        }
        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            final GetPQValuesResponseDto dataResponse = (GetPQValuesResponseDto) dataObject;

            final CorrelationIds ids = new CorrelationIds(organisationIdentification, deviceIdentification,
                    correlationUid);

            this.monitoringService.handleGetPQValuesResponse(dataResponse, ids, messageType, responseMessageResultType,
                    osgpException);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }
}
