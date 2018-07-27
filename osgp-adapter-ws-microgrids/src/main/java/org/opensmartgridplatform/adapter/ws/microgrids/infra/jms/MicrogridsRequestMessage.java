/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.microgrids.infra.jms;

import java.io.Serializable;

import org.joda.time.DateTime;

import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class MicrogridsRequestMessage extends RequestMessage {

    private static final long serialVersionUID = -1212784820197545944L;

    private final MicrogridsRequestMessageType messageType;

    public MicrogridsRequestMessage(final MicrogridsRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final Serializable request,
            final DateTime scheduleTime) {
        super(correlationUid, organisationIdentification, deviceIdentification, request);
        this.messageType = messageType;
    }

    public MicrogridsRequestMessageType getMessageType() {
        return this.messageType;
    }
}
