/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware.firmwarefile.enums;

import java.util.HashMap;
import java.util.Map;

public enum SecurityType {
  NO_SECURITY((byte) 0),
  SHA256((byte) 1),
  GMAC((byte) 2),
  CMAC((byte) 3),
  HMAC((byte) 4),
  ECDSA((byte) 5);

  private final byte code;
  private static final Map<Byte, SecurityType> map = new HashMap<>();

  static {
    for (final SecurityType securityType : SecurityType.values()) {
      map.put(securityType.code, securityType);
    }
  }

  private SecurityType(final byte code) {
    this.code = code;
  }

  public static SecurityType getByCode(final byte code) {
    final SecurityType type = map.get(code);
    if (type == null) {
      throw new IllegalArgumentException(
          String.format("No SecurityType found with code %d (byte)", code));
    }
    return type;
  }

  public byte getCode() {
    return this.code;
  }
}
