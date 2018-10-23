/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class RtuDevice extends Device {

    private static final long serialVersionUID = -5356111084356341244L;

    @Column(insertable = false)
    private Date lastCommunicationTime;

    public RtuDevice() {
        // No-arg constructor for frameworks.
    }

    public RtuDevice(final String deviceIdentification) {
        super(deviceIdentification);
    }

    public void messageReceived() {
        this.lastCommunicationTime = new Date();
    }

    public Date getLastCommunicationTime() {
        return this.lastCommunicationTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final RtuDevice device = (RtuDevice) o;
        return Objects.equals(this.deviceIdentification, device.deviceIdentification);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.deviceIdentification);
    }
}
