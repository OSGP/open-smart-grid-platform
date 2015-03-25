package com.alliander.osgp.adapter.ws.publiclighting.infra.jms;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class PublicLightingRequestMessage extends RequestMessage {

    private final PublicLightingRequestMessageType messageType;
    private final DateTime scheduleTime;

    public PublicLightingRequestMessage(final PublicLightingRequestMessageType messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final Serializable request,final DateTime scheduleTime) {
        super(correlationUid, organisationIdentification, deviceIdentification, request);
        this.messageType = messageType;
        this.scheduleTime=scheduleTime;
    }

    public PublicLightingRequestMessageType getMessageType() {
        return this.messageType;
    }

    public DateTime getScheduleTime() {
        return this.scheduleTime;
    }
}
