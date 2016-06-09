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
    LOGICAL_DEVICE("SWDeviceGenericIO");

    private String description;

    private LogicalDevice(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
