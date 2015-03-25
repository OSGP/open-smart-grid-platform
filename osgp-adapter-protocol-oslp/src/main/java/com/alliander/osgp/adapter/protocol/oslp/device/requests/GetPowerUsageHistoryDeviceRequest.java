package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.HistoryTermType;
import com.alliander.osgp.dto.valueobjects.TimePeriod;

public class GetPowerUsageHistoryDeviceRequest extends DeviceRequest {

    private TimePeriod timePeriod;
    private HistoryTermType historyTermType;

    public GetPowerUsageHistoryDeviceRequest(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final TimePeriod timePeriod,
            final HistoryTermType historyTermType) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.timePeriod = timePeriod;
        this.historyTermType = historyTermType;
    }

    public TimePeriod getTimePeriod() {
        return this.timePeriod;
    }

    public HistoryTermType getHistoryTermType() {
        return this.historyTermType;
    }
}
