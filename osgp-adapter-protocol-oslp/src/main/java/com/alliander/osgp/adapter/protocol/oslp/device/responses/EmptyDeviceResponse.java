package com.alliander.osgp.adapter.protocol.oslp.device.responses;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.oslp.device.DeviceResponse;

public class EmptyDeviceResponse extends DeviceResponse {

    private DeviceMessageStatus status;

    public EmptyDeviceResponse(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final DeviceMessageStatus status) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.status = status;
    }

    public DeviceMessageStatus getStatus() {
        return this.status;
    }
}
