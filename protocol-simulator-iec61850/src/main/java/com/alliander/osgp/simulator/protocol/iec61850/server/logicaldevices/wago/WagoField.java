/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.wago;

public enum WagoField {
    FIELD0("MMXU1"),
    FIELD1("MMXU2");

    private final String id;

    WagoField(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

}