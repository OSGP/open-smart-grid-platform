/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import com.alliander.osgp.domain.core.valueobjects.EventType;

public class EventBuilder {
    private Device device;
    private EventType eventType;
    private String description;
    private Integer index;

    public EventBuilder() {
        this.device = null;
        this.eventType = EventType.DIAG_EVENTS_GENERAL;
        this.description = null;
        this.index = null;
    }

    public EventBuilder withDevice(final Device device) {
        this.device = device;
        return this;
    }

    public EventBuilder withEventType(final EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public EventBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public EventBuilder withIndex(final Integer index) {
        this.index = index;
        return this;
    }

    public Event build() {
        return new Event(this.device, this.eventType, this.description, this.index);
    }
}