/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

public enum MbusAttributes {

    IDENTIFICATION_NUMBER("IDENTIFICATION_NUMBER"), 
    MANUFACTURER_ID("MANUFACTURER_ID"), 
    VERSION("VERSION"), 
    DEVICE_TYPE("DEVICE_TYPE");

    private final String description;

    private MbusAttributes(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
