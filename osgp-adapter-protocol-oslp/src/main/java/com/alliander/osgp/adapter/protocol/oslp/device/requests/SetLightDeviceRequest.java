package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import java.util.List;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.LightValue;

public class SetLightDeviceRequest extends DeviceRequest {

    private List<LightValue> lightValues;

    public SetLightDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final List<LightValue> lightValues) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.lightValues = lightValues;
    }

    public List<LightValue> getLightValues() {
        return this.lightValues;
    }
}
