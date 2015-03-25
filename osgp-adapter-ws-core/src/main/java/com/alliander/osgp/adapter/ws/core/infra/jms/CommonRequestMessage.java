package com.alliander.osgp.adapter.ws.core.infra.jms;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class CommonRequestMessage extends RequestMessage {
    private final CommonRequestMessageType messageType;
    private final DateTime scheduleTime;

    public CommonRequestMessage(final CommonRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final Serializable ovlRequest,
            final DateTime scheduleTime) {
        super(correlationUid, organisationIdentification, deviceIdentification, ovlRequest);

        this.messageType = messageType;
        this.scheduleTime = scheduleTime;
    }

    public CommonRequestMessageType getMessageType() {
        return this.messageType;
    }

    public DateTime getScheduleTime() {
        return this.scheduleTime;
    }
}
