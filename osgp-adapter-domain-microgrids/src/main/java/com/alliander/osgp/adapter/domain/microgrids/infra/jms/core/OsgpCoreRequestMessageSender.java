/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.microgrids.infra.jms.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

//Send request message to the requests queue of OSGP Core.
@Component(value = "domainMicrogridsOutgoingOsgpCoreRequestMessageSender")
public class OsgpCoreRequestMessageSender {

    @Autowired
    @Qualifier("domainMicrogridsOutgoingOsgpCoreRequestsJmsTemplate")
    private JmsTemplate osgpCoreRequestsJmsTemplate;

    public void send(final RequestMessage requestMessage, final String messageType, final String ipAddress) {
        this.send(requestMessage, messageType, ipAddress, null);
    }

    public void send(final RequestMessage requestMessage, final String messageType, final String ipAddress,
            final Long scheduleTime) {

        this.osgpCoreRequestsJmsTemplate
                .send(new OsgpCoreRequestMessageCreator(requestMessage, messageType, ipAddress, scheduleTime));
    }
}
