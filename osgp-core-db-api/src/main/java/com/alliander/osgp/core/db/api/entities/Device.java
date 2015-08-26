/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.db.api.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

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

    @ManyToOne
    @JoinTable(name = "device_authorization", joinColumns = @JoinColumn(name = "device"), inverseJoinColumns = @JoinColumn(name = "organisation"))
    private Organisation organisation;

    public Device() {
        // Default constructor
    }

    public Device(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public Device(final String deviceIdentification, final Organisation organisation) {
        this.deviceIdentification = deviceIdentification;
        this.organisation = organisation;
    }

    public Device(final String deviceIdentification, final Organisation organisation, final Float gpsLatitude,
            final Float gpsLongitude) {
        this.deviceIdentification = deviceIdentification;
        this.organisation = organisation;
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

    public Organisation getOrganisation() {
        return this.organisation;
    }
}
