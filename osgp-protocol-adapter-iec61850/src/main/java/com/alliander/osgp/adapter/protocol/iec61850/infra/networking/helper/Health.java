/**
 * Copyright 2014-2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Map IEC61850 health values (as byte) to the corresponding enum values (as String)
 */
public class Health {
    private static final Map<Byte, String> health;
    static {
        Map<Byte, String> map = new HashMap<>();
        map.put((byte) 1, "OK");
        map.put((byte) 2, "WARNING");
        map.put((byte) 3, "ALARM");
        health = map;
    }

    public static String fromByte(byte value) {
        return health.get(value);
    }
}
