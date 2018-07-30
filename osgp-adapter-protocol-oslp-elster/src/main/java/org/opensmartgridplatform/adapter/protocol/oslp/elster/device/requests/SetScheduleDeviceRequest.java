/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.dto.valueobjects.ScheduleMessageDataContainerDto;

public class SetScheduleDeviceRequest extends DeviceRequest {

    private final ScheduleMessageDataContainerDto scheduleMessageDataContainer;
    private final RelayTypeDto relayType;

    public SetScheduleDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final int messagePriority,
            final ScheduleMessageDataContainerDto scheduleMessageDataContainer, final RelayTypeDto relayType) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
        this.scheduleMessageDataContainer = scheduleMessageDataContainer;
        this.relayType = relayType;
    }

    public SetScheduleDeviceRequest(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final ScheduleMessageDataContainerDto scheduleMessageDataContainer,
            final RelayTypeDto relayType, final String domain, final String domainVersion, final String messageType,
            final int messagePriority, final String ipAddress, final int retryCount, final boolean isScheduled) {
        super(DeviceRequest.newBuilder().organisationIdentification(organisationIdentification)
                .deviceIdentification(deviceIdentification).correlationUid(correlationUid).domain(domain)
                .domainVersion(domainVersion).messageType(messageType).messagePriority(messagePriority)
                .ipAddress(ipAddress).retryCount(retryCount).isScheduled(isScheduled));
        this.scheduleMessageDataContainer = scheduleMessageDataContainer;
        this.relayType = relayType;
    }

    public SetScheduleDeviceRequest(final Builder deviceRequestBuilder,
            final ScheduleMessageDataContainerDto scheduleMessageDataContainer, final RelayTypeDto relayType) {
        super(deviceRequestBuilder);
        this.scheduleMessageDataContainer = scheduleMessageDataContainer;
        this.relayType = relayType;
    }

    public ScheduleMessageDataContainerDto getScheduleMessageDataContainer() {
        return this.scheduleMessageDataContainer;
    }

    public RelayTypeDto getRelayType() {
        return this.relayType;
    }
}
