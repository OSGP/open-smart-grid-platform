/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import java.util.List;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.LightValue;

public class SetLightDeviceRequest extends DeviceRequest {

    private List<LightValue> lightValues;

    public SetLightDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final List<LightValue> lightValues) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.lightValues = lightValues;
    }

    public SetLightDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final List<LightValue> lightValues, final String domain,
            final String domainVersion, final String messageType, final String ipAddress, final int retryCount,
            final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);

        this.lightValues = lightValues;
    }

    public List<LightValue> getLightValues() {
        return this.lightValues;
    }
}
