/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class DlmsDevice extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 3899692163578950343L;

    @Column(unique = true, nullable = false, length = 40)
    private String deviceIdentification;

    public DlmsDevice() {
        // Default constructor
    }

    public DlmsDevice(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final DlmsDevice device = (DlmsDevice) o;

        if (this.deviceIdentification != null ? !this.deviceIdentification.equals(device.deviceIdentification)
                : device.deviceIdentification != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int result = 31 * (this.deviceIdentification != null ? this.deviceIdentification.hashCode() : 0);
        return result;
    }
}
