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

public enum AddressType {
  MBUS_ADDRESS((byte) 1);

  private final byte code;
  private static final Map<Byte, AddressType> map = new HashMap<>();

  static {
    for (final AddressType securityType : AddressType.values()) {
      map.put(securityType.code, securityType);
    }
  }

  private AddressType(final byte code) {
    this.code = code;
  }

  public static AddressType getByCode(final byte code) {
    final AddressType type = map.get(code);
    if (type == null) {
      throw new IllegalArgumentException(
          String.format("No AddressType found with code %d (byte)", code));
    }
    return type;
  }

  public byte getCode() {
    return this.code;
  }
}
