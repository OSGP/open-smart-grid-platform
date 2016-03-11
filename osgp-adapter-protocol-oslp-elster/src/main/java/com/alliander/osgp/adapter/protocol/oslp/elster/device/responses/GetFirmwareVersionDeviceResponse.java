/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.device.responses;

import com.alliander.osgp.adapter.protocol.oslp.elster.device.DeviceResponse;

public class GetFirmwareVersionDeviceResponse extends DeviceResponse {

    private String firmwareVersion;

    public GetFirmwareVersionDeviceResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String firmwareVersion) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.firmwareVersion = firmwareVersion;
    }

    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }
}
