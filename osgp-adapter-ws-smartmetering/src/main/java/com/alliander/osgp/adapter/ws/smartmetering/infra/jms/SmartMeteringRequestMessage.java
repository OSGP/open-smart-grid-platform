package com.alliander.osgp.adapter.ws.smartmetering.infra.jms;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class SmartMeteringRequestMessage extends RequestMessage {

    private final SmartMeteringRequestMessageType messageType;
    private final DateTime scheduleTime;

    public SmartMeteringRequestMessage(final SmartMeteringRequestMessageType messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final Serializable request,final DateTime scheduleTime) {
        super(correlationUid, organisationIdentification, deviceIdentification, request);
        this.messageType = messageType;
        this.scheduleTime=scheduleTime;
    }

    public SmartMeteringRequestMessageType getMessageType() {
        return this.messageType;
    }
    
    public DateTime getScheduleTime() {
        return this.scheduleTime;
    }
}
