//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.server;

public enum SecurityLevel {
  NO_SECURITY(0),
  LLS1(1),
  HLS5(5);

  private final int level;

  SecurityLevel(final int level) {
    this.level = level;
  }

  public int getLevel() {
    return this.level;
  }

  public static SecurityLevel fromString(final String level) {
    try {
      return SecurityLevel.fromNumber(Integer.parseInt(level));
    } catch (final NumberFormatException e) {
      throw new IllegalArgumentException(String.format("security level %s not supported", level));
    }
  }

  public static SecurityLevel fromNumber(final int level) {
    switch (level) {
      case 0:
        return NO_SECURITY;
      case 1:
        return LLS1;
      case 5:
        return HLS5;
      default:
        throw new IllegalArgumentException(
            String.format("security level %s not supported yet", level));
    }
  }
}
