package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import java.util.List;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.RelayType;
import com.alliander.osgp.dto.valueobjects.Schedule;

public class SetScheduleDeviceRequest extends DeviceRequest {

    private List<Schedule> schedules;
    private RelayType relayType;

    public SetScheduleDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final List<Schedule> schedules, final RelayType relayType) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.schedules = schedules;
        this.relayType = relayType;
    }

    public List<Schedule> getSchedules() {
        return this.schedules;
    }

    public RelayType getRelayType() {
        return this.relayType;
    }
}
