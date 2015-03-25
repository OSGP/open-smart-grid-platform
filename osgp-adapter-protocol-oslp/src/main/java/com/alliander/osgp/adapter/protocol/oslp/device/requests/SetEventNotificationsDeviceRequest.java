package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import java.util.List;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.EventNotificationType;

public class SetEventNotificationsDeviceRequest extends DeviceRequest {

    private List<EventNotificationType> eventNotifications;

    public SetEventNotificationsDeviceRequest(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid,
            final List<EventNotificationType> eventNotifications) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.eventNotifications = eventNotifications;
    }

    public List<EventNotificationType> getEventNotifications() {
        return this.eventNotifications;
    }
}
