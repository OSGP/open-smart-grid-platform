/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.ws;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.alliander.osgp.adapter.domain.tariffswitching.infra.jms.core.OsgpCoreRequestMessageSender;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.MessageProcessor;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

/**
 * Base class for MessageProcessor implementations. Each MessageProcessor
 * implementation should be annotated with @Component. Further the MessageType
 * the MessageProcessor implementation can process should be passed in at
 * construction. The Singleton instance is added to the HashMap of
 * MessageProcessors after dependency injection has completed.
 */
public abstract class WebServiceRequestMessageProcessor implements MessageProcessor {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceRequestMessageProcessor.class);

    /**
     * This is the message sender needed for the message processor implementations
     * to handle the forwarding of messages to OSGP-CORE.
     */
    @Qualifier("domainTariffSwitchingOutgoingOsgpCoreRequestMessageSender")
    @Autowired
    protected OsgpCoreRequestMessageSender coreRequestMessageSender;

    /**
     * This is the message sender needed for the message processor implementation to
     * handle an error.
     */
    @Autowired
    protected WebServiceResponseMessageSender webServiceResponseMessageSender;

    /**
     * The hash map of message processor instances.
     */
    @Autowired
    protected WebServiceRequestMessageProcessorMap webServiceRequestMessageProcessorMap;

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
    protected WebServiceRequestMessageProcessor(final DeviceFunction deviceFunction) {
        this.deviceFunction = deviceFunction;
    }

    /**
     * Initialization function executed after dependency injection has finished. The
     * MessageProcessor Singleton is added to the HashMap of MessageProcessors. The
     * key for the HashMap is the integer value of the enumeration member.
     */
    @PostConstruct
    public void init() {
        this.webServiceRequestMessageProcessorMap.addMessageProcessor(this.deviceFunction.ordinal(),
                this.deviceFunction.name(), this);
    }

    /**
     * In case of an error, this function can be used to send a response containing
     * the exception to the web-service-adapter.
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
        LOGGER.error("Handling error for message type: " + messageType, e);
        OsgpException osgpException = null;
        if (e instanceof OsgpException) {
            osgpException = (OsgpException) e;
        } else {
            osgpException = new TechnicalException(ComponentType.DOMAIN_TARIFF_SWITCHING, "An unknown error occurred",
                    e);
        }
        ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(correlationUid).withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withResult(ResponseMessageResultType.NOT_OK)
                .withOsgpException(osgpException).withDataObject(e).build();
        this.webServiceResponseMessageSender.send(responseMessage);
    }
}
