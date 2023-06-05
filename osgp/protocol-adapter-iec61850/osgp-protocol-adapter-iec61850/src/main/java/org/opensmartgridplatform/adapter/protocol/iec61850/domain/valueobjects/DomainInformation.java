// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
