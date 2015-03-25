package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import org.joda.time.DateTime;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.TransitionType;

public class SetTransitionDeviceRequest extends DeviceRequest {

    private TransitionType transitionType;
    private DateTime transitionTime;

    public SetTransitionDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final TransitionType transitionType, final DateTime transitionTime) {
        super(organisationIdentification, deviceIdentification, correlationUid);

        this.transitionType = transitionType;
        this.transitionTime = transitionTime;
    }

    public TransitionType getTransitionType() {
        return this.transitionType;
    }

    public DateTime getTransitionTime() {
        return this.transitionTime;
    }
}
