/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.tariffswitching.infra.jms;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class TariffSwitchingRequestMessage extends RequestMessage {

    private final TariffSwitchingRequestMessageType messageType;
    private final DateTime scheduleTime;

    public TariffSwitchingRequestMessage(final TariffSwitchingRequestMessageType messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final Serializable request,final DateTime scheduleTime) {
        super(correlationUid, organisationIdentification, deviceIdentification, request);
        this.messageType = messageType;
        this.scheduleTime=scheduleTime;
    }

    public TariffSwitchingRequestMessageType getMessageType() {
        return this.messageType;
    }
    
    public DateTime getScheduleTime() {
        return this.scheduleTime;
    }
}
