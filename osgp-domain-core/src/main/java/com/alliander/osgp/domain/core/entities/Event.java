/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

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
    private static final long serialVersionUID = 5498420959323116237L;

    @ManyToOne()
    @JoinColumn()
    private Device device;

    @Column(nullable = false, name = "event")
    private EventType eventType;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "\"index\"")
    private Integer index;

    public Event() {
        // Default constructor
    }

    /**
     * Constructor.
     *
     * @param device
     * @param eventType
     * @param description
     * @param index
     */
    public Event(final Device device, final EventType eventType, final String description, final Integer index) {
        this.device = device;
        this.eventType = eventType;
        this.description = description;
        this.index = index;
    }

    public Device getDevice() {
        return this.device;
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
        if (this.device != null ? !this.device.equals(other.device) : other.device != null) {
            return false;
        }
        if (this.eventType != null ? !this.eventType.equals(other.eventType) : other.eventType != null) {
            return false;
        }
        if (this.description != null ? !this.description.equals(other.description) : other.description != null) {
            return false;
        }
        if (this.index != null ? !this.index.equals(other.index) : other.index != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.device != null ? this.device.hashCode() : 0;
        result = 31 * result + (this.eventType != null ? this.eventType.hashCode() : 0);
        result = 31 * result + (this.description != null ? this.description.hashCode() : 0);
        result = 31 * result + (this.index != null ? this.index.hashCode() : 0);
        return result;
    }
}
