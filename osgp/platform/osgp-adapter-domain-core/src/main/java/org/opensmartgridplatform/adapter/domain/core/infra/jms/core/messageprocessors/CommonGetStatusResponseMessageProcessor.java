/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.infra.jms.core.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.domain.core.application.services.DeviceInstallationService;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class for processing common get status response messages
 */
@Component("domainCoreCommonGetStatusResponseMessageProcessor")
public class CommonGetStatusResponseMessageProcessor extends BaseMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonGetStatusResponseMessageProcessor.class);

    @Autowired
    @Qualifier("domainCoreDeviceInstallationService")
    private DeviceInstallationService deviceInstallationService;

    @Autowired
    protected CommonGetStatusResponseMessageProcessor(
            @Qualifier("domainCoreOutboundWebServiceResponsesMessageSender") final WebServiceResponseMessageSender messageSender,
            @Qualifier("domainCoreInboundOsgpCoreResponsesMessageProcessorMap") final MessageProcessorMap messageProcessorMap) {
        super(messageSender, messageProcessorMap, MessageType.GET_STATUS, ComponentType.DOMAIN_CORE);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing common get status response message");

        String correlationUid = null;
        String messageType = null;
        int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
        String organisationIdentification = null;
        String deviceIdentification = null;

        ResponseMessage responseMessage;
        ResponseMessageResultType responseMessageResultType = null;
        OsgpException osgpException = null;
        Object dataObject;

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
            LOGGER.debug("osgpException", osgpException);
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {}", messageType);

            final DeviceStatusDto deviceStatusDto = (DeviceStatusDto) dataObject;

            final CorrelationIds ids = new CorrelationIds(organisationIdentification, deviceIdentification,
                    correlationUid);
            this.deviceInstallationService.handleGetStatusResponse(deviceStatusDto, ids, messageType, messagePriority,
                    responseMessageResultType, osgpException);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType,
                    messagePriority);
        }
    }
}
