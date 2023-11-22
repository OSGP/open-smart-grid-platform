// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

public enum Protocol {
  DSMR_2_2("DSMR", "2.2"),
  DSMR_4_2_2("DSMR", "4.2.2"),
  SMR_4_3("SMR", "4.3"),
  SMR_5_0_0("SMR", "5.0.0"),
  SMR_5_1("SMR", "5.1"),
  SMR_5_2("SMR", "5.2"),
  SMR_5_5("SMR", "5.5"),
  OTHER_PROTOCOL("?", "?");

  private final String name;
  private final String version;

  Protocol(final String name, final String version) {
    this.name = name;
    this.version = version;
  }

  public String getName() {
    return this.name;
  }

  public String getVersion() {
    return this.version;
  }

  public static Protocol forDevice(final DlmsDevice device) {
    return withNameAndVersion(device.getProtocolName(), device.getProtocolVersion());
  }

  public static Protocol withNameAndVersion(
      final String protocolName, final String protocolVersion) {
    for (final Protocol protocol : Protocol.values()) {
      if (protocol.name.equals(protocolName) && protocol.version.equals(protocolVersion)) {
        return protocol;
      }
    }
    // If no defined protocol matches, then don't throw exception, but return OTHER_PROTOCOL as
    // default.
    // This is preferred because the adapter is a general DLMS adapter.
    return OTHER_PROTOCOL;
  }

  public boolean isDsmr2() {
    return this.version.startsWith("2.");
  }

  public boolean isDsmr4() {
    return this.version.startsWith("4.");
  }

  public boolean isSmr5() {
    return this.version.startsWith("5.");
  }
}
