/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging;

import java.io.Serializable;

import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

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
