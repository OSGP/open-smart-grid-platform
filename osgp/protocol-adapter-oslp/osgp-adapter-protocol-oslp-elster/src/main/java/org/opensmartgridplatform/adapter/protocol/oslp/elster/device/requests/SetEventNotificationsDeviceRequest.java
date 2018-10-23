/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationMessageDataContainerDto;

public class SetEventNotificationsDeviceRequest extends DeviceRequest {

    private final EventNotificationMessageDataContainerDto eventNotificationsContainer;

    public SetEventNotificationsDeviceRequest(final String organisationIdentification,
            final String deviceIdentification, final String correlationUid, final int messagePriority,
            final EventNotificationMessageDataContainerDto eventNotificationsContainer) {
        super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
        this.eventNotificationsContainer = eventNotificationsContainer;
    }

    public SetEventNotificationsDeviceRequest(final Builder deviceRequestBuilder,
            final EventNotificationMessageDataContainerDto eventNotificationsContainer) {
        super(deviceRequestBuilder);
        this.eventNotificationsContainer = eventNotificationsContainer;
    }

    public EventNotificationMessageDataContainerDto getEventNotificationsContainer() {
        return this.eventNotificationsContainer;
    }
}
