/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.domain.da.application.services.AdHocManagementService;
import org.opensmartgridplatform.shared.infra.jms.BaseNotificationMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("domainDistributionAutomationGetDataRequestMessageProcessor")
public class GetDataRequestMessageProcessor extends BaseNotificationMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetDataRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainDistributionAutomationAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    @Autowired
    protected GetDataRequestMessageProcessor(final NotificationResponseMessageSender responseMessageSender,
            @Qualifier("domainDistributionAutomationInboundOsgpCoreRequestsaMessageProcessorMap") final MessageProcessorMap messageProcessorMap) {
        super(responseMessageSender, messageProcessorMap, MessageType.GET_DATA);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing GET_DATA message");

        String messageType = null;
        String payload = null;
        CorrelationIds correlationIds = null;

        try {
            messageType = message.getJMSType();
            final RequestMessage requestMessage = (RequestMessage) message.getObject();
            payload = (String) requestMessage.getRequest();
            correlationIds = new CorrelationIds(message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION),
                    message.getStringProperty(Constants.DEVICE_IDENTIFICATION), message.getJMSCorrelationID());
        } catch (final JMSException e) {
            this.logJmsException(correlationIds, messageType, payload, e);
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle request: {}", messageType);
            this.adHocManagementService.handleGetDataRequest(payload, correlationIds.getDeviceIdentification());
        } catch (final Exception e) {
            this.handleError(e, correlationIds.getCorrelationUid(), correlationIds.getOrganisationIdentification(),
                    correlationIds.getDeviceIdentification(), messageType);
        }
    }

    private void logJmsException(final CorrelationIds ids, final String messageType, final String payload,
            final JMSException e) {
        LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
        LOGGER.debug("correlationids: {}", ids);
        LOGGER.debug("messageType: {}", messageType);
        LOGGER.debug("payload: {}", payload);
    }
}
