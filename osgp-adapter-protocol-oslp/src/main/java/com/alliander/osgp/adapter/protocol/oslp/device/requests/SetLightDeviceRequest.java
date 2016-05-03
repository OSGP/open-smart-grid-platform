/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.LightValueMessageDataContainerDto;

public class SetLightDeviceRequest extends DeviceRequest {

    private LightValueMessageDataContainerDto lightValuesContainer;

    public SetLightDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final LightValueMessageDataContainerDto lightValuesContainer) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.lightValuesContainer = lightValuesContainer;
    }

    public SetLightDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final LightValueMessageDataContainerDto lightValuesContainer,
            final String domain, final String domainVersion, final String messageType, final String ipAddress,
            final int retryCount, final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);

        this.lightValuesContainer = lightValuesContainer;
    }

    public LightValueMessageDataContainerDto getLightValuesContainer() {
        return this.lightValuesContainer;
    }
}
