/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.domain.core.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Manufacturer entity class
 */
@Entity
public class Manufacturer implements Serializable {

    private static final long serialVersionUID = 4267096558283350606L;

    @Id
    @Column(nullable = false, length = 4)
    private String manufacturerId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column
    private boolean usePrefix;

    public Manufacturer() {
        // Default constructor
    }

    public Manufacturer(final String manufacturerId, final String name, final boolean usePrefix) {
        this.manufacturerId = manufacturerId;
        this.name = name;
        this.usePrefix = usePrefix;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isUsePrefix() {
        return this.usePrefix;
    }

    public void setUsePrefix(final boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    public String getManufacturerId() {
        return this.manufacturerId;
    }

    public void setManufacturerId(final String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

}
