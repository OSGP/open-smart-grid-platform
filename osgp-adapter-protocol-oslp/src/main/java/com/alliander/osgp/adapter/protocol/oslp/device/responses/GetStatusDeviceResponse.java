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
