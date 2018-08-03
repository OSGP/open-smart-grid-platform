/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.infra.jms.ws;

import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ObjectMessage;

// Send response message to the web service adapter.
public class WebServiceResponseMessageSender implements NotificationResponseMessageSender {

    @Autowired
    @Qualifier("domainDistributionAutomationOutgoingWebServiceResponsesJmsTemplate")
    private JmsTemplate webServiceResponsesJmsTemplate;

    /**
     * Send a response message to the web service adapter using a custom time to
     * live.
     *
     * @param responseMessage
     *            The response message to send.
     * @param timeToLive
     *            The custom time to live value in milliseconds.
     */
    public void send(final ResponseMessage responseMessage, final Long timeToLive, final String messageType) {

        // Keep the original time to live from configuration.
        final Long originalTimeToLive = this.webServiceResponsesJmsTemplate.getTimeToLive();
        if (timeToLive != null) {
            // Set the custom time to live.
            this.webServiceResponsesJmsTemplate.setTimeToLive(timeToLive);
        }

        this.webServiceResponsesJmsTemplate.send( session -> {
            final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
            objectMessage.setJMSCorrelationID(responseMessage.getCorrelationUid());
            objectMessage.setJMSType(messageType);
            objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                    responseMessage.getOrganisationIdentification());
            objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                    responseMessage.getDeviceIdentification());
            objectMessage.setStringProperty(Constants.RESULT, responseMessage.getResult().toString());
            if (responseMessage.getOsgpException() != null) {
                objectMessage.setStringProperty(Constants.DESCRIPTION,
                        responseMessage.getOsgpException().getMessage());
            }
            return objectMessage;
        } );

        if (timeToLive != null) {
            // Restore the time to live from the configuration.
            this.webServiceResponsesJmsTemplate.setTimeToLive(originalTimeToLive);
        }
    }

    @Override
    public void send(final ResponseMessage responseMessage, final String messageType) {
        this.send(responseMessage, null, messageType);
    }
}
