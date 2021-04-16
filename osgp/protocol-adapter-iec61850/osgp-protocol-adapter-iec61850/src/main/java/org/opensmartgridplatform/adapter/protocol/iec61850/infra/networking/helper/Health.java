/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import java.util.HashMap;
import java.util.Map;

/** Map IEC61850 health values (as byte) to the corresponding enum values (as String) */
public final class Health {
  private static final Map<Byte, String> MAPPING = new HashMap<>();

  static {
    MAPPING.put((byte) 1, "OK");
    MAPPING.put((byte) 2, "WARNING");
    MAPPING.put((byte) 3, "ALARM");
  }

  private Health() {
    // Hide public constructor for class with only static methods
  }

  public static String fromByte(final byte value) {
    return MAPPING.get(value);
  }
}
