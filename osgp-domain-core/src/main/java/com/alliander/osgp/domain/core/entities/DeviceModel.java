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

    private static final long serialVersionUID = -834369362882020365L;

    @ManyToOne()
    @JoinColumn(name = "manufacturer_id")
    private Manufacturer manufacturer;

    @Column(nullable = false, length = 15)
    private String modelCode;

    @Column(nullable = false, length = 255)
    private String description;

    public DeviceModel(final Manufacturer manufacturer, final String modelCode, final String description) {
        this.manufacturer = manufacturer;
        this.modelCode = modelCode;
        this.description = description;
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

}
