/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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

    public SetTransitionDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final TransitionType transitionType, final DateTime transitionTime,
            final String domain, final String domainVersion, final String messageType, final String ipAddress,
            final int retryCount, final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);

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
