/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

public class DomainInformation {

  final String domain;
  final String domainVersion;

  public DomainInformation(final String domain, final String domainVersion) {
    this.domain = domain;
    this.domainVersion = domainVersion;
  }

  public String getDomain() {
    return this.domain;
  }

  public String getDomainVersion() {
    return this.domainVersion;
  }
}
