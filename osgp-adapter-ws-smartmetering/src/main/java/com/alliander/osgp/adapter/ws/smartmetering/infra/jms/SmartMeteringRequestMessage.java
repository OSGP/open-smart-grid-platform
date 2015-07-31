/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.jms;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class SmartMeteringRequestMessage extends RequestMessage {

    private final SmartMeteringRequestMessageType messageType;
    private final DateTime scheduleTime;
    private final String deviceType;

    public SmartMeteringRequestMessage(final SmartMeteringRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final Serializable request,
            final DateTime scheduleTime, final String deviceType) {
        super(correlationUid, organisationIdentification, deviceIdentification, request);
        this.messageType = messageType;
        this.scheduleTime = scheduleTime;
        this.deviceType = deviceType;
    }

    public SmartMeteringRequestMessageType getMessageType() {
        return this.messageType;
    }

    public DateTime getScheduleTime() {
        return this.scheduleTime;
    }

    public String getDeviceType() {
        return this.deviceType;
    }
}
