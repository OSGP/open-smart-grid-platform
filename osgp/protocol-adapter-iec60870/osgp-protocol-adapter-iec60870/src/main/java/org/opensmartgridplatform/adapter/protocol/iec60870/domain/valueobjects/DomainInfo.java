/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import java.util.Objects;

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
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DomainInfo)) {
      return false;
    }
    final DomainInfo other = (DomainInfo) obj;
    return Objects.equals(this.domain, other.domain)
        && Objects.equals(this.domainVersion, other.domainVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.domain, this.domainVersion);
  }

  @Override
  public String toString() {
    return "DomainInfo [domain=" + this.domain + ", domainVersion=" + this.domainVersion + "]";
  }
}
