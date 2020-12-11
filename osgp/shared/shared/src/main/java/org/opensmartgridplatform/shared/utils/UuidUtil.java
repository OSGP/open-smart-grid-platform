/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UuidUtil {

    private UuidUtil() {
        // hide implicit public constructor
    }

    public static byte[] getBytesFromRandomUuid() {
        return getBytesFromUUID(UUID.randomUUID());
    }

    public static byte[] getBytesFromUUID(final UUID uuid) {
        final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

}
