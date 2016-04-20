/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

/**
 * Contains a list of all Logical nodes of the IEC61850 Device
 */
public enum LogicalNode {

    STREET_LIGHT_CONFIGURATION("CSLC"),
    SWITCH_COMPONENT_ONE("XSWC1"),
    SWITCH_COMPONENT_TWO("XSWC2"),
    SWITCH_COMPONENT_THREE("XSWC3"),
    SWITCH_COMPONENT_FOUR("XSWC4");

    private final String description;

    private LogicalNode(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public LogicalNode getSwitchComponentByIndex(final int index) {
        switch (index) {
        case 1:
            return SWITCH_COMPONENT_ONE;
        case 2:
            return SWITCH_COMPONENT_TWO;
        case 3:
            return SWITCH_COMPONENT_THREE;
        case 4:
            return SWITCH_COMPONENT_FOUR;
        default:
            throw new IllegalArgumentException("Invalid index value : " + index);
        }
    }

}
