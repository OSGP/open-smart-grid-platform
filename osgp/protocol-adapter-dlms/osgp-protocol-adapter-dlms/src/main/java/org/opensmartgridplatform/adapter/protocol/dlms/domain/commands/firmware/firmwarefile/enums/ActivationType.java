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

public enum ActivationType {
  IMMEDIATE_ACTIVATION((byte) 1),
  TIMED_ACTIVATION((byte) 2),
  MASTER_INITIATED_ACTIVATION((byte) 3);

  private final byte code;
  private static final Map<Byte, ActivationType> map = new HashMap<>();

  static {
    for (final ActivationType activationType : ActivationType.values()) {
      map.put(activationType.code, activationType);
    }
  }

  private ActivationType(final byte code) {
    this.code = code;
  }

  public static ActivationType getByCode(final byte code) {
    final ActivationType type = map.get(code);
    if (type == null) {
      throw new IllegalArgumentException(
          String.format("No ActivationType found with code %d (byte)", code));
    }
    return type;
  }

  public byte getCode() {
    return this.code;
  }
}
