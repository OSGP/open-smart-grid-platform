/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.device;

public class DeviceResponse {

    private String organisationIdentification;
    private String deviceIdentification;
    private String correlationUid;

    public DeviceResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid) {
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.correlationUid = correlationUid;
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
}
