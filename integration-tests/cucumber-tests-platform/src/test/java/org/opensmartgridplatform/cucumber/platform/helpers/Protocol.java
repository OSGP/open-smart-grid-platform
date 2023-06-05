// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.helpers;

public class Protocol {

  private final ProtocolType type;
  private final String protocol;
  private final String version;

  public Protocol(final ProtocolType type, final String protocol, final String version) {
    this.type = type;
    this.protocol = protocol;
    this.version = version;
  }

  public ProtocolType getType() {
    return this.type;
  }

  public String getProtocol() {
    return this.protocol;
  }

  public String getVersion() {
    return this.version;
  }

  public enum ProtocolType {
    OSLP,
    DSMR,
    DLMS,
    IEC60870;
  }
}
