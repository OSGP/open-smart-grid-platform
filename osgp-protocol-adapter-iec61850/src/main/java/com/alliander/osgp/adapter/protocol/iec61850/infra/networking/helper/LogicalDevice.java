/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

/**
 * Contains the name of the Logical Device.
 */
public enum LogicalDevice {
    /**
     * The name of the Logical Device.
     */
    LIGHTING("IO"),
    /**
     * Logical Device RTU 1
     */
    RTU_ONE("RTU1"),
    /**
     * Logical Device Photovoltaic 1
     */
    PV_ONE("PV1"),
    /**
     * Logical Device Photovoltaic 2
     */
    PV_TWO("PV2"),
    /**
     * Logical Device Photovoltaic 3
     */
    PV_THREE("PV3"),
    /**
     * Logical Device Battery 1
     */
    BATTERY_ONE("BATTERY1"),
    /**
     * Logical Device Battery 2
     */
    BATTERY_TWO("BATTERY2"),
    /**
     * Logical Device Engine 1
     */
    ENGINE_ONE("ENGINE1"),
    /**
     * Logical Device Engine 1
     */
    ENGINE_TWO("ENGINE2"),
    /**
     * Logical Device Engine 1
     */
    ENGINE_THREE("ENGINE3"),
    /**
     * Logical Device Load 1
     */
    LOAD_ONE("LOAD1");

    private String description;

    private LogicalDevice(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static LogicalDevice fromString(final String description) {

        if (description != null) {
            for (final LogicalDevice ld : LogicalDevice.values()) {
                if (description.equalsIgnoreCase(ld.description)) {
                    return ld;
                }
            }
        }
        throw new IllegalArgumentException("No LogicalDevice constant with description " + description + " found.");
    }
}
