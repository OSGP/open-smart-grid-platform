/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.AdHocManagementService;
import org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.domain.core.valueobjects.DomainType;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class for processing public lighting get status request messages
 */
@Component("domainPublicLightingGetStatusRequestMessageProcessor")
public class PublicLightingGetStatusRequestMessageProcessor extends BaseMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingGetStatusRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainPublicLightingAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    @Autowired
    public PublicLightingGetStatusRequestMessageProcessor(
            final WebServiceResponseMessageSender webServiceResponseMessageSender,
            @Qualifier("domainPublicLightingInboundWebServiceRequestsMessageProcessorMap") final MessageProcessorMap webServiceRequestMessageProcessorMap) {
        super(webServiceResponseMessageSender, webServiceRequestMessageProcessorMap, MessageType.GET_LIGHT_STATUS,
                ComponentType.DOMAIN_PUBLIC_LIGHTING);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.info("Processing public lighting get status request message");

        String correlationUid = null;
        String messageType = null;
        int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
        String organisationIdentification = null;
        String deviceIdentification = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            messagePriority = message.getJMSPriority();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("messagePriority: {}", messagePriority);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        try {
            LOGGER.info("Calling application service function: {}", messageType);

            this.adHocManagementService.getStatus(organisationIdentification, deviceIdentification, correlationUid,
                    DomainType.PUBLIC_LIGHTING, messageType, messagePriority);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType,
                    messagePriority);
        }
    }
}
