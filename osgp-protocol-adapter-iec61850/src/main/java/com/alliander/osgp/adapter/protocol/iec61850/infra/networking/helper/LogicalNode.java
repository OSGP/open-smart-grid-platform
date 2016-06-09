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
    /**
     * LLN0, configuration Logical Node zero.
     */
    LOGICAL_NODE_ZERO("LLN0"),
    /**
     * CSLC, configuration Logical Node.
     */
    STREET_LIGHT_CONFIGURATION("CSLC"),
    /**
     * XSWC1, relays number 1 Logical Node.
     */
    SWITCH_COMPONENT_ONE("XSWC1"),
    /**
     * XSWC2, relays number 1 Logical Node.
     */
    SWITCH_COMPONENT_TWO("XSWC2"),
    /**
     * XSWC3, relays number 1 Logical Node.
     */
    SWITCH_COMPONENT_THREE("XSWC3"),
    /**
     * XSWC4, relays number 1 Logical Node.
     */
    SWITCH_COMPONENT_FOUR("XSWC4");

    private final String description;

    private LogicalNode(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Get the name of a relay Logical Node by index/number.
     *
     * @param index
     *            The index/number of the relay.
     *
     * @return The name of a relay Logical Node.
     */
    public static LogicalNode getSwitchComponentByIndex(final int index) {
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
