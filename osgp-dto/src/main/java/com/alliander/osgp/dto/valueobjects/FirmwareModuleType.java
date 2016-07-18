/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

public enum FirmwareModuleType {

    COMMUNICATION("Communication"),
    FUNCTIONAL("Functional"),
    SECURITY("Security"),
    M_BUS("M-bus"),
    MODULE_ACTIVE("Module active");

    private final String description;

    private FirmwareModuleType(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
