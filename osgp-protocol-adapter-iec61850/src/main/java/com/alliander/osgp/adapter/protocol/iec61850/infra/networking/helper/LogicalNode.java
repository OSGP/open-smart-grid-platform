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
     * LPHD1, configuration Physical Device Node one.
     */
    PHYSICAL_DEVICE_ONE("LPHD1"),
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
    SWITCH_COMPONENT_FOUR("XSWC4"),
    /**
     * DGEN1, DER Generator Logical Node.
     */
    GENERATOR_ONE("DGEN1"),
    /**
     * ZBAT1, Logical Node containing Battery System characteristics
     */
    BATTERY_ONE("ZBAT1"),
    /**
     * MMXU1, Measurements Logical Node.
     */
    MEASUREMENT_ONE("MMXU1"),
    /**
     * MMXU2, Measurements Logical Node.
     */
    MEASUREMENT_TWO("MMXU2"),
    /**
     * MMXU3, Measurements Logical Node.
     */
    MEASUREMENT_THREE("MMXU3"),
    /**
     * MMXU4, Measurements Logical Node.
     */
    MEASUREMENT_FOUR("MMXU4"),
    /**
     * MMXU5, Measurements Logical Node.
     */
    MEASUREMENT_FIVE("MMXU5"),
    /**
     * MMTR1, Logical Node Meter Reading
     */
    METER_READING_ONE("MMTR1"),
    /**
     * MMTR2, Logical Node Meter Reading
     */
    METER_READING_TWO("MMTR2"),
    /**
     * MMTR3, Logical Node Meter Reading
     */
    METER_READING_THREE("MMTR3"),
    /**
     * MMTR4, Logical Node Meter Reading
     */
    METER_READING_FOUR("MMTR4"),
    /**
     * MMTR5, Logical Node Meter Reading
     */
    METER_READING_FIVE("MMTR5"),
    /**
     * DRCT, DER Controller characteristics;
     */
    DER_CONTROLLER_CHARACTERISTICS_ONE("DRCT1"),
    /**
     * DRCC, DER Supervisory Control
     */
    DER_SUPERVISORY_CONTROL_ONE("DRCC1");

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

    public static LogicalNode fromString(final String description) {

        if (description != null) {
            for (final LogicalNode ln : LogicalNode.values()) {
                if (description.equalsIgnoreCase(ln.description)) {
                    return ln;
                }
            }
        }
        throw new IllegalArgumentException("No LogicalNode constant with description " + description + " found.");
    }
}
