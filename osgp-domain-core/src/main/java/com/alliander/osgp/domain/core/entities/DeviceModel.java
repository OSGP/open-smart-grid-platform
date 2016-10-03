/**
 * Copyright 2016 Smart Society Services B.V.
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

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * DeviceModel entity class holds information about the device model or type
 */
@Entity
public class DeviceModel extends AbstractEntity {

    private static final long serialVersionUID = 7957241305474770350L;

    @ManyToOne()
    @JoinColumn()
    private Manufacturer manufacturerId;

    @Column(nullable = false, length = 15)
    private String modelCode;

    @Column(length = 255)
    private String description;

    @Column
    private boolean fileStorage;

    @Column
    private boolean metered;

    public DeviceModel() {
        // Default constructor
    }

    public DeviceModel(final Manufacturer manufacturerId, final String modelCode, final String description,
            final boolean metered) {
        this.manufacturerId = manufacturerId;
        this.modelCode = modelCode;
        this.description = description;
        // default behaviour is true
        this.fileStorage = true;
        this.metered = metered;
    }

    public DeviceModel(final Manufacturer manufacturerId, final String modelCode, final String description,
            final boolean fileStorage, final boolean metered) {
        this(manufacturerId, modelCode, description, metered);
        this.fileStorage = fileStorage;
        this.metered = metered;
    }

    public void updateData(final String description, final boolean metered) {
        this.description = description;
        this.metered = metered;
    }

    public Manufacturer getManufacturerId() {
        return this.manufacturerId;
    }

    public String getDescription() {
        return this.description;
    }

    public String getModelCode() {
        return this.modelCode;
    }

    public boolean isFileStorage() {
        return this.fileStorage;
    }

    public boolean isMetered() {
        return this.metered;
    }
}
