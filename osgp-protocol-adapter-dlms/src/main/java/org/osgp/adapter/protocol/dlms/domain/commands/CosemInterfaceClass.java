/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

public enum CosemInterfaceClass {

    REGISTER(3),
    EXTENDED_REGISTER(4),
    DEMAND_REGISTER(5),
    PROFILE_GENERIC(7),
    CLOCK(8);

    private int id;

    private CosemInterfaceClass(int id) {
        this.id = id;
    }

    public int id() {
        return this.id;
    }

}
