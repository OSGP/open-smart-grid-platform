/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 *
 */
public abstract class OsgpCoreResponseMessageProcessor implements MessageProcessor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpCoreResponseMessageProcessor.class);

    /**
     * This is the message sender needed for the message processor
     * implementation to forward response messages to web service adapter.
     */
    @Autowired
    protected WebServiceResponseMessageSender webServiceResponseMessageSender;

    /**
     * The hash map of message processor instances.
     */
    @Autowired
    @Qualifier("domainSmartMeteringOsgpCoreResponseMessageProcessorMap")
    protected OsgpCoreResponseMessageProcessorMap osgpCoreResponseMessageProcessorMap;

    /**
     * The message type that a message processor implementation can handle.
     */
    protected List<DeviceFunction> deviceFunctions;

    /**
     * Construct a message processor instance by passing in the message type.
     *
     * @param deviceFunction
     *            The message type a message processor can handle.
     */
    protected OsgpCoreResponseMessageProcessor(final DeviceFunction deviceFunction) {
        this.deviceFunctions = new ArrayList<>();
        this.deviceFunctions.add(deviceFunction);
    }

    /**
     * In case a message processor instance can process multiple message types,
     * a message type can be added.
     *
     * @param deviceFunction
     *            The message type a message processor can handle.
     */
    protected void addMessageType(final DeviceFunction deviceFunction) {
        this.deviceFunctions.add(deviceFunction);
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors. The key for the HashMap is the integer value of the
     * enumeration member.
     */
    @PostConstruct
    public void init() {
        for (final DeviceFunction deviceFunction : this.deviceFunctions) {
            this.osgpCoreResponseMessageProcessorMap.addMessageProcessor(deviceFunction.ordinal(),
                    deviceFunction.name(), this);
        }
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing smart metering response message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        ResponseMessage responseMessage = null;
        ResponseMessageResultType responseMessageResultType = null;
        OsgpException osgpException = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            responseMessage = (ResponseMessage) message.getObject();
            responseMessageResultType = responseMessage.getResult();
            osgpException = responseMessage.getOsgpException();
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

            if (osgpException != null) {
                this.handleError(osgpException, correlationUid, organisationIdentification, deviceIdentification,
                        messageType);
            } else if (this.hasRegularResponseObject(responseMessage)) {
                LOGGER.info("Calling application service function to handle response: {}", messageType);

                this.handleMessage(deviceIdentification, organisationIdentification, correlationUid, messageType,
                        responseMessage, osgpException);
            } else {
                LOGGER.error(
                        "No osgpException, yet dataObject ({}) is not of the regular type for handling response: {}",
                        responseMessage.getDataObject() == null ? null : responseMessage.getDataObject().getClass()
                                .getName(), messageType);

                this.handleError(new TechnicalException(ComponentType.DOMAIN_SMART_METERING,
                        "Unexpected response data handling request.", null), correlationUid,
                        organisationIdentification, deviceIdentification, messageType);
            }

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }

    /**
     * The {@code dataObject} in the {@code responseMessage} can either have a
     * value that would normally be returned as an answer, or it can contain an
     * object that was used in the request message (or other unexpected value).
     * <p>
     * The object from the request message is sometimes returned as object in
     * the response message to allow retries of requests without other knowledge
     * of what was sent earlier.
     * <p>
     * To filter out these, or other unexpected situations that may occur in the
     * future, each message processor is supposed to check the response message
     * for expected types of data objects.
     *
     * @param responseMessage
     * @return {@code true} if {@code responseMessage} contains a
     *         {@code dataObject} that can be processed normally; {@code false}
     *         otherwise.
     */
    protected abstract boolean hasRegularResponseObject(final ResponseMessage responseMessage);

    protected abstract void handleMessage(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessage responseMessage,
            final OsgpException osgpException);

    /**
     * In case of an error, this function can be used to send a response
     * containing the exception to the web-service-adapter.
     *
     * @param e
     *            The exception.
     * @param correlationUid
     *            The correlation UID.
     * @param organisationIdentification
     *            The organisation identification.
     * @param deviceIdentification
     *            The device identification.
     * @param messageType
     *            The message type.
     */
    protected void handleError(final Exception e, final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final String messageType) {
        LOGGER.info("handeling error: {} for message type: {}", e.getMessage(), messageType);
        final OsgpException osgpException = this.ensureOsgpException(e);
        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, ResponseMessageResultType.NOT_OK, osgpException, null), messageType);
    }

    private OsgpException ensureOsgpException(final Exception e) {

        if (e instanceof OsgpException) {
            return (OsgpException) e;
        }

        return new TechnicalException(ComponentType.DOMAIN_SMART_METERING,
                "Unexpected exception while retrieving response message", e);
    }
}
