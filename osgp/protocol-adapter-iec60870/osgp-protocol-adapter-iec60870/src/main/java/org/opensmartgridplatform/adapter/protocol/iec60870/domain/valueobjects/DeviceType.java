/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

public enum DeviceType {
    DA_DEVICE(DomainType.DISTRIBUTION_AUTOMATION),
    LIGHT_MEASUREMENT_GATEWAY(DomainType.PUBLIC_LIGHTING),
    LIGHT_MEASUREMENT_DEVICE(DomainType.PUBLIC_LIGHTING);

    private DomainType domainType;

    private DeviceType(final DomainType domainType) {
        this.domainType = domainType;
    }

    public DomainType domainType() {
        return this.domainType;
    }
}
