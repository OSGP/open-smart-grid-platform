/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects;

/**
 * An Enum used to filter on Device managed externally or internally.
 */
public enum DeviceExternallyManagedFilterType {
    BOTH(null),
    EXTMGT(true),
    INTMGT(false);

    private Boolean value;

    private DeviceExternallyManagedFilterType(final Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return this.value;
    }
}
