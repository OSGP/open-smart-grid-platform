/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.device.responses;

import java.util.List;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponse;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;

public class GetFirmwareVersionDeviceResponse extends DeviceResponse {

    private List<FirmwareVersionDto> firmwareVersions;

    public GetFirmwareVersionDeviceResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final List<FirmwareVersionDto> firmwareVersions) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.firmwareVersions = firmwareVersions;
    }

    public List<FirmwareVersionDto> getFirmwareVersions() {
        return this.firmwareVersions;
    }

}
