/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class Event extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5987663923796632312L;

    @ManyToOne()
    @JoinColumn()
    private Device device;

    @Column(nullable = false)
    private Date dateTime;

    @Column(nullable = false, name = "event")
    private EventType eventType;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "\"index\"")
    private Integer index;

    public Event() {
        // Default constructor
    }

    public Event(final Device device, final Date dateTime, final EventType eventType, final String description,
            final Integer index) {
        this.device = device;
        this.dateTime = dateTime;
        this.eventType = eventType;
        this.description = description;
        this.index = index;
    }

    public Device getDevice() {
        return this.device;
    }

    public Date getDateTime() {
        return this.dateTime;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getIndex() {
        return this.index;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        final Event other = (Event) o;
        final boolean isDeviceEqual = Objects.equals(this.device, other.device);
        final boolean isDateTimeEqual = Objects.equals(this.dateTime, other.dateTime);
        final boolean isEventTypeEqual = Objects.equals(this.eventType, other.eventType);
        final boolean isDescriptionEqual = Objects.equals(this.description, other.description);
        final boolean isIndexEqual = Objects.equals(this.index, other.index);

        return isDeviceEqual && isDateTimeEqual && isEventTypeEqual && isDescriptionEqual && isIndexEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.device, this.dateTime, this.eventType, this.description, this.index);
    }
}
