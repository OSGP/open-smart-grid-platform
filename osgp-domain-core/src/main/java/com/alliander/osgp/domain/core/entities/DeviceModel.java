/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * DeviceModel entity class holds information about the device model or type
 */
@Entity
public class DeviceModel extends AbstractEntity implements Comparable<DeviceModel> {

    private static final long serialVersionUID = 7957241305474770350L;

    @ManyToOne()
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @Column(nullable = false, length = 255)
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

    public DeviceModel(final Manufacturer manufacturer, final String modelCode, final String description,
            final boolean metered) {
        this.manufacturer = manufacturer;
        this.modelCode = modelCode;
        this.description = description;
        // default behavior is true
        this.fileStorage = true;
        this.metered = metered;
    }

    public DeviceModel(final Manufacturer manufacturer, final String modelCode, final String description,
            final boolean fileStorage, final boolean metered) {
        this(manufacturer, modelCode, description, metered);
        this.fileStorage = fileStorage;
        this.metered = metered;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DeviceModel)) {
            return false;
        }
        final DeviceModel other = (DeviceModel) obj;
        return Objects.equals(this.modelCode, other.modelCode) && Objects.equals(this.manufacturer, other.manufacturer);
    }

    @Override
    public int compareTo(final DeviceModel o) {
        final int compareManufacturer = this.manufacturer.compareTo(o.manufacturer);
        if (compareManufacturer != 0) {
            return compareManufacturer;
        }
        return this.modelCode.compareTo(o.modelCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.modelCode, this.manufacturer);
    }

    @Override
    public String toString() {
        return String.format("DeviceModel[manufacturer=%s, code=%s]", this.manufacturer.getCode(), this.modelCode);
    }

    public void updateData(final String description, final boolean metered) {
        this.description = description;
        this.metered = metered;
    }

    public Manufacturer getManufacturer() {
        return this.manufacturer;
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
