/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
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
public abstract class OsgpCoreRequestMessageProcessor implements MessageProcessor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpCoreRequestMessageProcessor.class);

    /**
     * This is the message sender needed for the message processor
     * implementations to handle the forwarding of messages to WS-SmartMetering.
     * <p>
     * Requests from OSGP-Core (which should be notifications not based on an
     * earlier request) are handled as the existing notification responses (that
     * do belong with earlier requests) from here. In this way, the system
     * handling the notifications does not have to make a distinction in the way
     * of handling these notification types.
     */
    @Autowired
    protected WebServiceResponseMessageSender webServiceResponseMessageSender;

    /**
     * The hash map of message processor instances.
     */
    @Autowired
    protected OsgpCoreRequestMessageProcessorMap osgpCoreRequestMessageProcessorMap;

    /**
     * The message type that a message processor implementation can handle.
     */
    protected DeviceFunction deviceFunction;

    /**
     * Construct a message processor instance by passing in the message type.
     *
     * @param deviceFunction
     *            The message type a message processor can handle.
     */
    protected OsgpCoreRequestMessageProcessor(final DeviceFunction deviceFunction) {
        this.deviceFunction = deviceFunction;
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors. The key for the HashMap is the integer value of the
     * enumeration member.
     */
    @PostConstruct
    public void init() {
        this.osgpCoreRequestMessageProcessorMap.addMessageProcessor(this.deviceFunction.ordinal(),
                this.deviceFunction.name(), this);
    }

    protected abstract void handleMessage(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Object dataObject, final String messageType) throws FunctionalException;

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        Object dataObject = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            dataObject = message.getObject();

        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }

        try {
            LOGGER.info("Calling application service function: {}", messageType);
            this.handleMessage(organisationIdentification, deviceIdentification, correlationUid, dataObject,
                    messageType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }

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
        LOGGER.info("handling error: {} for message type: {}", e.getMessage(), messageType);
        final OsgpException osgpException = this.ensureOsgpException(e);
        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, ResponseMessageResultType.NOT_OK, osgpException, null), messageType);
    }

    private OsgpException ensureOsgpException(final Exception e) {

        if (e instanceof OsgpException) {
            return (OsgpException) e;
        }

        return new TechnicalException(ComponentType.DOMAIN_SMART_METERING,
                "Unexpected exception while retrieving OSGP-Core request message", e);
    }
}
