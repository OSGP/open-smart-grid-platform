/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class EventNotificationDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5665838352689024852L;

    private final String deviceUid;
    private final DateTime dateTime;
    private final EventTypeDto eventType;
    private final String description;
    private final Integer index;

    /**
     * Earlier constructor without the DateTime the event occurred. For lack of
     * a better default this will use {@code DateTime.now()} as the moment
     * registered with the event.
     *
     * @deprecated Use
     *             {@link #EventNotificationDto(String, DateTime, EventTypeDto, String, Integer)}
     *             if the time the event occurred is known.
     */
    @Deprecated
    public EventNotificationDto(final String deviceUid, final EventTypeDto eventType, final String description,
            final Integer index) {
        this(deviceUid, DateTime.now(), eventType, description, index);
    }

    public EventNotificationDto(final String deviceUid, final DateTime dateTime, final EventTypeDto eventType,
            final String description, final Integer index) {
        this.deviceUid = deviceUid;
        this.dateTime = dateTime;
        this.eventType = eventType;
        this.description = description;
        this.index = index;
    }

    @Override
    public String toString() {
        return String.format("EventNotificationDto[deviceUid=%s, dateTime=%s, eventType=%s, index=%s, description=%s]",
                this.deviceUid, this.dateTime, this.eventType, this.index, this.description);
    }

    // TODO: remove this one?
    public String getDeviceUid() {
        return this.deviceUid;
    }

    public DateTime getDateTime() {
        return this.dateTime;
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
