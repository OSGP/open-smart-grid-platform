package com.alliander.osgp.adapter.ws.admin.infra.jms;

import java.io.Serializable;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class AdminRequestMessage extends RequestMessage {
    private AdminRequestMessageType messageType;

    public AdminRequestMessage(final AdminRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final Serializable ovlRequest) {
        super(correlationUid, organisationIdentification, deviceIdentification, ovlRequest);

        this.messageType = messageType;
    }

    public AdminRequestMessageType getMessageType() {
        return this.messageType;
    }
}
