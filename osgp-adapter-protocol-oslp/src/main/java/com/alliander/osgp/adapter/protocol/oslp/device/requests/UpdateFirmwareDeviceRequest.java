package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;

public class UpdateFirmwareDeviceRequest extends DeviceRequest {

    private String firmwareDomain;
    private String firmwareUrl;

    public UpdateFirmwareDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final String firmwareDomain, final String firmwareUrl) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.firmwareDomain = firmwareDomain;
        this.firmwareUrl = firmwareUrl;
    }

    public String getFirmwareDomain() {
        return this.firmwareDomain;
    }

    public String getFirmwareUrl() {
        return this.firmwareUrl;
    }
}
