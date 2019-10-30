/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

public class DomainInfo {

    private final String domain;
    private final String domainVersion;

    public DomainInfo(final String domain, final String domainVersion) {
        this.domain = domain;
        this.domainVersion = domainVersion;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getDomainVersion() {
        return this.domainVersion;
    }

    @Override
    public String toString() {
        return "DomainInfo [domain=" + this.domain + ", domainVersion=" + this.domainVersion + "]";
    }
}
