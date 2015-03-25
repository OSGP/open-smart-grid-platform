package com.alliander.osgp.adapter.protocol.oslp.device.responses;

import java.util.List;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceMessageStatus;
import com.alliander.osgp.dto.valueobjects.PowerUsageData;

public class GetPowerUsageHistoryDeviceResponse extends EmptyDeviceResponse {

    private List<PowerUsageData> powerUsageHistoryData;

    public GetPowerUsageHistoryDeviceResponse(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final DeviceMessageStatus status,
            final List<PowerUsageData> powerUsageHistoryData) {
        super(organisationIdentification, deviceIdentification, correlationUid, status);
        this.powerUsageHistoryData = powerUsageHistoryData;
    }

    public List<PowerUsageData> getPowerUsageHistoryData() {
        return this.powerUsageHistoryData;
    }
}
