package com.alliander.osgp.adapter.protocol.oslp.device.responses;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;

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
