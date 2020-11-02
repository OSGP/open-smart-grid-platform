/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.messageprocessor;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public class DomainResponseMessageProcessor implements MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainResponseMessageProcessor.class);

    @Qualifier("wsPublicLightingInboundDomainResponsesMessageProcessorMap")
    @Autowired
    protected MessageProcessorMap domainResponseMessageProcessorMap;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ResponseDataService responseDataService;

    /**
     * The message type that a message processor implementation can handle.
     */
    protected MessageType messageType;

    /**
     * Construct a message processor instance by passing in the message type.
     *
     * @param messageType
     *            The message type a message processor can handle.
     */
    protected DomainResponseMessageProcessor(final MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors.
     */
    @PostConstruct
    public void init() {
        this.domainResponseMessageProcessorMap.addMessageProcessor(this.messageType, this);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing public lighting response message");

        String correlationUid = null;
        String jmsType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        NotificationType notificationType;
        ResponseMessageResultType resultType;
        String resultDescription;
        Serializable dataObject;

        try {
            correlationUid = message.getJMSCorrelationID();
            jmsType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            resultType = ResponseMessageResultType.valueOf(message.getStringProperty(Constants.RESULT));
            resultDescription = message.getStringProperty(Constants.DESCRIPTION);
            notificationType = NotificationType.valueOf(jmsType);
            dataObject = message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", jmsType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        try {
            LOGGER.info("Calling application service function to handle response: {} with correlationUid: {}", jmsType,
                    correlationUid);

            final CorrelationIds ids = new CorrelationIds(organisationIdentification, deviceIdentification,
                    correlationUid);
            this.handleMessage(ids, jmsType, resultType, resultDescription, dataObject);

            this.notificationService.sendNotification(organisationIdentification, deviceIdentification,
                    resultType.name(), correlationUid, resultDescription, notificationType);
        } catch (final RuntimeException e) {
            this.handleError(e, correlationUid, notificationType);
        }
    }

    protected void handleMessage(final CorrelationIds ids, final String messageType,
            final ResponseMessageResultType resultType, final String resultDescription, final Serializable dataObject) {

        final short numberOfNotificationsSent = 0;
        Serializable deviceResponseObject;

        if (ResponseMessageResultType.OK == resultType) {
            deviceResponseObject = dataObject;
        } else {
            deviceResponseObject = resultDescription;
        }

        final ResponseData responseData = new ResponseData(ids, messageType, resultType, deviceResponseObject,
                numberOfNotificationsSent);
        this.responseDataService.enqueue(responseData);
    }

    protected void handleError(final RuntimeException e, final String correlationUid,
            final NotificationType notificationType) {

        LOGGER.warn("Error '{}' occurred while trying to send notification type: {} with correlationUid: {}",
                e.getMessage(), notificationType, correlationUid, e);
    }
}
