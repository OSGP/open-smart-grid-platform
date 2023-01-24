/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

/** Class for util methods dealing with universally unique identifiers (UUIDs). */
public class UuidUtil {

  private UuidUtil() {
    // hide implicit public constructor
  }

  /**
   * @return a byte array representing a random UUID
   */
  public static byte[] getBytesFromRandomUuid() {
    return getBytesFromUUID(UUID.randomUUID());
  }

  /**
   * @param uuid the UUID to generate the byte array with
   * @return a byte array representing the UUID given as a parameter
   */
  public static byte[] getBytesFromUUID(final UUID uuid) {
    final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());

    return bb.array();
  }
}
