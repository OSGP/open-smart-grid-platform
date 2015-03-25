package com.alliander.osgp.adapter.protocol.oslp.device.responses;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceMessageStatus;
import com.alliander.osgp.dto.valueobjects.PowerUsageData;

public class GetActualPowerUsageDeviceResponse extends EmptyDeviceResponse {

    private PowerUsageData actualPowerUsageData;

    public GetActualPowerUsageDeviceResponse(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final DeviceMessageStatus status,
            final PowerUsageData actualPowerUsageData) {
        super(organisationIdentification, deviceIdentification, correlationUid, status);
        this.actualPowerUsageData = actualPowerUsageData;
    }

    public PowerUsageData getActualPowerUsageData() {
        return this.actualPowerUsageData;
    }
}
