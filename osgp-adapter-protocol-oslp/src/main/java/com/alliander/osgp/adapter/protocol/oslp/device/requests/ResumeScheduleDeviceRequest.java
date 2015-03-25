package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;

public class ResumeScheduleDeviceRequest extends DeviceRequest {

    private Integer index;
    private boolean immediate;

    public ResumeScheduleDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final Integer index, final boolean immediate) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.index = index;
        this.immediate = immediate;
    }

    public Integer getIndex() {
        return this.index;
    }

    public boolean isImmediate() {
        return this.immediate;
    }
}
