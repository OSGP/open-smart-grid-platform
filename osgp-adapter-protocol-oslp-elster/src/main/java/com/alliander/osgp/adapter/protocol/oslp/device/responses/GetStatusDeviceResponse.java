/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.device.responses;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;
import com.alliander.osgp.dto.valueobjects.DeviceStatus;

public class GetStatusDeviceResponse extends DeviceResponse {

    private DeviceStatus deviceStatus;

    public GetStatusDeviceResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final DeviceStatus deviceStatus) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.deviceStatus = deviceStatus;
    }

    public DeviceStatus getDeviceStatus() {
        return this.deviceStatus;
    }
}
