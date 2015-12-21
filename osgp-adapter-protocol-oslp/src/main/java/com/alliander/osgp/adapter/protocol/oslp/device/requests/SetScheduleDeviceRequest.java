/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.device.requests;

import com.alliander.osgp.adapter.protocol.oslp.device.DeviceRequest;
import com.alliander.osgp.dto.valueobjects.RelayType;
import com.alliander.osgp.dto.valueobjects.ScheduleMessageDataContainer;

public class SetScheduleDeviceRequest extends DeviceRequest {

    private ScheduleMessageDataContainer scheduleMessageDataContainer;
    private RelayType relayType;

    public SetScheduleDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final ScheduleMessageDataContainer scheduleMessageDataContainer,
            final RelayType relayType) {
        super(organisationIdentification, deviceIdentification, correlationUid);
        this.scheduleMessageDataContainer = scheduleMessageDataContainer;
        this.relayType = relayType;
    }

    public SetScheduleDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final ScheduleMessageDataContainer scheduleMessageDataContainer,
            final RelayType relayType, final String domain, final String domainVersion, final String messageType,
            final String ipAddress, final int retryCount, final boolean isScheduled) {
        super(organisationIdentification, deviceIdentification, correlationUid, domain, domainVersion, messageType,
                ipAddress, retryCount, isScheduled);
        this.scheduleMessageDataContainer = scheduleMessageDataContainer;
        this.relayType = relayType;
    }

    public RelayType getRelayType() {
        return this.relayType;
    }

    public ScheduleMessageDataContainer getScheduleMessageDataContainer() {
        return this.scheduleMessageDataContainer;
    }
}
