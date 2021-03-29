/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms;

import javax.jms.ObjectMessage;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "wsSmartMeteringOutboundDomainRequestsMessageSender")
public class SmartMeteringRequestMessageSender {

    @Autowired
    @Qualifier("wsSmartMeteringOutboundDomainRequestsJmsTemplate")
    private JmsTemplate jmsTemplate;

    public void send(final SmartMeteringRequestMessage requestMessage) {
        log.debug("Sending smart metering request message to the queue");

        if (requestMessage.getMessageType() == null) {
            log.error("MessageType is null");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getOrganisationIdentification())) {
            log.error("OrganisationIdentification is blank");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getDeviceIdentification())) {
            log.error("DeviceIdentification is blank");
            return;
        }
        if (StringUtils.isBlank(requestMessage.getCorrelationUid())) {
            log.error("CorrelationUid is blank");
            return;
        }

        this.sendMessage(requestMessage);
    }

    private void sendMessage(final SmartMeteringRequestMessage requestMessage) {
        log.info("Sending message to the smart metering requests queue");

        this.jmsTemplate.send(session -> {
            final ObjectMessage objectMessage = session.createObjectMessage(requestMessage.getRequest());
            objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
            objectMessage.setJMSType(requestMessage.getMessageType());
            objectMessage.setJMSPriority(requestMessage.getMessagePriority());
            objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                    requestMessage.getOrganisationIdentification());
            objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION, requestMessage.getDeviceIdentification());
            objectMessage.setBooleanProperty(Constants.BYPASS_RETRY, requestMessage.bypassRetry());

            if (requestMessage.getScheduleTime() != null) {
                objectMessage.setLongProperty(Constants.SCHEDULE_TIME, requestMessage.getScheduleTime());
            }
            return objectMessage;
        });
    }
}
