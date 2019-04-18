/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.responses;

import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public class DeviceResponse {

    private final MessageMetadata messageMetadata;

    public DeviceResponse(final MessageMetadata messageMetadata) {
        this.messageMetadata = messageMetadata;
    }

    public String getOrganisationIdentification() {
        return this.messageMetadata.getOrganisationIdentification();
    }

    public String getDeviceIdentification() {
        return this.messageMetadata.getDeviceIdentification();
    }

    public String getCorrelationUid() {
        return this.messageMetadata.getCorrelationUid();
    }

    public int getMessagePriority() {
        return this.messageMetadata.getMessagePriority();
    }
}
