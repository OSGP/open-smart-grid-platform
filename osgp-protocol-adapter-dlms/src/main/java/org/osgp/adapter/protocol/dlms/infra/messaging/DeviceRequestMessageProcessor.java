/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;

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

    protected void handleError(final Exception e, final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final String domain, final String domainVersion,
            final String messageType, final int retryCount) {
        LOGGER.error("Error while processing message", e);
        final OsgpException ex = new TechnicalException(ComponentType.UNKNOWN,
                "Unexpected exception while retrieving response message", e);

        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage(domain, domainVersion,
                messageType, correlationUid, organisationIdentification, deviceIdentification,
                ResponseMessageResultType.NOT_OK, ex, null, retryCount);

        this.responseMessageSender.send(protocolResponseMessage);
    }

    protected void handleError(final Exception e, final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final String domain, final String domainVersion, final String messageType) {
        LOGGER.error("Error while processing message", e);
        final OsgpException ex = new TechnicalException(ComponentType.UNKNOWN,
                "Unexpected exception while retrieving response message", e);

        final ProtocolResponseMessage protocolResponseMessage = new ProtocolResponseMessage(domain, domainVersion,
                messageType, correlationUid, organisationIdentification, deviceIdentification,
                ResponseMessageResultType.NOT_OK, ex, null);

        this.responseMessageSender.send(protocolResponseMessage);
    }

    /**
     * @param logger
     *            the logger from the calling subClass
     * @param exception
     *            the exception to be logged
     * @param device
     *            a DlmsMessagingDevice containing debug info to be logged
     */
    protected void logJmsException(final Logger logger, final JMSException exception, final DlmsMessagingDevice device) {
        logger.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", exception);
        logger.debug("correlationUid: {}", device.getCorrelationUid());
        logger.debug("domain: {}", device.getDomain());
        logger.debug("domainVersion: {}", device.getDomainVersion());
        logger.debug("messageType: {}", device.getMessageType());
        logger.debug("organisationIdentification: {}", device.getOrganisationIdentification());
        logger.debug("deviceIdentification: {}", device.getDeviceIdentification());

    }
}
