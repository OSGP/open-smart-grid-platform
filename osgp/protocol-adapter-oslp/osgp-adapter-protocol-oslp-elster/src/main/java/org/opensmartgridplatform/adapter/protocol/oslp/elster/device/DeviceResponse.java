/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device;

public class DeviceResponse {

    private final String organisationIdentification;
    private final String deviceIdentification;
    private final String correlationUid;
    private final int messagePriority;

    public DeviceResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final int messagePriority) {
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.correlationUid = correlationUid;
        this.messagePriority = messagePriority;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public int getMessagePriority() {
        return this.messagePriority;
    }
}
