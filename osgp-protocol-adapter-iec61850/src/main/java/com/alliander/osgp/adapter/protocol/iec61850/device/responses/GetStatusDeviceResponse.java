/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.responses;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.dto.valueobjects.DeviceStatus;

public class GetStatusDeviceResponse extends DeviceResponse {

    private DeviceStatus deviceStatus;

    public GetStatusDeviceResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid) {
        super(organisationIdentification, deviceIdentification, correlationUid);
    }

    public DeviceStatus getDeviceStatus() {
        return this.deviceStatus;
    }

    public void setDeviceStatus(final DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}
