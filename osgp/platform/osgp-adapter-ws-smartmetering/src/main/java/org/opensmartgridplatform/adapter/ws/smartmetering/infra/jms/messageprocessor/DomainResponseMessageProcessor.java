/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.messageprocessor;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceLookupKey;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.schema.shared.notification.GenericNotification;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
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
import org.springframework.beans.factory.annotation.Value;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 *
 */
public abstract class DomainResponseMessageProcessor implements MessageProcessor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainResponseMessageProcessor.class);

    /**
     * The map of message processor instances.
     */
    @Qualifier("domainSmartMeteringResponseMessageProcessorMap")
    @Autowired
    protected MessageProcessorMap domainResponseMessageProcessorMap;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ResponseDataService responseDataService;

    @Value("${web.service.notification.application.name}")
    private String applicationName;
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
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing smart metering response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        final String notificationMessage;
        final NotificationType notificationType;
        final ResponseMessageResultType resultType;
        final String resultDescription;
        final Serializable dataObject;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            resultType = ResponseMessageResultType.valueOf(message.getStringProperty(Constants.RESULT));
            resultDescription = message.getStringProperty(Constants.DESCRIPTION);

            notificationMessage = message.getStringProperty(Constants.DESCRIPTION);
            notificationType = NotificationType.valueOf(messageType);

            dataObject = message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        final NotificationWebServiceLookupKey webServiceLookupKey =
                new NotificationWebServiceLookupKey(organisationIdentification, this.applicationName);
        try {
            LOGGER.info("Calling application service function to handle response: {} with correlationUid: {}",
                    messageType, correlationUid);

            final CorrelationIds ids = new CorrelationIds(organisationIdentification, deviceIdentification,
                    correlationUid);
            this.handleMessage(ids, messageType, resultType, resultDescription, dataObject);

            // Send notification indicating data is available.
            this.notificationService.sendNotification(webServiceLookupKey, new GenericNotification(notificationMessage, resultType.name(),
                    deviceIdentification, correlationUid, String.valueOf(notificationType)));
        } catch (final Exception e) {
            this.handleError(e, webServiceLookupKey, correlationUid, deviceIdentification,
                    notificationType);
        }
    }

    protected void handleMessage(final CorrelationIds ids, final String messageType,
            final ResponseMessageResultType resultType, final String resultDescription, final Serializable dataObject) {

        final short numberOfNotificationsSent = 0;
        final Serializable meterResponseObject;
        if (dataObject == null) {
            meterResponseObject = resultDescription;
        } else {
            meterResponseObject = dataObject;
        }

        final ResponseData responseData = new ResponseData(ids, messageType, resultType, meterResponseObject,
                numberOfNotificationsSent);
        this.responseDataService.enqueue(responseData);
    }

    /**
     * In case of an error, this function can be used to send a response containing
     * the exception to the web-service-adapter.
     */
    protected void handleError(final Exception e, final NotificationWebServiceLookupKey webServiceLookupKey,
            final String correlationUid, final String deviceIdentification, final NotificationType notificationType) {

        LOGGER.info("handeling error: {} for notification type: {} with correlationUid: {}", e.getMessage(),
                notificationType, correlationUid, e);
        this.notificationService.sendNotification(webServiceLookupKey, new GenericNotification(e.getMessage(),
                "NOT_OK", deviceIdentification, correlationUid, String.valueOf(notificationType)));
    }
}
