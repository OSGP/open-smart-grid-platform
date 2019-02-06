/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.device.responses;

import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceResponse;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public class EmptyDeviceResponse extends DeviceResponse {

    private final DeviceMessageStatus status;

    public EmptyDeviceResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final int messagePriority, final DeviceMessageStatus status) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
        this.status = status;
    }

    public EmptyDeviceResponse(final MessageMetadata messageMetadata, final DeviceMessageStatus status) {
        super(messageMetadata.getOrganisationIdentification(), messageMetadata.getDeviceIdentification(),
                messageMetadata.getCorrelationUid(), messageMetadata.getMessagePriority());
        this.status = status;
    }

    public DeviceMessageStatus getStatus() {
        return this.status;
    }
}
