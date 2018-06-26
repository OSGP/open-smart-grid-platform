/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * An entity class which contains the information of a single relay of a device.
 */
@Entity
public class RelayStatus extends AbstractEntity {

    private static final long serialVersionUID = -6288672019209482063L;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column
    private int index;

    @Column
    private boolean lastKnownState;

    @Column
    private Date lastKnowSwitchingTime;

    public RelayStatus() {
        // Default constructor
    }

    public RelayStatus(final Device device, final int index, final boolean lastKnownState,
            final Date lastKnowSwitchingTime) {
        this.device = device;
        this.index = index;
        this.lastKnownState = lastKnownState;
        this.lastKnowSwitchingTime = lastKnowSwitchingTime;
    }

    public void updateStatus(final boolean lastKnownState, final Date lastKnowSwitchingTime) {
        this.lastKnownState = lastKnownState;
        this.lastKnowSwitchingTime = lastKnowSwitchingTime;
    }

    public boolean isLastKnownState() {
        return this.lastKnownState;
    }

    public void setLastKnownState(final boolean lastKnownState) {
        this.lastKnownState = lastKnownState;
    }

    public Date getLastKnowSwitchingTime() {
        return this.lastKnowSwitchingTime;
    }

    public void setLastKnowSwitchingTime(final Date lastKnowSwitchingTime) {
        this.lastKnowSwitchingTime = lastKnowSwitchingTime;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelayStatus)) {
            return false;
        }
        final RelayStatus relayStatus = (RelayStatus) o;
        final boolean isDeviceEqual = Objects.equals(this.device, relayStatus.device);
        final boolean isIndexEqual = Objects.equals(this.index, relayStatus.index);

        return isDeviceEqual && isIndexEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.device, this.index);
    }

    @Override
    public String toString() {
        return String.format("index: %d, lastKnownState: %s, lastKnownSwitchingTime: %s", this.index,
                this.lastKnownState, Instant.ofEpochMilli(this.lastKnowSwitchingTime.getTime()).toString());
    }
}
