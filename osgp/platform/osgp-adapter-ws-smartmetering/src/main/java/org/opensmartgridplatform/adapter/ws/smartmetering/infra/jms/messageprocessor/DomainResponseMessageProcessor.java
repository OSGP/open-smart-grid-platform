/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
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
import org.opensmartgridplatform.adapter.ws.smartmetering.application.ApplicationConstants;
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
public abstract class DomainResponseMessageProcessor implements MessageProcessor {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainResponseMessageProcessor.class);

    /**
     * The map of message processor instances.
     */
    @Qualifier("wsSmartMeteringInboundDomainResponsesMessageProcessorMap")
    @Autowired
    protected MessageProcessorMap messageProcessorMap;

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
        this.messageProcessorMap.addMessageProcessor(this.messageType, this);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing smart metering response message");

        String correlationUid = null;
        String actualMessageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        final String notificationMessage;
        final NotificationType notificationType;
        final ResponseMessageResultType resultType;
        final String resultDescription;
        final Serializable dataObject;

        try {
            correlationUid = message.getJMSCorrelationID();
            actualMessageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            resultType = ResponseMessageResultType.valueOf(message.getStringProperty(Constants.RESULT));
            resultDescription = message.getStringProperty(Constants.DESCRIPTION);

            notificationMessage = message.getStringProperty(Constants.DESCRIPTION);
            notificationType = NotificationType.valueOf(actualMessageType);

            dataObject = message.getObject();
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", actualMessageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        LOGGER.info("Calling application service function to handle response: {} with correlationUid: {}",
                actualMessageType, correlationUid);

        final CorrelationIds ids = new CorrelationIds(organisationIdentification, deviceIdentification, correlationUid);
        this.handleMessage(ids, actualMessageType, resultType, resultDescription, dataObject);

        try {
            // Send notification indicating data is available.
            this.notificationService.sendNotification(
                    new NotificationWebServiceLookupKey(organisationIdentification,
                            ApplicationConstants.APPLICATION_NAME),
                    new GenericNotification(notificationMessage, resultType.name(), deviceIdentification,
                            correlationUid, String.valueOf(notificationType)));

        } catch (final Exception e) {
            // Logging is enough, sending the notification will be done
            // automatically by the resend notification job
            LOGGER.warn(
                    "Delivering notification with correlationUid: {} and notification type: {} did not complete successfully.",
                    correlationUid, notificationType, e);
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

}
