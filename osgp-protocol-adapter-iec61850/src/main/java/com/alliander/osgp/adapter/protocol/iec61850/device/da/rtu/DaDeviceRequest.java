/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.da.rtu;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;

import java.io.Serializable;

public class DaDeviceRequest extends DeviceRequest {

    private Serializable request;

    public DaDeviceRequest(final String organisationIdentification, final String deviceIdentification,
                           final String correlationUid, final Serializable request) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.request = request;
    }

    public DaDeviceRequest(final String organisationIdentification, final String deviceIdentification,
                           final String correlationUid, final Serializable request, final String domain,
                           final String domainVersion, final String messageType, final String ipAddress, final int retryCount,
                           final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);

        this.request = request;
    }

    public Serializable getRequest() {
        return this.request;
    }
}
