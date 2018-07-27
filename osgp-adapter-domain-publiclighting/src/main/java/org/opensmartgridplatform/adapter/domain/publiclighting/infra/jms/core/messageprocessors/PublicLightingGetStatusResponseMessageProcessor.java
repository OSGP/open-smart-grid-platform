/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.adapter.domain.publiclighting.application.services.AdHocManagementService;
import org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DomainType;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;

/**
 * Class for processing public lighting get status response messages
 */
@Component("domainPublicLightingGetStatusResponseMessageProcessor")
public class PublicLightingGetStatusResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicLightingGetStatusResponseMessageProcessor.class);

    @Autowired
    @Qualifier("domainPublicLightingAdHocManagementService")
    private AdHocManagementService adHocManagementService;

    protected PublicLightingGetStatusResponseMessageProcessor() {
        super(DeviceFunction.GET_LIGHT_STATUS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing public lighting get status response message");

        String correlationUid = null;
        String messageType = null;
        int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
        String organisationIdentification = null;
        String deviceIdentification = null;

        ResponseMessage responseMessage = null;
        ResponseMessageResultType responseMessageResultType = null;
        OsgpException osgpException = null;
        Object dataObject = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            messagePriority = message.getJMSPriority();
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
            LOGGER.debug("messagePriority: {}", messagePriority);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("responseMessageResultType: {}", responseMessageResultType);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("osgpException: {}", osgpException);
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            final DeviceStatusDto deviceLightStatus = (DeviceStatusDto) dataObject;

            this.adHocManagementService.handleGetStatusResponse(deviceLightStatus, DomainType.PUBLIC_LIGHTING,
                    deviceIdentification, organisationIdentification, correlationUid, messageType, messagePriority,
                    responseMessageResultType, osgpException);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType,
                    messagePriority);
        }
    }
}
