/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.infra.jms.core.messageprocessors;

import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.domain.da.application.services.AdHocManagementService;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.BaseNotificationMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
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

@Component("domainDistributionAutomationGetDataResponseMessageProcessor")
public class GetDataResponseMessageProcessor extends BaseNotificationMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetDataResponseMessageProcessor.class);

    @Autowired
    @Qualifier("domainDistributionAutomationAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    @Autowired
    protected GetDataResponseMessageProcessor(final NotificationResponseMessageSender responseMessageSender,
            @Qualifier("domainDistributionAutomationInboundOsgpCoreResponsesMessageProcessorMap") final MessageProcessorMap messageProcessorMap) {
        super(responseMessageSender, messageProcessorMap, MessageType.GET_DATA);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing DA GET_DATA response message");
        this.getResponseValues(message).ifPresent(this::processResponseValues);
    }

    private Optional<ResponseValues> getResponseValues(final ObjectMessage message) {
        final ResponseValues responseValues = new ResponseValues();
        try {
            responseValues.correlationUid = message.getJMSCorrelationID();
            responseValues.messageType = message.getJMSType();
            responseValues.organisationIdentification = message.getStringProperty(
                    Constants.ORGANISATION_IDENTIFICATION);
            responseValues.deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            responseValues.responseMessage = (ResponseMessage) message.getObject();
            responseValues.responseMessageResultType = responseValues.responseMessage.getResult();
            responseValues.osgpException = responseValues.responseMessage.getOsgpException();
            responseValues.payload = (String) responseValues.responseMessage.getDataObject();
        } catch (final JMSException e) {
            this.logJmsException(responseValues, e);
            return Optional.empty();
        }
        return Optional.of(responseValues);
    }

    private void processResponseValues(final ResponseValues responseValues) {
        try {
            LOGGER.info("Calling application service function to handle response: {}", responseValues.messageType);
            this.adHocManagementService.handleGetDataRequest(responseValues.payload,
                    responseValues.deviceIdentification);
        } catch (final Exception e) {
            this.handleError(e, responseValues.correlationUid, responseValues.organisationIdentification,
                    responseValues.deviceIdentification, responseValues.messageType);
        }
    }

    private void logJmsException(final ResponseValues responseValues, final JMSException e) {
        LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
        LOGGER.debug("correlationUid: {}", responseValues.correlationUid);
        LOGGER.debug("messageType: {}", responseValues.messageType);
        LOGGER.debug("organisationIdentification: {}", responseValues.organisationIdentification);
        LOGGER.debug("deviceIdentification: {}", responseValues.deviceIdentification);
        LOGGER.debug("responseMessageResultType: {}", responseValues.responseMessageResultType);
        LOGGER.debug("deviceIdentification: {}", responseValues.deviceIdentification);
        LOGGER.debug("osgpException: {}", (Object) responseValues.osgpException);
    }

    private static class ResponseValues {
        String correlationUid;
        String messageType;
        String organisationIdentification;
        String deviceIdentification;
        ResponseMessage responseMessage;
        ResponseMessageResultType responseMessageResultType;
        OsgpException osgpException;
        String payload;
    }

}
