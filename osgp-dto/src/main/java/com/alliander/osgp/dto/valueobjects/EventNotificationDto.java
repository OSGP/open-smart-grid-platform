/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class EventNotificationDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5665838352689024852L;

    private final String deviceUid;
    private final EventTypeDto eventType;
    private final String description;
    private final Integer index;

    public EventNotificationDto(final String deviceUid, final EventTypeDto eventType, final String description,
            final Integer index) {
        this.deviceUid = deviceUid;
        this.eventType = eventType;
        this.description = description;
        this.index = index;
    }

    // TODO: remove this one?
    public String getDeviceUid() {
        return this.deviceUid;
    }

    public EventTypeDto getEventType() {
        return this.eventType;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getIndex() {
        return this.index;
    }
}
