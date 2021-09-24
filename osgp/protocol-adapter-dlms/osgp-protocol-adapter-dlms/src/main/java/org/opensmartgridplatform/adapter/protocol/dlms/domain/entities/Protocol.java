/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

public enum Protocol {
  DSMR_4_2_2("DSMR", "4.2.2", true),
  SMR_5_0_0("SMR", "5.0.0", true),
  SMR_5_1("SMR", "5.1", true),
  OTHER_PROTOCOL("?", "?", true);

  private final String name;
  private final String version;
  private final boolean selectValuesInSelectiveAccessSupported;

  Protocol(
      final String name,
      final String version,
      final boolean selectValuesInSelectiveAccessSupported) {
    this.name = name;
    this.version = version;
    this.selectValuesInSelectiveAccessSupported = selectValuesInSelectiveAccessSupported;
  }

  public String getName() {
    return this.name;
  }

  public String getVersion() {
    return this.version;
  }

  public boolean isSelectValuesInSelectiveAccessSupported() {
    return this.selectValuesInSelectiveAccessSupported;
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

  public boolean isSmr5() {
    return this.version.startsWith("5.");
  }
}
