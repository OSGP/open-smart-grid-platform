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
 * Manufacturer entitiy class
 */
@Entity
public class Manufacturer implements Serializable {

    private static final long serialVersionUID = 4267096558283350606L;

    @Id
    @Column(nullable = false, length = 4)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    public Manufacturer() {
        // Default constructor
    }

    public Manufacturer(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
