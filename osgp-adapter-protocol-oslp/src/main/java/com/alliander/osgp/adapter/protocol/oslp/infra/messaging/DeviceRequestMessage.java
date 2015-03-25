package com.alliander.osgp.adapter.protocol.oslp.infra.messaging;

import java.io.Serializable;

import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class DeviceRequestMessage extends RequestMessage {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8265058052684751357L;

    private DeviceRequestMessageType messageType;

    public DeviceRequestMessage(final DeviceRequestMessageType messageType, final String correlationUid,
            final String organisationIdentification, final String deviceIdentification, final Serializable ovlRequest) {
        super(correlationUid, organisationIdentification, deviceIdentification, ovlRequest);

        this.messageType = messageType;
    }

    public DeviceRequestMessageType getMessageType() {
        return this.messageType;
    }
}
