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

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@Entity
public class Ean extends AbstractEntity {

    private static final long serialVersionUID = 2569469187462546946L;

    @ManyToOne()
    @JoinColumn()
    private Device device;

    @Column(nullable = false)
    private String code;

    @Column()
    private String description;

    public Ean() {
        // Default constructor
    }

    /**
     * Constructor.
     *
     * @param device
     * @param code
     * @param description
     */
    public Ean(final Device device, final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    public Device getDevice() {
        return this.device;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        final Ean other = (Ean) o;
        if (this.device != null ? !this.device.getDeviceIdentification().equals(other.device.getDeviceIdentification())
                : other.device != null) {
            return false;
        }
        if (this.code != null ? !this.code.equals(other.code) : other.code != null) {
            return false;
        }
        if (this.description != null ? !this.description.equals(other.description) : other.description != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.code != null ? this.code.hashCode() : 0;
        result = 31 * result + (this.description != null ? this.description.hashCode() : 0);
        result = 31 * result + (this.device != null ? this.device.hashCode() : 0);
        return result;
    }
}
