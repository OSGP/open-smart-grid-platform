/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

public enum DomainType {
    DISTRIBUTION_AUTOMATION(new DomainInfo("DISTRIBUTION_AUTOMATION", "1.0")),
    PUBLIC_LIGHTING(new DomainInfo("PUBLIC_LIGHTING", "1.0"));

    private DomainInfo domainInfo;

    private DomainType(final DomainInfo domainInfo) {
        this.domainInfo = domainInfo;
    }

    public DomainInfo domainInfo() {
        return this.domainInfo;
    }
}
