/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.shared.infra.jms.MessageProcessorMap;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class DeviceRequestMessageProcessor implements MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageProcessor.class);

    @Autowired
    protected DeviceResponseMessageSender responseMessageSender;

    @Autowired
    @Qualifier("protocolDlmsDeviceRequestMessageProcessorMap")
    protected MessageProcessorMap dlmsRequestMessageProcessorMap;

    protected final DeviceRequestMessageType deviceRequestMessageType;

    /**
     * Each MessageProcessor should register it's MessageType at construction.
     *
     * @param deviceRequestMessageType
     *            The MessageType the MessageProcessor implementation can
     *            process.
     */
    protected DeviceRequestMessageProcessor(final DeviceRequestMessageType deviceRequestMessageType) {
        this.deviceRequestMessageType = deviceRequestMessageType;
    }

    /**
     * Initialization function executed after dependency injection has finished.
     * The MessageProcessor Singleton is added to the HashMap of
     * MessageProcessors. The key for the HashMap is the integer value of the
     * enumeration member.
     */
    @PostConstruct
    public void init() {
        this.dlmsRequestMessageProcessorMap.addMessageProcessor(this.deviceRequestMessageType.ordinal(),
                this.deviceRequestMessageType.name(), this);
    }

    /**
     * @param logger
     *            the logger from the calling subClass
     * @param exception
     *            the exception to be logged
     * @param messageMetadata
     *            a DlmsDeviceMessageMetadata containing debug info to be logged
     */
    protected void logJmsException(final Logger logger, final JMSException exception,
            final DlmsDeviceMessageMetadata messageMetadata) {
        logger.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", exception);
        logger.debug("correlationUid: {}", messageMetadata.getCorrelationUid());
        logger.debug("domain: {}", messageMetadata.getDomain());
        logger.debug("domainVersion: {}", messageMetadata.getDomainVersion());
        logger.debug("messageType: {}", messageMetadata.getMessageType());
        logger.debug("organisationIdentification: {}", messageMetadata.getOrganisationIdentification());
        logger.debug("deviceIdentification: {}", messageMetadata.getDeviceIdentification());

    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        LOGGER.debug("Processing {} request message", this.deviceRequestMessageType.name());
        final DlmsDeviceMessageMetadata messageMetadata = new DlmsDeviceMessageMetadata();

        try {
            // Handle message
            messageMetadata.handleMessage(message);
            final Serializable response = this.handleMessage(messageMetadata, message.getObject());

            // Send response
            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, this.responseMessageSender,
                    response);
        } catch (final ConnectionException exception) {
            // Retry / redeliver by throwing RuntimeException.
            throw exception;
        } catch (final JMSException exception) {
            this.logJmsException(LOGGER, exception, messageMetadata);
        } catch (final Exception exception) {
            // Return original request + exception
            LOGGER.error("Unexpected exception during {}", this.deviceRequestMessageType.name(), exception);

            final OsgpException ex = this.ensureOsgpException(exception);
            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, this.responseMessageSender,
                    message.getObject());
        }
    }

    abstract protected Serializable handleMessage(final DlmsDeviceMessageMetadata messageMetadata,
            final Serializable requestObject) throws OsgpException, ProtocolAdapterException;

    protected OsgpException ensureOsgpException(final Exception e) {
        if (e instanceof OsgpException) {
            final Throwable cause = e.getCause();
            if (cause != null && !(cause instanceof OsgpException)) {
                return new OsgpException(ComponentType.PROTOCOL_DLMS, e.getMessage(), new OsgpException(
                        ComponentType.PROTOCOL_DLMS, cause.getMessage()));
            }

            return (OsgpException) e;
        }

        return new TechnicalException(ComponentType.PROTOCOL_DLMS,
                "Unexpected exception while handling protocol request/response message", new OsgpException(
                        ComponentType.PROTOCOL_DLMS, e.getMessage()));
    }

    protected void sendResponseMessage(final DlmsDeviceMessageMetadata messageMetadata,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender, final Serializable responseObject) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(messageMetadata.getDomain(),
                messageMetadata.getDomainVersion(), messageMetadata.getMessageType(),
                messageMetadata.getCorrelationUid(), messageMetadata.getOrganisationIdentification(),
                messageMetadata.getDeviceIdentification(), result, osgpException, responseObject,
                messageMetadata.getRetryCount());

        responseMessageSender.send(responseMessage);
    }

    protected void sendResponseMessage(final DlmsDeviceMessageMetadata messageMetadata,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender) {

        this.sendResponseMessage(messageMetadata, result, osgpException, responseMessageSender, null);
    }
}
