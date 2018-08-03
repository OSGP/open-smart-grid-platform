/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class Device extends AbstractEntity {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4272375057090210869L;

    @Column(unique = true, nullable = false, length = 40)
    private String deviceIdentification;

    @Column
    private Float gpsLatitude;
    @Column
    private Float gpsLongitude;

    public Device() {
        // Default constructor
    }

    public Device(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public Device(final String deviceIdentification, final Float gpsLatitude, final Float gpsLongitude) {
        this.deviceIdentification = deviceIdentification;
        this.gpsLatitude = gpsLatitude;
        this.gpsLongitude = gpsLongitude;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public Float getGpsLatitude() {
        return this.gpsLatitude;
    }

    public Float getGpsLongitude() {
        return this.gpsLongitude;
    }
}
