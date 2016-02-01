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
    @JoinColumn(name = "manufacturer")
    private Manufacturer manufacturer;

    @Column(nullable = false, length = 10)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    public DeviceModel(final Manufacturer manufacturer, final String code, final String name) {
        super();
        this.manufacturer = manufacturer;
        this.code = code;
        this.name = name;
    }

    public Manufacturer getManufacturer() {
        return this.manufacturer;
    }

    /**
     * used by jpa
     */
    @SuppressWarnings("unused")
    private void setManufacturer(final Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCode() {
        return this.code;
    }

    /**
     * used by jpa
     */
    @SuppressWarnings("unused")
    private void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    /**
     * used by jpa
     */
    @SuppressWarnings("unused")
    private void setName(final String name) {
        this.name = name;
    }

}
