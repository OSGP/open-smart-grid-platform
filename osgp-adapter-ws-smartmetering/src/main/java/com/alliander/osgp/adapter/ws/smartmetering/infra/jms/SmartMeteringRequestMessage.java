/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.jms;

import java.io.Serializable;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class SmartMeteringRequestMessage extends RequestMessage {

    private static final long serialVersionUID = 8978488633831083383L;

    private final SmartMeteringRequestMessageType messageType;

    public SmartMeteringRequestMessage(final SmartMeteringRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final Serializable request) {
        this(messageType, correlationUid, organisationIdentification, deviceIdentification, null, request);
    }

    public SmartMeteringRequestMessage(final SmartMeteringRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final String ipAddress,
            final Serializable request) {
        super(correlationUid, organisationIdentification, deviceIdentification, ipAddress, request);
        this.messageType = messageType;
    }

    public SmartMeteringRequestMessageType getMessageType() {
        return this.messageType;
    }
}
